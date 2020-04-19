package biz.nable.sb.cor.comp.service.impl;

import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.bean.CommonSearchBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.comp.bean.*;
import biz.nable.sb.cor.comp.component.UserLinkCompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.UserLinkCompanyRepository;
import biz.nable.sb.cor.comp.db.repository.UserMstRepository;
import biz.nable.sb.cor.comp.request.UserLinkRequest;
import biz.nable.sb.cor.comp.response.ApprovalPendingUserLinkResponse;
import biz.nable.sb.cor.comp.response.UserLinkListByApprovalIDResponse;
import biz.nable.sb.cor.comp.thirdparty.GetUserDetailsResponse;
import biz.nable.sb.cor.comp.thirdparty.GroupsDetails;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.ErrorDescription;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserLinkCompanyService {

    @Autowired
    CommonConverter commonConverter;

    @Autowired
    UserLinkCompanyTempComponent userLinkCompanyTempComponent;

    @Autowired
    UserLinkCompanyRepository userLinkCompanyRepository;

    @Autowired
    UserMstRepository userMstRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String USER_HASH_TAG = "COMPANY_ID=";

    private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.LINK_USER;

    public CommonResponse createUserLinkTemp(UserLinkRequest userLinkRequest, String userId, String userGroup,
                                         String requestId, String adminUser) {
        logger.info("================== Start Create User Link Temp Request =================");
        CommonResponse commonResponse = new CommonResponse();
        CompanyMst companyMst = new CompanyMst();
        companyMst.setCompanyId(userLinkRequest.getCompanyId());
        UserMst userMst = new UserMst();
        userMst.setUserId(Long.parseLong(userId));
        Optional<UserLinkedCompany> userLinkedCompany =
                userLinkCompanyRepository.findByCompanyMstAndUserMst(companyMst, userMst);
        if (userLinkedCompany.isPresent()){
            logger.info(messageSource.getMessage(ErrorCode.USER_ID_ALREADY_LINK,
                    new Object[]{ ErrorDescription.USER_RECORD_ALREADY_LINKED },
                    LocaleContextHolder.getLocale()));
            commonResponse.setErrorCode(ErrorCode.USER_ID_ALREADY_LINK);
            commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
            commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.USER_ID_ALREADY_LINK,
                    new Object[]{ ErrorDescription.USER_RECORD_ALREADY_LINKED },
                    LocaleContextHolder.getLocale()));
        }else {
            Optional<UserMst> userMstOptional = userMstRepository.findByUserId(Long.parseLong(userId));
            if (!userMstOptional.isPresent()){
                logger.info(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS,
                        new Object[] {ErrorDescription.USER_RECORD_ALREADY_EXISTS},
                        LocaleContextHolder.getLocale()));
                commonResponse.setErrorCode(ErrorCode.USER_RECORD_ALREADY_EXISTS);
                commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
                commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS,
                        new Object[] {ErrorDescription.USER_RECORD_ALREADY_EXISTS},
                        LocaleContextHolder.getLocale()));
            }else {
                CommonRequestBean commonRequestBean ;
                userLinkRequest.setCreatedBy(adminUser);
                userLinkRequest.setCreatedDate(new Date());
                userLinkRequest.setUserGroup(userGroup);
                commonRequestBean = setCommonRequestBean(userGroup, userId, requestId, userLinkRequest);
                CommonResponseBean commonResponseBean = userLinkCompanyTempComponent.createTempRecord(commonRequestBean, requestId);
                commonResponse.setErrorCode(commonResponseBean.getErrorCode());
                commonResponse.setReturnCode(commonResponseBean.getReturnCode());
                commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
            }
        }
        logger.info("================== End Create User Link Temp Request =================");
        return commonResponse;
    }

    public CommonResponse updateUserLinkTemp(UserLinkRequest userLinkRequest, String userId, String userGroup,
                                             String requestId, String adminUser) {
        logger.info("================== Start Update User Link Temp Request =================");
        CommonResponse commonResponse = new CommonResponse();
        CompanyMst companyMst = new CompanyMst();
        companyMst.setCompanyId(userLinkRequest.getCompanyId());
        UserMst userMst = new UserMst();
        userMst.setUserId(Long.parseLong(userId));
        Optional<UserLinkedCompany> userLinkedCompany =
                userLinkCompanyRepository.findByCompanyMstAndUserMst(companyMst, userMst);
        if (!userLinkedCompany.isPresent()){
            setErrorMessage(commonResponse);
        }else {
            CommonRequestBean commonRequestBean =  new CommonRequestBean() ;
            userLinkRequest.setLastUpdatedBy(adminUser);
            userLinkRequest.setLastUpdatedDate(new Date());
            userLinkRequest.setUserGroup(userGroup);
            String hashTags = USER_HASH_TAG.concat(String.valueOf(userLinkRequest.getCompanyId()));
            String referenceNo = userId.concat("&").concat(userLinkRequest.getCompanyId());
            commonRequestBean.setHashTags(hashTags);
            commonRequestBean.setReferenceNo(referenceNo);
            commonRequestBean.setRequestType(REQUEST_TYPE.name());
            commonRequestBean.setUserGroup(userGroup);
            commonRequestBean.setUserId(userId);
            commonRequestBean.setCommonTempBean(userLinkRequest);
            CommonResponseBean commonResponseBean = userLinkCompanyTempComponent.updateTempCompany(commonRequestBean, requestId);
            userLinkedCompany.get().setRecordStatus(RecordStatuUsersEnum.MODIFY_PENDING);
            userLinkCompanyRepository.save(userLinkedCompany.get());
            commonResponse.setErrorCode(commonResponseBean.getErrorCode());
            commonResponse.setReturnCode(commonResponseBean.getReturnCode());
            commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
        }
        logger.info("================== End Update User Link Temp Request =================");
        return commonResponse;
    }

    private void setErrorMessage(CommonResponse commonResponse) {
        logger.info("================== Start Set error message Request =================");
        logger.info(
                messageSource.getMessage(ErrorCode.NO_USER_LINK_COMPANY_RECORD_FOUND,
                        new Object[] { ErrorDescription.NO_USER_LINK_COMPANY_RECORD }, LocaleContextHolder.getLocale()));
        commonResponse.setErrorCode(ErrorCode.NO_USER_LINK_COMPANY_RECORD_FOUND);
        commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
        commonResponse.setReturnMessage(
                messageSource.getMessage(ErrorCode.NO_USER_LINK_COMPANY_RECORD_FOUND,
                        new Object[] { ErrorDescription.NO_USER_LINK_COMPANY_RECORD }, LocaleContextHolder.getLocale()));
        logger.info("================== End Set error message Request =================");
    }

    private CommonRequestBean setCommonRequestBean(String userGroup, String userId, String requestId, UserLinkRequest userLinkRequest) {
        logger.info("================== Start set common request bean=================");
        CommonRequestBean commonRequestBean = new CommonRequestBean();
        String hashTags = USER_HASH_TAG.concat(String.valueOf(userLinkRequest.getCompanyId()));
        String referenceNo = userId.concat("&").concat(userLinkRequest.getCompanyId());
        commonRequestBean.setCommonTempBean(userLinkRequest);
        commonRequestBean.setHashTags(hashTags);
        commonRequestBean.setReferenceNo(referenceNo);
        commonRequestBean.setRequestType(REQUEST_TYPE.name());
        commonRequestBean.setUserGroup(userGroup);
        commonRequestBean.setUserId(userId);
        logger.info("================== End set common request bean=================");
        return commonRequestBean;
    }

    public ApprovalPendingUserLinkResponse pendingUserLinkCompany(String userGroup, String requestId, String adminUserId, String approvalStatus){
        logger.info("================== Start pending user link company request =================");
        ApprovalPendingUserLinkResponse approvalPendingUserLinkResponse = new ApprovalPendingUserLinkResponse();
        CommonSearchBean bean =  setCommonSearchBean(userGroup, adminUserId, approvalStatus, REQUEST_TYPE);
        List<TempDto> tempResponseList;
        tempResponseList = userLinkCompanyTempComponent.getAuthPendingRecord(bean).getTempList();

        if (tempResponseList == null){
            setErrorMessage(approvalPendingUserLinkResponse);
        }else {
            final String[] userId = new String[1];
            Set<UserLinkListResponse> userLinkListResponseSet = new HashSet<>();
            tempResponseList.forEach(tempResponse -> {
                UserLinkListResponse userLinkListResponse = new UserLinkListResponse();
                String[] responseArray = tempResponse.getReferenceNo().split("&");
                userId[0] = responseArray[0];
                userLinkListResponse.setUserId(userId[0]);
                userLinkListResponse.setApprovalId(tempResponse.getApprovalId());
                userLinkListResponse.setApprovalStatus(approvalStatus);
                userLinkListResponse.setSignature(tempResponse.getSignature());
                LinkedCompaniesBean linkedCompaniesBean = commonConverter.mapToPojo(tempResponse.getRequestPayload(), LinkedCompaniesBean.class);
                userLinkListResponse.setLinkedCompaniesBean(linkedCompaniesBean);
                userLinkListResponseSet.add(userLinkListResponse);
            });
            approvalPendingUserLinkResponse.setUserLinkListResponseHashSet(userLinkListResponseSet);
            approvalPendingUserLinkResponse.setReturnCode(HttpStatus.OK.value());
            approvalPendingUserLinkResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
            approvalPendingUserLinkResponse.setReturnMessage(
                    messageSource.getMessage(ErrorCode.OPARATION_SUCCESS,
                            new Object[] { ErrorDescription.SUCCESS }, LocaleContextHolder.getLocale()));
        }
        logger.info("================== End pending user link company request =================");
        return approvalPendingUserLinkResponse;
    }

    private CommonSearchBean setCommonSearchBean(String userGroup, String adminUserId, String approvalStatus, RequestTypeEnum requestType) {
        logger.info("================== Start set common search bean request =================");
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

    public UserLinkListByApprovalIDResponse pendingUserLinkByApprovalID (String userGroup, String requestId, String adminUserId, String approvalId){
        logger.info("================== Start Pending User Link By ApprovalID request =================");
        UserLinkListByApprovalIDResponse userLinkListByApprovalIDResponse = new UserLinkListByApprovalIDResponse();
        CommonSearchBean bean =  setCommonSearchBean(userGroup, adminUserId, null, REQUEST_TYPE);
        List<TempDto> tempResponseList;
        tempResponseList = userLinkCompanyTempComponent.getAuthPendingRecord(bean).getTempList();
        if (tempResponseList == null){
            setErrorMessage(userLinkListByApprovalIDResponse);
        }else {
            final String[] userId = new String[1];
            final String[] companyId = new String[1];
            tempResponseList.forEach(tempResponse -> {
                LinkedCompaniesBean linkedCompaniesBean;
                String[] responseArray = tempResponse.getReferenceNo().split("&");
                userId[0] = responseArray[0];
                companyId[0] = responseArray[1];
                userLinkListByApprovalIDResponse.setUserId(userId[0]);
                userLinkListByApprovalIDResponse.setApprovalId(approvalId);
                userLinkListByApprovalIDResponse.setApprovalStatus(tempResponse.getActionType().toString());
                userLinkListByApprovalIDResponse.setSignature(tempResponse.getSignature());
                linkedCompaniesBean = commonConverter.mapToPojo(tempResponse.getRequestPayload(), LinkedCompaniesBean.class);
                ModifiedUserLinkBean modifiedUserLinkBean = new ModifiedUserLinkBean();
                modifiedUserLinkBean.setLinkedCompaniesBean(linkedCompaniesBean);
                userLinkListByApprovalIDResponse.setModifiedUserLinkBean(modifiedUserLinkBean);
                CompanyMst companyMstId = new CompanyMst();
                companyMstId.setCompanyId(companyId[0]);
                UserMst userMstId = new UserMst();
                userMstId.setUserId(Long.parseLong(userId[0]));
                Optional<UserLinkedCompany> userLinkedCompany = userLinkCompanyRepository.findByCompanyMstAndUserMst(companyMstId,userMstId);
                OriginalUserLinkBean originalUserLinkBean = new OriginalUserLinkBean();
                linkedCompaniesBean = new LinkedCompaniesBean();
                linkedCompaniesBean.setCompanyId(userLinkedCompany.get().getCompanyMst().getCompanyId());
                linkedCompaniesBean.setCompanyName(userLinkedCompany.get().getCompanyMst().getCompanyName());
                linkedCompaniesBean.setAllAccountAccessFlag(userLinkedCompany.get().getAllAcctAccessFlg());
                Set<UserCompanyFeature> userCompanyFeatures = userLinkedCompany.get().getUserCompanyFeatures();
                Set<UserCompanyFeaturesBean> userCompanyFeaturesBeanSet = new HashSet<>();
                userCompanyFeatures.forEach(userCompanyFeature -> {
                    UserCompanyFeaturesBean userCompanyFeaturesBean = new UserCompanyFeaturesBean();
                    userCompanyFeaturesBean.setFeatureId(userCompanyFeature.getFeature());
                    userCompanyFeaturesBeanSet.add(userCompanyFeaturesBean);
                });
                linkedCompaniesBean.setUserCompanyFeaturesBean(userCompanyFeaturesBeanSet);
                Set<UserCompanyAccount> userCompanyAccounts = userLinkedCompany.get().getUserCompanyAccounts();
                Set<UserCompanyAccountsBean> userCompanyAccountsBeanHashSet = new HashSet<>();
                userCompanyAccounts.forEach(userCompanyAccount -> {
                    UserCompanyAccountsBean userCompanyAccountsBean = new UserCompanyAccountsBean();
                    userCompanyAccountsBean.setAccountNumber(userCompanyAccount.getAccountNo());
                    userCompanyAccountsBeanHashSet.add(userCompanyAccountsBean);
                });
                linkedCompaniesBean.setUserCompanyAccountsBean(userCompanyAccountsBeanHashSet);
                GetUserDetailsResponse getUserDetailsResponse = userService.callGetUsers(Long.parseLong(userId[0]));
                Set<GroupsDetails> groupsDetails = getUserDetailsResponse.groups;
                Set<UserCompanyWorkflowGroupsBean> userCompanyWorkflowGroupsBeanHashSet = new HashSet<>();
                groupsDetails.forEach( getValues -> {
                    UserCompanyWorkflowGroupsBean userCompanyWorkflowGroupsBean = new UserCompanyWorkflowGroupsBean();
                    userCompanyWorkflowGroupsBean.setUserGroupId(getValues.getGroupId());
                    userCompanyWorkflowGroupsBeanHashSet.add(userCompanyWorkflowGroupsBean);
                });
                linkedCompaniesBean.setUserCompanyWorkflowGroupsBean(userCompanyWorkflowGroupsBeanHashSet);
                originalUserLinkBean.setLinkedCompaniesBean(linkedCompaniesBean);
                userLinkListByApprovalIDResponse.setOriginalUserLinkBean(originalUserLinkBean);
            });
            userLinkListByApprovalIDResponse.setReturnCode(HttpStatus.OK.value());
            userLinkListByApprovalIDResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
            userLinkListByApprovalIDResponse.setReturnMessage(
                    messageSource.getMessage(ErrorCode.OPARATION_SUCCESS,
                            new Object[] { ErrorDescription.SUCCESS }, LocaleContextHolder.getLocale()));
        }
        logger.info("================== End Pending User Link By ApprovalID request =================");
        return userLinkListByApprovalIDResponse;
    }
}
