package biz.nable.sb.cor.comp.service.impl;

import java.util.List;
import java.util.Optional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import biz.nable.sb.cor.common.bean.CommonSearchBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.comp.bean.*;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.*;
import biz.nable.sb.cor.comp.request.BlockRequest;
import biz.nable.sb.cor.comp.request.DeleteUserRequest;
import biz.nable.sb.cor.comp.response.ApprovalPendingUserResponse;
import biz.nable.sb.cor.comp.response.UserListResponseByUserID;
import biz.nable.sb.cor.comp.response.UserResponseList;
import biz.nable.sb.cor.comp.thirdparty.GetUserDetailsResponse;
import biz.nable.sb.cor.comp.thirdparty.GroupsDetails;
import biz.nable.sb.cor.comp.utility.*;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.component.UserTempComponent;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {
	private static final String COMMON_USER_GROUP = "SYSTEM";

	@Autowired
	CommonConverter commonConverter;

	@Autowired
	BranchMstRepository branchMstRepository;

	@Autowired
	UserMstRepository userMstRepository;

	@Autowired
	BranchDeleteRepository branchDeleteRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BranchTempComponent branchTempComponent;

	@Autowired
	UserTempComponent userTempComponent;

	@Autowired
	FeaturesRepository featuresRepository;

	@Autowired
	CompanyFeaturesRepository companyFeaturesRepository;

    @Value("${get.user.details.url}")
    private String getUserDetailsURL;

	private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.USER;

	private static final String USER_HASH_TAG = "COMPANY_ID=";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse createTempUser(CreateUserRequest createUserRequest, String userGroup,
			String requestId,String adminUser) {
		logger.info("================== Start Create User Request =================");
		logger.info("Create User {} to {}", createUserRequest.getUserName(), createUserRequest.getPrimaryCompanyId());
		CommonResponse commonResponse = new CommonResponse();
		CommonRequestBean commonRequestBean ;
		createUserRequest.setCreateDate(new Date());
		createUserRequest.setCreateBy(adminUser);
		createUserRequest.setUserGroup(userGroup);
        commonRequestBean = setCommonRequestBean(userGroup, requestId, createUserRequest);
		logger.info("Create commonRequestBean");
		CommonResponseBean commonResponseBean = userTempComponent.createTempRecord(commonRequestBean, requestId);
		commonResponse.setErrorCode(commonResponseBean.getErrorCode());
		commonResponse.setReturnCode(commonResponseBean.getReturnCode());
		commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
		logger.info("================== End Create User Request =================");
		return commonResponse;
	}


	public CommonResponse updateTempUser(CreateUserRequest createUserRequest, String userId, String userGroup,
										 String requestId,String adminUser) {
		logger.info("================== Start update user request=================");
		Optional<UserMst> optional = userMstRepository.findByUserId(Long.parseLong(userId));
		CommonResponse commonResponse = new CommonResponse();
		CommonRequestBean commonRequestBean = new CommonRequestBean();
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_USER_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
		}else {
            createUserRequest.setLastModifiedDate(new Date());
            createUserRequest.setLastModifiedBy(adminUser);
            createUserRequest.setUserId(optional.get().getUserId());
            String hashTags = USER_HASH_TAG.concat(String.valueOf(createUserRequest.getPrimaryCompanyId()));
            String referenceNo;
            if (createUserRequest.getPrimaryCompanyId() != null){
                referenceNo = requestId.concat("-").concat(createUserRequest.getPrimaryCompanyId());
            }else{
                referenceNo = requestId;
            }
            commonRequestBean.setCommonTempBean(createUserRequest);
            commonRequestBean.setHashTags(hashTags);
            commonRequestBean.setReferenceNo(referenceNo);
            commonRequestBean.setRequestType(REQUEST_TYPE.name());
            commonRequestBean.setUserGroup(userGroup);
            commonRequestBean.setUserId(userId);
            CommonResponseBean commonResponseBean = userTempComponent.updateTempUser(commonRequestBean, requestId);
            optional.get().setRecordStatus(RecordStatuUsersEnum.MODIFY_PENDING);
            userMstRepository.save(optional.get());
            commonResponse.setErrorCode(commonResponseBean.getErrorCode());
            commonResponse.setReturnCode(commonResponseBean.getReturnCode());
            commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
			logger.info("================== End update user request =================");
		}
		return commonResponse;
	}

	public ApprovalPendingUserResponse getPendingAuthUseres( String adminUserId, String userGroup, String approvalStatus) {
		logger.info("================== Start auth pending user request=================");
		ApprovalPendingUserResponse commonResponse = new ApprovalPendingUserResponse();
		CommonSearchBean bean = setCommonSearchBean(adminUserId,  userGroup, approvalStatus, REQUEST_TYPE);
		List<TempDto> tempResponseList = new ArrayList<>();
		try{
			tempResponseList = userTempComponent.getAuthPendingRecord(bean).getTempList();

			if (tempResponseList == null){
				logger.info(
						messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.NO_USER_RECORD_FOUND);
				commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
				commonResponse.setReturnMessage(
						messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			}
		}catch (Exception exception){
			logger.error("Error in get auth pending record: {}", exception.toString());
		}
		Set<Long> userReferenceList = new HashSet<>();
		Set<UserListResponseBean> userListResponseBeanSet = new HashSet<>();
		tempResponseList.forEach(tempDto -> {
			UserListResponseBean userListResponseBean =  new UserListResponseBean();
			try{
				userListResponseBean.setId(String.valueOf(tempDto.getId()));
				userListResponseBean.setApprovalId(tempDto.getApprovalId());
				userListResponseBean.setSignature(String.valueOf(tempDto.getSignature()));
				userListResponseBean.setRequestType(tempDto.getRequestType());
				userListResponseBean.setReferenceNo(String.valueOf(tempDto.getReferenceNo()));
				userListResponseBean.setActionType(tempDto.getActionType());
			}catch (Exception exception){
				logger.error("Error in copying tempDTO to userListResponseBean.: {}", exception.toString());
			}

			ModifiedUserResponse modifiedUserResponse = commonConverter.mapToPojo(tempDto.getRequestPayload(), ModifiedUserResponse.class);
			userListResponseBean.setModifiedUserResponseSet(modifiedUserResponse);
			if (!ActionTypeEnum.CREATE.equals(tempDto.getActionType())) {
				long userID = Long.parseLong(tempDto.getRequestPayload().get("userId").toString());
				userReferenceList.add(userID);
			}
			Optional.of(userReferenceList).ifPresent(userList -> {
				Set<UserMst> userMstSet = userMstRepository.findByUserIdIn(userReferenceList);
				userListResponseBean.setOriginalUserResponseSet(setAuthUserListResponse(userMstSet));
			});
            userListResponseBean.setCreatedBy(tempDto.getCreatedBy());
            userListResponseBean.setCreatedDate(tempDto.getCreatedDate());
            userListResponseBean.setLastUpdatedBy(tempDto.getLastUpdatedBy());
            userListResponseBean.setLastUpdatedDate(tempDto.getLastUpdatedDate());
			userListResponseBeanSet.add(userListResponseBean);
		});


		commonResponse.setUserListResponseBeanSet(userListResponseBeanSet);
		commonResponse.setReturnCode(HttpStatus.OK.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End auth pending user request=================");
		return commonResponse;
	}


	private CommonSearchBean setCommonSearchBean(String adminUserId, String userGroup, String approvalStatus, RequestTypeEnum requestType) {
		logger.info("================== Start set common search bean for user request =================");
		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(requestType.name());
		bean.setUserGroup(userGroup);
		bean.setUserId(adminUserId);
		if (approvalStatus != null){
			switch (approvalStatus) {
				case "NEW_PENDING":
					bean.setActionType(ActionTypeEnum.valueOf("CREATE"));
					break;
				case "MODIFY_PENDING":
					bean.setActionType(ActionTypeEnum.valueOf("UPDATE"));
					break;
				case "DELETE_PENDING":
					bean.setActionType(ActionTypeEnum.valueOf("DELETE"));
					break;
			}
		}
		logger.info("================== End set common search bean request =================");
		return bean;
	}

	private OriginalUserResponse setAuthUserListResponse(Set<UserMst> userMstSet){
		logger.info("================== Start set auth user list request =================");
		OriginalUserResponse originalUserResponse = new OriginalUserResponse();
		userMstSet.forEach(value -> {
			try {
				BeanUtils.copyProperties(originalUserResponse, value);
				UserListResponseBean userListResponseBean = new UserListResponseBean();
				originalUserResponse.setUserAccounts(setUserAccountBean(value.getUserPrimaryAccounts()));
				originalUserResponse.setUserFeatures(setUserFeatureBean(value.getUserPrimaryFeatures()));
				originalUserResponse.setUserWorkFlowGroupBeans(null);

			}catch (IllegalAccessException | InvocationTargetException exception){
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), exception,
						ErrorCode.DATA_COPY_ERROR);
			}
			logger.info("================== End set auth user list response =================");
		});
		return originalUserResponse;
	}

	public UserResponseList getUserList(String companyId, RecordStatuUsersEnum recordStatus) {
		logger.info("================== Start get user list request =================");
        UserResponseList userResponseListSet =  new UserResponseList();

		Set<UserMst> userMstSet;
		if (companyId != null && recordStatus == null){
			userMstSet = userMstRepository.findByCompanyId(companyId);
		}else if (companyId == null && recordStatus != null){
			userMstSet = userMstRepository.findByRecordStatus(recordStatus);
		}else {
			userMstSet = userMstRepository.findAll();
		}
        Set<UserListResponse> userListResponseSet = new HashSet<>();
		userMstSet.forEach( values -> {
            AtomicReference<UserListResponse> userListResponse = new AtomicReference<>(new UserListResponse());
                 userListResponse.set(UserListResponse.builder()
                .userId(values.getUserId())
                .userName(values.getUserName())
                .designation(values.getDesignation())
                .branch(values.getBranch())
                .recordStatus(values.getRecordStatus())
                .status(values.getStatus())
                .email(values.getEmail())
                .iamCreateState(values.getIamCreateState())
                .userType(values.getUserType())
                .createdBy(values.getCreatedBy())
                .createdDate(values.getCreatedDate())
                .lastUpdatedBy(values.getLastUpdatedBy())
                .lastUpdatedDate(values.getLastUpdatedDate())
                .lastVerifiedBy(values.getLastVerifiedBy())
                .lastVerifiedDate(values.getLastVerifiedDate())
                .build());
            userListResponseSet.add(userListResponse.get());
		});

        userResponseListSet.setUserListResponses(userListResponseSet);
        userResponseListSet.setReturnCode(HttpStatus.OK.value());
        userResponseListSet.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        userResponseListSet.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, new Object[] {ErrorDescription.SUCCESS}, LocaleContextHolder.getLocale()));
		logger.info("================== End get user list response =================");
		return userResponseListSet;
	}

	public CommonResponse deleteUser(String userId, String requestId, String userGroup, String adminUserId, DeleteUserRequest deleteUserRequest){
        logger.info("================== Start Delete User =================");
        Optional<UserMst> userMstOptional = userMstRepository.findByUserId(Long.parseLong(userId));
        CommonResponse commonResponse = new CommonResponse();
        CommonRequestBean commonRequestBean = new CommonRequestBean();
        CreateUserRequest createUserRequest;
        if (!userMstOptional.isPresent()) {
            logger.info(
                    messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
            commonResponse.setErrorCode(ErrorCode.NO_USER_RECORD_FOUND);
            commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
            commonResponse.setReturnMessage(
                    messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
        }else {
			String hashTags = USER_HASH_TAG.concat(String.valueOf(userMstOptional.get().getCompanyId()));
			String referenceNo = null;
			if (userMstOptional.get().getCompanyId() != null){
				referenceNo = requestId.concat("-").concat(userMstOptional.get().getCompanyId());
			}else{
				referenceNo = requestId;
			}
			createUserRequest = setCreateUserRequest(userMstOptional);
			commonRequestBean.setCommonTempBean(createUserRequest);
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);
			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(String.valueOf(userMstOptional.get().getUserId()));
			CommonResponseBean commonResponseBean = userTempComponent.deleteUserTemp(commonRequestBean, requestId);
			userMstOptional.get().setRecordStatus(RecordStatuUsersEnum.DELETE_PENDING);
            userMstRepository.save(userMstOptional.get());
            commonResponse.setErrorCode(commonResponseBean.getErrorCode());
            commonResponse.setReturnCode(commonResponseBean.getReturnCode());
            commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
        }
        logger.info("================== End Delete User =================");
		return commonResponse;
    }

    private CreateUserRequest setCreateUserRequest(Optional<UserMst> userMstOptional){
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUserId(userMstOptional.get().getUserId());
		createUserRequest.setUserName(userMstOptional.get().getUserName());
		createUserRequest.setDesignation(userMstOptional.get().getDesignation());
		createUserRequest.setBranchCode(userMstOptional.get().getBranch());
		createUserRequest.setUserName(userMstOptional.get().getUserName());
		createUserRequest.setEmail(userMstOptional.get().getEmail());
		createUserRequest.setUserType(userMstOptional.get().getUserType());
		createUserRequest.setPrimaryCompanyId(userMstOptional.get().getCompanyId());
		createUserRequest.setAllAccountAccessFlag(userMstOptional.get().getAllAcctAccessFlg());
        createUserRequest.setUserAccountBeans(setUserAccountBean(userMstOptional.get().getUserPrimaryAccounts()));
        createUserRequest.setUserFeatureBeans(setUserFeatureBean(userMstOptional.get().getUserPrimaryFeatures()));
        createUserRequest.setCreateBy(userMstOptional.get().getCreatedBy());
		createUserRequest.setCreateDate(userMstOptional.get().getCreatedDate());
		createUserRequest.setLastModifiedBy(userMstOptional.get().getLastUpdatedBy());
		createUserRequest.setLastModifiedDate(userMstOptional.get().getLastUpdatedDate());
		createUserRequest.setLastVerifiedBy(userMstOptional.get().getLastVerifiedBy());
		createUserRequest.setLastVerifiedDate(userMstOptional.get().getLastVerifiedDate());
		return createUserRequest;
	}
    private Set<UserAccountsBean> userAccountsBeanSet = new HashSet<>();
	private Set<UserAccountsBean> setUserAccountBean(Set<UserPrimaryAccount> userPrimaryAccounts){
        UserAccountsBean userAccountsBean = new UserAccountsBean();
        userPrimaryAccounts.forEach(values -> {
            userAccountsBean.setAccountId(values.getAccountNo());
            userAccountsBeanSet.add(userAccountsBean);
        });
       return userAccountsBeanSet;
    }

	private Set<UserFeaturesBean> setUserFeatureBean(Set<UserPrimaryFeature> userPrimaryFeatures){
	    Set<UserFeaturesBean> userFeatureBeanSet = new HashSet<>();
		UserFeaturesBean userFeatureBean = new UserFeaturesBean();
		userPrimaryFeatures.forEach(values -> {
			userFeatureBean.setFeatureId(values.getFeature());
			userFeatureBeanSet.add(userFeatureBean);
		});
		return userFeatureBeanSet;
	}

	public UserListResponseByUserID getUserListByUserID(String userID, AccountTypeEnum accountType, String accountNumber, String accountName, Date acctOpenedDateFrom, Date acctOpenedDateTo, String currencyType) {
		logger.info("================== Start get user list request =================");
		UserListResponseByUserID userListResponseByUserID =  new UserListResponseByUserID();
		Optional<UserMst> optionalUserMst = userMstRepository.findByUserId(Long.parseLong(userID));
		Optional.ofNullable(optionalUserMst).ifPresent( value -> {
			userListResponseByUserID.setUserId(value.get().getUserId());
			userListResponseByUserID.setUserName(value.get().getUserName());
			userListResponseByUserID.setUserType(value.get().getUserType());
			userListResponseByUserID.setDesignation(value.get().getDesignation());
			userListResponseByUserID.setBranch(value.get().getBranch());
			userListResponseByUserID.setStatus(value.get().getStatus());
			userListResponseByUserID.setRecordStatus(value.get().getRecordStatus());
			userListResponseByUserID.setIamCreateState(value.get().getIamCreateState());

			Set<UserPrimaryFeature> primaryCompanyFeatures = value.get().getUserPrimaryFeatures();
			primaryCompanyFeatures.forEach(valueRs -> {
				long feature = valueRs.getFeature();
				Optional<Features> featuresOptional = featuresRepository.findById(feature);
				Set<PrimaryCompanyFeatures> primaryCompanyFeaturesSet = new HashSet<>();
				Optional.ofNullable(featuresOptional).ifPresent(features -> {
					PrimaryCompanyFeatures primaryCompanyFeaturesData = new PrimaryCompanyFeatures();
					primaryCompanyFeaturesData.setFeatureId(features.get().getId());
					primaryCompanyFeaturesData.setFeatureName(features.get().getDescription());
                    primaryCompanyFeaturesSet.add(primaryCompanyFeaturesData);
				});
                userListResponseByUserID.setPrimaryCompanyFeatures(primaryCompanyFeaturesSet);
			});
			Set<UserPrimaryAccount> primaryCompanyAccount = value.get().getUserPrimaryAccounts();
			Set<PrimaryCompanyAccounts> primaryCompanyAccountsSet = new HashSet<>();
			primaryCompanyAccount.forEach(valueRs -> {
				PrimaryCompanyAccounts primaryCompanyAccounts = new PrimaryCompanyAccounts();
				primaryCompanyAccounts.setAccountNumber(valueRs.getAccountNo());
                primaryCompanyAccountsSet.add(primaryCompanyAccounts);
			});
            userListResponseByUserID.setPrimaryCompanyAccounts(primaryCompanyAccountsSet);
            GetUserDetailsResponse getUserDetailsResponse = callGetUsers(value.get().getUserId());
            Set<GroupsDetails> groupsDetails = getUserDetailsResponse.groups;
            Set<PrimaryCompanyWorkflowGroups> primaryCompanyWorkflowGroupsSet = new HashSet<>();
            groupsDetails.forEach( getValues -> {
                PrimaryCompanyWorkflowGroups primaryCompanyWorkflowGroups = new PrimaryCompanyWorkflowGroups();
                primaryCompanyWorkflowGroups.setUserGroupId(getValues.getGroupId());
                primaryCompanyWorkflowGroupsSet.add(primaryCompanyWorkflowGroups);
            });
			userListResponseByUserID.setPrimaryCompanyWorkflowGroups(primaryCompanyWorkflowGroupsSet);
			LinkedCompaniesBean linkedCompaniesBean = setLinkedCompaniesBean(value);
			userListResponseByUserID.setLinkedCompaniesBean(linkedCompaniesBean);
		});

        userListResponseByUserID.setReturnCode(HttpStatus.OK.value());
        userListResponseByUserID.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        userListResponseByUserID.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, new Object[] {ErrorDescription.SUCCESS}, LocaleContextHolder.getLocale()));
		logger.info("================== End get user list request =================");
		return userListResponseByUserID;
	}

    GetUserDetailsResponse callGetUsers(long userID){
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String URL = getUserDetailsURL;

        return restTemplate.getForObject(URL, GetUserDetailsResponse.class, userID);
    }
    
	private LinkedCompaniesBean setLinkedCompaniesBean(Optional<UserMst> userMst){
		logger.info("================== Start set linked companies bean request =================");
		AtomicReference<LinkedCompaniesBean> linkedCompaniesBean = new AtomicReference<>();
		Set<UserLinkedCompany> userLinkedCompanies = userMst.get().getUserLinkedCompanies();
		userLinkedCompanies.forEach(values -> {
			linkedCompaniesBean.set(new LinkedCompaniesBean());
			CompanyMst companyMst = values.getCompanyMst();
			linkedCompaniesBean.get().setCompanyId(companyMst.getCompanyId());
			linkedCompaniesBean.get().setCompanyName(companyMst.getCompanyName());
			Set<UserCompanyFeature> userCompanyFeatures = values.getUserCompanyFeatures();
			userCompanyFeatures.forEach(userCompanyFeature -> {
				long feature = userCompanyFeature.getFeature();
				Set<CompanyFeatures> features = companyFeaturesRepository.findByFeature(feature);
                Set<UserCompanyFeaturesBean> userCompanyFeaturesBeanSet = new HashSet<>();
				features.forEach(featuresList -> {
					UserCompanyFeaturesBean userCompanyFeaturesBean = new UserCompanyFeaturesBean();
					userCompanyFeaturesBean.setFeatureId(featuresList.getFeature());
					userCompanyFeaturesBean.setFeatureName(featuresList.getFeatureDescription());
                    userCompanyFeaturesBeanSet.add(userCompanyFeaturesBean);
				});
                linkedCompaniesBean.get().setUserCompanyFeatures(userCompanyFeaturesBeanSet);
			});
			Set<UserCompanyAccount> userCompanyAccounts = values.getUserCompanyAccounts();
            Set<UserCompanyAccountsBean> userCompanyAccountsBeanHashSet = new HashSet<>();
			userCompanyAccounts.forEach(userCompanyAccount -> {
				UserCompanyAccountsBean userCompanyAccountsBean =  new UserCompanyAccountsBean();
				userCompanyAccountsBean.setAccountNumber(userCompanyAccount.getAccountNo());
				userCompanyAccountsBeanHashSet.add(userCompanyAccountsBean);
			});
            linkedCompaniesBean.get().setUserCompanyAccounts(userCompanyAccountsBeanHashSet);
            GetUserDetailsResponse getUserDetailsResponse = callGetUsers(values.getUserMst().getUserId());
            Set<GroupsDetails> groupsDetails = getUserDetailsResponse.groups;
            Set<UserCompanyWorkflowGroupsBean> userCompanyWorkflowGroupsBeanHashSet = new HashSet<>();
            groupsDetails.forEach( getValues -> {
                UserCompanyWorkflowGroupsBean userCompanyWorkflowGroupsBean = new UserCompanyWorkflowGroupsBean();
                userCompanyWorkflowGroupsBean.setUserGroupId(getValues.getGroupId());
                userCompanyWorkflowGroupsBeanHashSet.add(userCompanyWorkflowGroupsBean);
            });
			linkedCompaniesBean.get().setUserCompanyWorkflowGroups(userCompanyWorkflowGroupsBeanHashSet);
		});
		logger.info("================== end set linked companies bean request =================");
		return linkedCompaniesBean.get();
	}

	public CommonResponse changeStatus(String userId,String companyId, String requestId, String userGroup, String adminUserId, BlockRequest blockRequest){
		logger.info("================== Start change status request =================");
		CommonResponse commonResponse = new CommonResponse();
		Optional<UserMst> userMstOptional = userMstRepository.findByUserIdAndCompanyId(Long.parseLong(userId), companyId);
		CommonRequestBean commonRequestBean = new CommonRequestBean();
		CreateUserRequest createUserRequest ;
		if (userMstOptional.get().getStatus().equals(blockRequest.getBlockedStatus())){
			logger.info(
					messageSource.getMessage(ErrorCode.USER_ALREADY_SAME_STATUS, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.USER_ALREADY_SAME_STATUS);
			commonResponse.setReturnCode(HttpStatus.OK.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.USER_ALREADY_SAME_STATUS, null, LocaleContextHolder.getLocale()));
		}else {
            createUserRequest = setCreateUserRequest(userMstOptional);
            createUserRequest.setLastModifiedDate(new Date());

            createUserRequest.setLastModifiedBy(adminUserId);
            createUserRequest.setUserId(userMstOptional.get().getUserId());
            String hashTags = USER_HASH_TAG.concat(String.valueOf(createUserRequest.getPrimaryCompanyId()));
            String referenceNo;
            if (createUserRequest.getPrimaryCompanyId() != null){
                referenceNo = requestId.concat("-").concat(createUserRequest.getPrimaryCompanyId());
            }else{
                referenceNo = requestId;
            }
            commonRequestBean.setCommonTempBean(createUserRequest);
            commonRequestBean.setHashTags(hashTags);
            commonRequestBean.setReferenceNo(referenceNo);
            commonRequestBean.setRequestType(REQUEST_TYPE.name());
            commonRequestBean.setUserGroup(userGroup != null ? userGroup : COMMON_USER_GROUP);
            commonRequestBean.setUserId(userId);
            CommonResponseBean commonResponseBean = userTempComponent.updateTempUser(commonRequestBean, requestId);
            userMstOptional.get().setRecordStatus(RecordStatuUsersEnum.MODIFY_PENDING);
            userMstOptional.get().setStatus(blockRequest.getBlockedStatus());
            userMstRepository.save(userMstOptional.get());
            commonResponse.setErrorCode(commonResponseBean.getErrorCode());
            commonResponse.setReturnCode(commonResponseBean.getReturnCode());
            commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
        }
		logger.info("================== End change status request =================");
		return commonResponse;
	}

	private CommonRequestBean setCommonRequestBean(String userGroup, String requestId,CreateUserRequest createUserRequest) {
		logger.info("================== Start set common request bean=================");
        CommonRequestBean commonRequestBean = new CommonRequestBean();
		String hashTags = USER_HASH_TAG.concat(String.valueOf(createUserRequest.getPrimaryCompanyId()));
        String referenceNo;
        referenceNo = requestId.concat("-").concat(createUserRequest.getPrimaryCompanyId());
        commonRequestBean.setCommonTempBean(createUserRequest);
		commonRequestBean.setHashTags(hashTags);
		commonRequestBean.setReferenceNo(referenceNo);
		commonRequestBean.setRequestType(REQUEST_TYPE.name());
		commonRequestBean.setUserGroup(userGroup);
		commonRequestBean.setUserId(String.valueOf(createUserRequest.getUserId()));
		logger.info("================== End set common request bean=================");
		return commonRequestBean;
	}
}