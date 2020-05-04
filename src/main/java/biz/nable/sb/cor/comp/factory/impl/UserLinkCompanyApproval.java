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
import biz.nable.sb.cor.comp.bean.*;
import biz.nable.sb.cor.comp.component.UserLinkCompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.UserLinkCompanyRepository;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
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
            changeMstUserStatus(approvalBean, commonTemp);
        }
        commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
        commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        commonResponse.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
        logger.info("User DoApprove method success response: {}", commonResponse);
        return commonResponse;

    }

    private void changeMstUserStatus(ApprovalBean approvalBean, TempDto commonTemp) {
        logger.info("================== Start change master user status request =================");
        if (!ActionTypeEnum.CREATE.name().equals(approvalBean.getActionType())) {
//            UserLinkRequest userLinkRequest = commonConverter.mapToPojo(commonTemp.getRequestPayload(), UserLinkRequest.class);
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
            if (!linkedCompanyOptional.isPresent()) {
                throw new SystemException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
                        LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
            }
            UserLinkedCompany userLinkedCompany = linkedCompanyOptional.get();
            userLinkedCompany.setRecordStatus(RecordStatuUsersEnum.VERIFIED);
            userLinkCompanyRepository.save(userLinkedCompany);
            logger.info("================== End change master user status request =================");
        }
    }

    private void addUserToLinkCompanyMst(ApprovalBean approvalBean, TempDto commonTemp){
        final String[] userId = new String[1];
        UserLinkRequest userLinkRequest = commonConverter.mapToPojo(commonTemp.getRequestPayload(),
                UserLinkRequest.class);
        UserLinkedCompany userLinkedCompany;
        Map<String, Object> requestPayload = commonTemp.getRequestPayload();
        String[] responseArray = commonTemp.getReferenceNo().split("&");
        userId[0] = responseArray[0];
        CompanyMst companyMst =  new CompanyMst();
        companyMst.setCompanyId(String.valueOf(requestPayload.get("companyId")));
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

        CompanyMst companyMst =  new CompanyMst();
        companyMst.setCompanyId(String.valueOf(companyId[0]));
        UserMst userMst =  new UserMst();
        userMst.setUserId(Long.valueOf(userId[0]));
        userLinkRequest.setUserLinkId(linkedCompanyOptional.get().getLinkId());
        userLinkRequest.setCreatedBy(linkedCompanyOptional.get().getCreatedBy());
        userLinkRequest.setCreatedDate(linkedCompanyOptional.get().getCreatedDate());
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
        userLinkedCompany.setUserCompanyAccounts(userLinkRequest.getUserCompanyAccounts() != null ? setUserCompanyAccount(userLinkRequest, userLinkedCompany) : null);
        userLinkedCompany.setUserCompanyFeatures(userLinkRequest.getUserCompanyFeatures() != null ? setUserCompanyFeature(userLinkRequest, userLinkedCompany) : null);
        userLinkedCompany.setLastVerifiedBy(approvalBean.getVerifiedBy());
        userLinkedCompany.setLastVerifiedDate(new Date());
        if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())){
            userLinkedCompany.setLastUpdatedBy(null);
            userLinkedCompany.setLastUpdatedDate(null);
            userLinkedCompany.setCreatedBy(commonTemp.getCreatedBy());
            userLinkedCompany.setCreatedDate(commonTemp.getCreatedDate());
        }else{
            userLinkedCompany.setLastUpdatedBy(commonTemp.getLastUpdatedBy());
            userLinkedCompany.setLastUpdatedDate(commonTemp.getLastUpdatedDate());
            userLinkedCompany.setLinkId(userLinkRequest.getUserLinkId());
            userLinkedCompany.setCreatedBy(userLinkRequest.getCreatedBy());
            userLinkedCompany.setCreatedDate(userLinkRequest.getCreatedDate());
        }
        userLinkCompanyRepository.save(userLinkedCompany);
    }

    private void callUserCreation (UserMst userMst , UserLinkRequest userLinkRequest){
        InsertUpdateUserRequest insertUpdateUserRequest = new InsertUpdateUserRequest();
        insertUpdateUserRequest.setCompanyId(userMst.getCompanyId());
        insertUpdateUserRequest.setUserId(userMst.getUserId());
        Set<UserCompanyWorkflowGroupsBean> workFlowGroupsBeans = userLinkRequest.getUserCompanyWorkflowGroups();
        Set<GroupsRequest> groupsRequestSet = new HashSet<>();
        workFlowGroupsBeans.forEach(values -> {
            GroupsRequest groupsRequest = new GroupsRequest();
            groupsRequest.setGroupId(values.getUserGroupId());
            groupsRequestSet.add(groupsRequest);
        });
        insertUpdateUserRequest.setGroups(groupsRequestSet);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<InsertUpdateUserRequest> request = new HttpEntity<>(insertUpdateUserRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        String URL = addUserURL;
        restTemplate.exchange(URL, HttpMethod.POST, request,CommonResponse.class);
    }

    private Set<UserCompanyAccount> setUserCompanyAccount(UserLinkRequest userLinkRequest, UserLinkedCompany userLinkedCompany){
        Set<UserCompanyAccountsBean> userAccountBeans = userLinkRequest.getUserCompanyAccounts();
        Set<UserCompanyAccount> userCompanyAccounts =  new HashSet<>();
        for ( UserCompanyAccountsBean userAccountsBeanList : userAccountBeans) {
            UserCompanyAccount userCompanyAccount = new UserCompanyAccount();
            userCompanyAccount.setAccountNo(userAccountsBeanList.getAccountNumber());
            userCompanyAccount.setUserLinkedCompanyAccount(userLinkedCompany);
            userCompanyAccounts.add(userCompanyAccount);
        }
        return userCompanyAccounts;
    }
    private Set<UserCompanyFeature> setUserCompanyFeature(UserLinkRequest userLinkRequest, UserLinkedCompany userLinkedCompany){
        Set<UserCompanyFeaturesBean> userFeatureBeans = userLinkRequest.getUserCompanyFeatures();
        Set<UserCompanyFeature> userCompanyFeatures =  new HashSet<>();
        for ( UserCompanyFeaturesBean userFeaturesBeanList : userFeatureBeans) {
            UserCompanyFeature userCompanyFeature = new UserCompanyFeature();
            userCompanyFeature.setFeature(userFeaturesBeanList.getFeatureId());
            userCompanyFeature.setUserLinkedCompanyFeature(userLinkedCompany);
            userCompanyFeatures.add(userCompanyFeature);
        }
        return userCompanyFeatures;
    }
}
