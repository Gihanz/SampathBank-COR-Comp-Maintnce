package biz.nable.sb.cor.comp.service.impl;

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
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.component.UserTempComponent;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Service
public class UserService {

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
			setCreateUserRequest(userGroup, requestId, adminUser, commonRequestBean, createUserRequest);
			setCommonResponse(requestId, commonResponse, optional, commonRequestBean);
			logger.info("================== End update user request =================");
		}
		return commonResponse;
	}

	public ApprovalPendingUserResponse getPendingAuthUseres(String userId, String userGroup, String approvalStatus) {
		logger.info("================== Start auth pending user request=================");
		ApprovalPendingUserResponse commonResponse = new ApprovalPendingUserResponse();
		CommonSearchBean bean = setCommonSearchBean(userId, userGroup, approvalStatus);
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
		Set<String> userReferenceList = new HashSet<>();
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
//				BeanUtils.copyProperties(userListResponseBean, tempDto);
			}catch (Exception exception){
				logger.error("Error in copying tempDTO to userListResponseBean.: {}", exception.toString());
			}

			ModifiedUserResponse modifiedUserResponse = commonConverter.mapToPojo(tempDto.getRequestPayload(), ModifiedUserResponse.class);
			userListResponseBean.setModifiedUserResponseSet(modifiedUserResponse);
			if (!ActionTypeEnum.CREATE.equals(tempDto.getActionType())) {
				userReferenceList.add(tempDto.getReferenceNo());
			}
			Optional.of(userReferenceList).ifPresent(userList -> {
				Set<UserMst> userMstSet = userMstRepository.findByUserIdIn(userReferenceList);
				userListResponseBean.setOriginalUserResponseSet(setAuthUserListResponse(userMstSet));
			});
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

	public CommonSearchBean setCommonSearchBean(String userId, String userGroup, String approvalStatus){
		logger.info("================== Start set common search bean=================");
		CommonSearchBean bean = setCommonSearchBean(userGroup, userId, approvalStatus, REQUEST_TYPE);
		logger.info("================== End set common search bean=================");
		return bean;
	}

	private CommonSearchBean setCommonSearchBean(String userGroup, String adminUserId, String approvalStatus, RequestTypeEnum requestType) {
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
//				UserListResponseBean userListResponseBean = userListResponseBeanSet.stream()
//						.filter(userListResponseValue -> userListResponseValue.getId().equals(originalUserResponse.getId())).findAny().orElse(null);
				UserListResponseBean userListResponseBean = new UserListResponseBean();
//				Set<UserPrimaryAccount> userAccount = new HashSet<>(value.getUserPrimaryAccounts());
//				Set<UserPrimaryFeature> userFeature = new HashSet<>(value.getUserPrimaryFeatures());
				Set<UserAccountsBean> userAccountsBeanSet  = Collections.singleton((UserAccountsBean) value.getUserPrimaryAccounts());
				Set<UserFeaturesBean> userFeaturesBeanSet  = Collections.singleton((UserFeaturesBean) value.getUserPrimaryFeatures());
				originalUserResponse.setUserAccounts(userAccountsBeanSet);
				originalUserResponse.setUserFeatures(userFeaturesBeanSet);
				originalUserResponse.setUserWorkFlowGroupBeans(null);
//				Optional.ofNullable(userListResponseBean).ifPresent(response -> {
//					userListResponseBean.setOriginalUserResponseSet(originalUserResponse);
//				});

			}catch (IllegalAccessException | InvocationTargetException exception){
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), exception,
						ErrorCode.DATA_COPY_ERROR);
			}
			logger.info("================== End set auth user list response =================");
		});
		return originalUserResponse;
	}

	public UserResponseList getUserList(String companyId, String recordStatus) {
		logger.info("================== Start get user list request =================");
        UserResponseList userResponseListSet =  new UserResponseList();
		UserListResponse userListResponse = new UserListResponse();
		Set<UserMst> userMstSet;
		if (companyId != null && recordStatus == null){
			userMstSet = userMstRepository.findByCompanyId(companyId);
		}else if (companyId == null && recordStatus != null){
			userMstSet = userMstRepository.findByRecordStatus(recordStatus);
		}else {
			userMstSet = userMstRepository.findAll();
		}
		userMstSet.forEach(value -> {
			userListResponse.builder()
					.id(value.getUserId())
					.userName(value.getUserName())
					.designation(value.getDesignation())
					.branch(value.getBranch())
					.recordStatus(value.getRecordStatus())
					.status(value.getStatus())
					.email(value.getEmail())
					.iamCreateState(value.getIamCreateState())
					.userType(value.getUserType())
					.createdBy(value.getCreatedBy())
					.createdDate(value.getCreatedDate())
					.lastUpdatedBy(value.getLastUpdatedBy())
					.lastUpdatedDate(value.getLastUpdatedDate())
					.lastVerifiedBy(value.getLastVerifiedBy())
					.lastVerifiedDate(value.getLastVerifiedDate())
					.build();
		});
        userResponseListSet.setUserListResponses(userListResponse);
		logger.info("================== End get user list response =================");
		return userResponseListSet;
	}

	public CommonResponse deleteUser(String userId, String requestId, String userGroup, String adminUserId, DeleteUserRequest deleteUserRequest){
        logger.info("================== Start Delete User =================");
        Optional<UserMst> userMstOptional = userMstRepository.findByUserId(Long.parseLong(userId));
        CommonResponse commonResponse = new CommonResponse();
        CommonRequestBean commonRequestBean = new CommonRequestBean();
        CreateUserRequest createUserRequest = new CreateUserRequest();
        if (!userMstOptional.isPresent()) {
            logger.info(
                    messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
            commonResponse.setErrorCode(ErrorCode.NO_USER_RECORD_FOUND);
            commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
            commonResponse.setReturnMessage(
                    messageSource.getMessage(ErrorCode.NO_USER_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
        }else {
			setModifyRequest(userGroup, requestId, adminUserId, userMstOptional, commonRequestBean, createUserRequest);
			CommonResponseBean commonResponseBean = userTempComponent.deleteBranchTemp(commonRequestBean, requestId);
			userMstOptional.get().setRecordStatus(RecordStatuUsersEnum.DELETE_PENDING);
            userMstRepository.save(userMstOptional.get());
            commonResponse.setErrorCode(commonResponseBean.getErrorCode());
            commonResponse.setReturnCode(commonResponseBean.getReturnCode());
            commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
        }
        logger.info("================== End Delete User =================");
		return commonResponse;
    }

	public UserListResponseByUserID getUserListByUserID(String userID) {
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
				Optional.ofNullable(featuresOptional).ifPresent(features -> {
					PrimaryCompanyFeatures primaryCompanyFeaturesData = new PrimaryCompanyFeatures();
					primaryCompanyFeaturesData.setFeatureId(features.get().getId());
					primaryCompanyFeaturesData.setFeatureName(features.get().getDescription());

					userListResponseByUserID.setPrimaryCompanyFeatures(primaryCompanyFeaturesData);
				});
			});
			Set<UserPrimaryAccount> primaryCompanyAccount = value.get().getUserPrimaryAccounts();
			primaryCompanyAccount.forEach(valueRs -> {
				PrimaryCompanyAccounts primaryCompanyAccounts = new PrimaryCompanyAccounts();
				primaryCompanyAccounts.setAccountNumber(valueRs.getAccountNo());
				userListResponseByUserID.setPrimaryCompanyAccounts(primaryCompanyAccounts);
			});
			userListResponseByUserID.setPrimaryCompanyWorkflowGroups(null);
			LinkedCompaniesBean linkedCompaniesBean = setLinkedCompaniesBean(value);
			userListResponseByUserID.setLinkedCompaniesBean(linkedCompaniesBean);
		});
		logger.info("================== End get user list request =================");
		return userListResponseByUserID;
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
				features.forEach(featuresList -> {
					UserCompanyFeaturesBean userCompanyFeaturesBean = new UserCompanyFeaturesBean();
					userCompanyFeaturesBean.setFeatureId(featuresList.getFeature());
					userCompanyFeaturesBean.setFeatureName(featuresList.getFeatureDescription());
					linkedCompaniesBean.get().setUserCompanyFeaturesBean(userCompanyFeaturesBean);
				});
			});
			Set<UserCompanyAccount> userCompanyAccounts = values.getUserCompanyAccounts();
			userCompanyAccounts.forEach(userCompanyAccount -> {
				UserCompanyAccountsBean userCompanyAccountsBean =  new UserCompanyAccountsBean();
				userCompanyAccountsBean.setAccountNumber(userCompanyAccount.getAccountNo());
				linkedCompaniesBean.get().setUserCompanyAccountsBean(userCompanyAccountsBean);
			});
			linkedCompaniesBean.get().setUserCompanyWorkflowGroupsBean(null);
		});
		logger.info("================== end set linked companies bean request =================");
		return linkedCompaniesBean.get();
	}

	public CommonResponse changeStatus(String userId,String companyId, String requestId, String userGroup, String adminUserId, BlockRequest blockRequest){
		logger.info("================== Start change status request =================");
		CommonResponse commonResponse = new CommonResponse();
		Optional<UserMst> userMstOptional = userMstRepository.findByUserIdAndCompanyId(userId, companyId);
		CommonRequestBean commonRequestBean = new CommonRequestBean();
		CreateUserRequest createUserRequest = new CreateUserRequest();
		if (userMstOptional.get().getRecordStatus().equals(blockRequest.getBlockedStatus())){
			logger.info(
					messageSource.getMessage(ErrorCode.USER_ALREADY_SAME_STATUS, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.USER_ALREADY_SAME_STATUS);
			commonResponse.setReturnCode(HttpStatus.OK.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.USER_ALREADY_SAME_STATUS, null, LocaleContextHolder.getLocale()));
		}else {
			setModifyRequest(userGroup, requestId, adminUserId, userMstOptional, commonRequestBean, createUserRequest);
			commonResponse = setCommonResponse(requestId, commonResponse, userMstOptional, commonRequestBean);
		}
		logger.info("================== End change status request =================");
		return commonResponse;
	}

	private CommonResponse setCommonResponse(String requestId, CommonResponse commonResponse, Optional<UserMst> userMstOptional,
											 CommonRequestBean commonRequestBean) {
		logger.info("================== Start set common response request =================");
		CommonResponseBean commonResponseBean = userTempComponent.updateTempCompany(commonRequestBean, requestId);
		userMstOptional.get().setRecordStatus(RecordStatuUsersEnum.MODIFY_PENDING);
		userMstRepository.save(userMstOptional.get());
		commonResponse.setErrorCode(commonResponseBean.getErrorCode());
		commonResponse.setReturnCode(commonResponseBean.getReturnCode());
		commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
		logger.info("================== End set common response request =================");
		return commonResponseBean;
	}

	private void setModifyRequest(String userGroup, String requestId,String adminUserId, Optional<UserMst> userMstOptional,
								  CommonRequestBean commonRequestBean, CreateUserRequest createUserRequest) {
		logger.info("================== Start set modify request request =================");
		try {
			BeanUtils.copyProperties(createUserRequest, userMstOptional.get());
		}catch (IllegalAccessException | InvocationTargetException exception){
			throw new SystemException(
					messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), exception,
					ErrorCode.DATA_COPY_ERROR);
		}
		setCreateUserRequest(userGroup, requestId, adminUserId, commonRequestBean, createUserRequest);
		logger.info("================== End set modify request request =================");
	}

	private void setCreateUserRequest(String userGroup, String requestId, String adminUserId, CommonRequestBean commonRequestBean,
									  CreateUserRequest createUserRequest) {
		logger.info("================== Start set create user request =================");
		createUserRequest.setLastModifiedDate(new Date());
		createUserRequest.setLastModifiedBy(adminUserId);
		createUserRequest.setUserGroup(userGroup);
		setCommonRequestBean(userGroup, requestId, createUserRequest);
		commonRequestBean.setCommonTempBean(createUserRequest);
		logger.info("================== End set create user request =================");
	}

	private CommonRequestBean setCommonRequestBean(String userGroup, String requestId,CreateUserRequest createUserRequest) {
		logger.info("================== Start set common request bean=================");
        CommonRequestBean commonRequestBean = new CommonRequestBean();
		String hashTags = USER_HASH_TAG.concat(String.valueOf(createUserRequest.getPrimaryCompanyId()));
		String referenceNo = requestId.concat("-").concat(createUserRequest.getPrimaryCompanyId());
        commonRequestBean.setCommonTempBean(createUserRequest);
		commonRequestBean.setHashTags(hashTags);
		commonRequestBean.setReferenceNo(referenceNo);
		commonRequestBean.setRequestType(REQUEST_TYPE.name());
		commonRequestBean.setUserGroup(userGroup);
		commonRequestBean.setUserId(createUserRequest.getUserName());
		logger.info("================== End set common request bean=================");
		return commonRequestBean;
	}
}