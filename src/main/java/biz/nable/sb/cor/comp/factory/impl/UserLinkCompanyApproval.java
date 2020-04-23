package biz.nable.sb.cor.comp.factory.impl;

import biz.nable.sb.cor.common.bean.ApprovalBean;
import biz.nable.sb.cor.common.bean.ApprovalResponseBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.template.CommonApprovalTemplate;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.ApprovalStatus;
import biz.nable.sb.cor.comp.bean.UserAccountsBean;
import biz.nable.sb.cor.comp.bean.UserFeaturesBean;
import biz.nable.sb.cor.comp.bean.UserWorkFlowGroupsBean;
import biz.nable.sb.cor.comp.component.UserLinkCompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.UserLinkCompanyRepository;
import biz.nable.sb.cor.comp.request.UserLinkRequest;
import biz.nable.sb.cor.comp.thirdparty.GroupsRequest;
import biz.nable.sb.cor.comp.thirdparty.InsertUpdateUserRequest;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Transactional
@Component
public class UserLinkCompanyApproval implements CommonApprovalTemplate {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    UserLinkCompanyTempComponent userLinkCompanyTempComponent;

    @Autowired
    UserLinkCompanyRepository userLinkCompanyRepository;

    @Autowired
    private CommonConverter commonConverter;

    @Value("${add.user.url}")
    private String addUserURL;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CommonResponse doApprove(ApprovalBean approvalBean) {
        CommonResponse commonResponse = new CommonResponse();
        ApprovalResponseBean approvalResponseBean = userLinkCompanyTempComponent.doApprove(approvalBean);
        TempDto commonTemp = approvalResponseBean.getTempDto();
        if (ApprovalStatus.VERIFIED.name().equalsIgnoreCase(approvalBean.getApprovalStatus())) {
            if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
                addUserToLinkCompanyMst(approvalBean, commonTemp);
            } else if (ActionTypeEnum.UPDATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
                updateUserToLinkCompanyMst(approvalBean, commonTemp);
            }
        }else {

        }
        commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
        commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        commonResponse.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
        logger.info("User DoApprove method success response: {}", commonResponse);
        return commonResponse;
    }

    private void addUserToLinkCompanyMst(ApprovalBean approvalBean, TempDto commonTemp){
        final String[] userId = new String[1];
        UserLinkRequest userLinkRequest = commonConverter.mapToPojo(commonTemp.getRequestPayload(),
                UserLinkRequest.class);
        UserLinkedCompany userLinkedCompany;
        Map<String, Object> a= commonTemp.getRequestPayload();
        String[] responseArray = commonTemp.getReferenceNo().split("&");
        userId[0] = responseArray[0];
        CompanyMst companyMst =  new CompanyMst();
        companyMst.setCompanyId(String.valueOf(a.get("companyId")));
        UserMst userMst =  new UserMst();
        userMst.setUserId(Long.valueOf(userId[0]));
        userLinkedCompany = buildUserLinkedCompany(userLinkRequest, userMst, companyMst);
        saveMasterLinkTable(userLinkRequest, userLinkedCompany, approvalBean, commonTemp);
        callUserCreation(userMst, userLinkRequest);
    }

    private void updateUserToLinkCompanyMst(ApprovalBean approvalBean, TempDto commonTemp){
        UserLinkRequest userLinkRequest = commonConverter.mapToPojo(commonTemp.getRequestPayload(), UserLinkRequest.class);
        UserLinkedCompany userLinkedCompany;
        final String[] userId = new String[1];
        final String[] companyId = new String[1];
        Optional<UserLinkedCompany> linkedCompanyOptional;
        Optional.ofNullable(approvalBean.getReferenceId()).ifPresent( value -> {
           String[] responseArray = value.split("&");
           userId[0] = responseArray[0];
           companyId[0] = responseArray[1];
        });
        CompanyMst companyMstId = new CompanyMst();
        companyMstId.setCompanyId(companyId[0]);
        UserMst userMstId = new UserMst();
        userMstId.setUserId(Long.parseLong(userId[0]));
        linkedCompanyOptional = userLinkCompanyRepository.findByCompanyMstAndUserMst(companyMstId, userMstId);
        try {
            BeanUtils.copyProperties(linkedCompanyOptional, userLinkRequest);
        }catch (Exception exception){
            logger.error("createUserRequest to userMst data mapping error {}", exception.toString());
            throw new SystemException(
                    messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
                    ErrorCode.DATA_COPY_ERROR);
        }
        CompanyMst companyMst =  new CompanyMst();
        companyMst.setCompanyId(String.valueOf(companyId[0]));
        UserMst userMst =  new UserMst();
        userMst.setUserId(Long.valueOf(String.valueOf(userId[0])));
        userLinkedCompany = buildUserLinkedCompany(userLinkRequest, userMst, companyMst);
        saveMasterLinkTable(userLinkRequest, userLinkedCompany, approvalBean, commonTemp);
        callUserCreation(userMst, userLinkRequest);
    }

    private UserLinkedCompany buildUserLinkedCompany(UserLinkRequest userLinkRequest, UserMst userMst, CompanyMst companyMst) {
        UserLinkedCompany userLinkedCompany = new UserLinkedCompany();
        userLinkedCompany.setCompanyMst(companyMst);
        userLinkedCompany.setUserMst(userMst);
        userLinkedCompany.setAllAcctAccessFlg(userLinkRequest.getAllAccountAccessFlag());
        return userLinkedCompany;
    }

    private void saveMasterLinkTable(UserLinkRequest userLinkRequest, UserLinkedCompany userLinkedCompany, ApprovalBean approvalBean, TempDto commonTemp){

        userLinkedCompany.setRecordStatus(RecordStatuUsersEnum.VERIFIED);
        userLinkedCompany.setUserCompanyAccounts(userLinkRequest.getUserAccountBeans() != null ? setUserCompanyAccount(userLinkRequest, userLinkedCompany) : null);
        userLinkedCompany.setUserCompanyFeatures(userLinkRequest.getUserFeatureBeans() != null ? setUserCompanyFeature(userLinkRequest, userLinkedCompany) : null);
        userLinkedCompany.setCreatedBy(commonTemp.getCreatedBy());
        userLinkedCompany.setCreatedDate(commonTemp.getCreatedDate());
        userLinkedCompany.setLastVerifiedBy(approvalBean.getVerifiedBy());
        userLinkedCompany.setLastVerifiedDate(new Date());
        if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())){
            userLinkedCompany.setLastUpdatedBy(null);
            userLinkedCompany.setLastUpdatedDate(null);
        }else{
            userLinkedCompany.setLastUpdatedBy(commonTemp.getLastUpdatedBy());
            userLinkedCompany.setLastUpdatedDate(commonTemp.getLastUpdatedDate());
        }
        userLinkCompanyRepository.save(userLinkedCompany);
    }

    private void callUserCreation (UserMst userMst , UserLinkRequest userLinkRequest){

        InsertUpdateUserRequest insertUpdateUserRequest = new InsertUpdateUserRequest();
        insertUpdateUserRequest.setCompanyId(userMst.getCompanyId());
        insertUpdateUserRequest.setUserId(userMst.getUserId());
        Set<UserWorkFlowGroupsBean> workFlowGroupsBeans = userLinkRequest.getUserWorkFlowGroupBeans();
        Set<GroupsRequest> groupsRequestSet = new HashSet<>();
        workFlowGroupsBeans.forEach(values -> {
            GroupsRequest groupsRequest = new GroupsRequest();
            groupsRequest.setGroupId(values.getGroupId());
            groupsRequestSet.add(groupsRequest);
        });
        insertUpdateUserRequest.setGroups(groupsRequestSet);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<InsertUpdateUserRequest> request = new HttpEntity<>(insertUpdateUserRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        String URL = addUserURL;
//        String URL = "http://localhost:8080/v1/groups/user-batch";
        restTemplate.exchange(URL, HttpMethod.POST, request,CommonResponse.class);
    }
    private Set<UserCompanyAccount> setUserCompanyAccount(UserLinkRequest userLinkRequest, UserLinkedCompany userLinkedCompany){
        Set<UserAccountsBean> userAccountBeans = userLinkRequest.getUserAccountBeans();
        Set<UserCompanyAccount> userCompanyAccounts =  new HashSet<>();
        for ( UserAccountsBean userAccountsBeanList : userAccountBeans) {
            UserCompanyAccount userCompanyAccount = new UserCompanyAccount();
            userCompanyAccount.setAccountNo(userAccountsBeanList.getAccountId());
            userCompanyAccount.setUserLinkedCompanyAccount(userLinkedCompany);
            userCompanyAccounts.add(userCompanyAccount);
        }
        return userCompanyAccounts;
    }
    private Set<UserCompanyFeature> setUserCompanyFeature(UserLinkRequest userLinkRequest, UserLinkedCompany userLinkedCompany){
        Set<UserFeaturesBean> userFeatureBeans = userLinkRequest.getUserFeatureBeans();
        Set<UserCompanyFeature> userCompanyFeatures =  new HashSet<>();
        for ( UserFeaturesBean userFeaturesBeanList : userFeatureBeans) {
            UserCompanyFeature userCompanyFeature = new UserCompanyFeature();
            userCompanyFeature.setFeature(userFeaturesBeanList.getFeatureId());
            userCompanyFeature.setUserLinkedCompanyFeature(userLinkedCompany);
            userCompanyFeatures.add(userCompanyFeature);
        }
        return userCompanyFeatures;
    }
}
