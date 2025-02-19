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
import biz.nable.sb.cor.comp.bean.UserWorkFlowGroupsBean;
import biz.nable.sb.cor.comp.component.UserTempComponent;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.UserMstHistoryRepository;
import biz.nable.sb.cor.comp.db.repository.UserMstRepository;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.bean.UserAccountsBean;
import biz.nable.sb.cor.comp.bean.UserFeaturesBean;
import biz.nable.sb.cor.comp.thirdparty.GroupsRequest;
import biz.nable.sb.cor.comp.thirdparty.InsertUpdateUserRequest;
import biz.nable.sb.cor.comp.utility.*;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Transactional
@Component
public class UserApproval implements CommonApprovalTemplate {

    @Autowired
    UserTempComponent userTempComponent;

    @Autowired
    UserMstRepository userMstRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CommonConverter commonConverter;

    @Autowired
    UserMstHistoryRepository userMstHistoryRepository;

    @Value("${add.user.url}")
    private String addUserURL;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CommonResponse doApprove(ApprovalBean approvalBean) {
        logger.info("================== Start user approval process =================");
        logger.info("User DoApprove method request: {}", approvalBean);
        CommonResponse commonResponse = new CommonResponse();
        ApprovalResponseBean approvalResponseBean = userTempComponent.doApprove(approvalBean);
        TempDto commonTemp = approvalResponseBean.getTempDto();
        if (ApprovalStatus.VERIFIED.name().equalsIgnoreCase(approvalBean.getApprovalStatus())) {
            if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
                addToUserMaster(approvalBean, commonTemp);
            } else if (ActionTypeEnum.UPDATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
                updateUserMaster(approvalBean, commonTemp);
            } else if (ActionTypeEnum.DELETE.name().equalsIgnoreCase(approvalBean.getActionType())) {
                deleteFromUserMst(approvalBean, commonTemp);
            }
        }else {
            changeMstUserStatus(approvalBean,commonTemp);
        }
        commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
        commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        commonResponse.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
        logger.info("User DoApprove method success response: {}", commonResponse);
        logger.info("================== Start user approval process =================");
        return commonResponse;
    }

    private void changeMstUserStatus(ApprovalBean approvalBean, TempDto tempDto) {
        logger.info("================== Start change master user status request =================");
        if (!ActionTypeEnum.CREATE.name().equals(approvalBean.getActionType())) {
            CreateUserRequest createUserRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),CreateUserRequest.class);
            Optional<UserMst> userMstOptional = userMstRepository.findByUserId(createUserRequest.getUserId());
            if (!userMstOptional.isPresent()) {
                throw new SystemException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
                        LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
            }
            UserMst userMst = userMstOptional.get();
            userMst.setRecordStatus(RecordStatuUsersEnum.VERIFIED);
            userMstRepository.save(userMst);
            logger.info("================== End change master user status request =================");
        }
    }
    private void addToUserMaster(ApprovalBean approvalBean, TempDto tempDto) {
        logger.info("================== Start user addToUserMaster process =================");
        CreateUserRequest createUserRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),CreateUserRequest.class);
        UserMst userMst = new UserMst();
        logger.info("Start Inserting a new user");
//        userMst = new UserMst();
        userMst = setUserMasterTable(approvalBean, createUserRequest, userMst);
        userMst.setCreatedBy(approvalBean.getEnteredBy());
        userMst.setCreatedDate(approvalBean.getEnteredDate());
        userMst.setLastUpdatedBy(createUserRequest.getLastModifiedBy());
        userMst.setLastUpdatedDate(createUserRequest.getLastModifiedDate());
        userMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
        userMst.setLastVerifiedDate(new Date());
        userMst.setStatus(StatusUserEnum.ACTIVE);
        userMstRepository.save(userMst);
        Optional<UserMst> userMstOptional = userMstRepository.findByApprovalId(Long.parseLong(approvalBean.getApprovalId()));
        callUserCreation(userMstOptional, createUserRequest);
    }

    private void callUserCreation(Optional<UserMst> userMstOptional, CreateUserRequest createUserRequest){

        try {
            InsertUpdateUserRequest insertUpdateUserRequest = new InsertUpdateUserRequest();
            insertUpdateUserRequest.setCompanyId(userMstOptional.get().getCompanyId());
            insertUpdateUserRequest.setUserId(userMstOptional.get().getUserId());
            Set<UserWorkFlowGroupsBean> workFlowGroupsBeans = createUserRequest.getUserWorkFlowGroupBeans();
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

            String URL;
            URL = addUserURL;
            restTemplate.exchange(URL, HttpMethod.POST, request,CommonResponse.class);

        }catch (Exception exception){
            logger.error("InsertUpdateUser API Calling Error: {}", exception.toString());
        }
    }

    private UserMst setUserMasterTable(ApprovalBean approvalBean, CreateUserRequest createUserRequest, UserMst userMst){

        userMst.setUserId(createUserRequest.getUserId());
        userMst.setUserName(createUserRequest.getUserName());
        userMst.setDesignation(createUserRequest.getDesignation());
        userMst.setBranch(createUserRequest.getBranchCode());
        userMst.setEmail(createUserRequest.getEmail());
        userMst.setUserType(createUserRequest.getUserType());
        userMst.setCompanyId(createUserRequest.getPrimaryCompanyId());
        userMst.setAllAcctAccessFlg(createUserRequest.getAllAccountAccessFlag());
        userMst.setIamCreateState(CreateState.ACTIVATED);
//        userMst.setUserLinkedCompanies(null);
        userMst.setUserPrimaryAccounts(createUserRequest.getUserAccountBeans() != null ? setUserPrimaryAccount(createUserRequest, userMst) : null);
        userMst.setUserPrimaryFeatures(createUserRequest.getUserFeatureBeans() != null ? setUserPrimaryFeature(createUserRequest, userMst) : null);
        userMst.setApprovalId(Long.parseLong(approvalBean.getApprovalId()));
        userMst.setRecordStatus(RecordStatuUsersEnum.VERIFIED);
        return userMst;
    }

    private Set<UserPrimaryAccount> setUserPrimaryAccount(CreateUserRequest createUserRequest, UserMst userMst){
        Set<UserAccountsBean> userAccountBeans = createUserRequest.getUserAccountBeans();
        Set<UserPrimaryAccount> userPrimaryAccountSet =  new HashSet<>();
        for ( UserAccountsBean userAccountsBeanList : userAccountBeans) {
            UserPrimaryAccount userPrimaryAccount = new UserPrimaryAccount();
            userPrimaryAccount.setAccountNo(userAccountsBeanList.getAccountId());
            userPrimaryAccount.setUserMstAcc(userMst);
            userPrimaryAccountSet.add(userPrimaryAccount);
        }
        return userPrimaryAccountSet;
    }

    private Set<UserPrimaryFeature> setUserPrimaryFeature(CreateUserRequest createUserRequest, UserMst userMst){
        Set<UserFeaturesBean> userFeatureBeans = createUserRequest.getUserFeatureBeans();
        Set<UserPrimaryFeature> userPrimaryFeatureSet =  new HashSet<>();
        for ( UserFeaturesBean userFeaturesBeanList : userFeatureBeans) {
            UserPrimaryFeature userPrimaryFeature = new UserPrimaryFeature();
            userPrimaryFeature.setFeature(userFeaturesBeanList.getFeatureId());
            userPrimaryFeature.setUserMstFea(userMst);
            userPrimaryFeatureSet.add(userPrimaryFeature);
        }
        return userPrimaryFeatureSet;
    }

    private void updateUserMaster(ApprovalBean approvalBean, TempDto tempDto){
        logger.info("================== Start user updateUserMaster process =================");
        CreateUserRequest createUserRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),CreateUserRequest.class);
        Optional<UserMst> userMasterResponse = userMstRepository.findByUserId(createUserRequest.getUserId());
        UserMst userMst = new UserMst();
        if (!userMasterResponse.isPresent()) {
            throw new SystemException(
                    messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
                    ErrorCode.NO_COMPANY_RECORD_FOUND);
        }else{
            userMst = setUserMasterTable(approvalBean, createUserRequest, userMst);
            userMst.setCreatedBy(approvalBean.getEnteredBy());
            userMst.setCreatedDate(approvalBean.getEnteredDate());
            userMst.setLastUpdatedBy(createUserRequest.getLastModifiedBy());
            userMst.setLastUpdatedDate(createUserRequest.getLastModifiedDate());
            userMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
            if (userMasterResponse.get().getStatus().equals(StatusUserEnum.BLOCKED)){
                userMst.setStatus(StatusUserEnum.BLOCKED);
            }else {
                userMst.setStatus(StatusUserEnum.ACTIVE);
            }
            userMst.setLastVerifiedDate(new Date());
            logger.info("================== End user updateUserMaster process =================");
            userMstRepository.save(userMst);
            Optional<UserMst> userMstOptional = userMstRepository.findByUserId(userMst.getUserId());
            callUserCreation(userMstOptional, createUserRequest);
        }


    }

    private void deleteFromUserMst(ApprovalBean approvalBean, TempDto tempDto) {
        logger.info("================== Start delete from user master process =================");
        long userID = Long.parseLong(tempDto.getRequestPayload().get("userId").toString());
        Optional<UserMst> optionalUserMst = userMstRepository.findByUserId(userID);
        Set<UserMst> userMst = new HashSet<>();
        UserMstHistory userMstHistory = new UserMstHistory();
        UserMst userMstlist = new UserMst();
//        Optional.ofNullable(optionalUserMst).ifPresent( value -> {
//            userMst.add(optionalUserMst);
//        });
        try {
            BeanUtils.copyProperties(userMstHistory, optionalUserMst);
            userMstHistory.setLastVerifiedBy(approvalBean.getVerifiedBy());
            userMstHistory.setLastVerifiedDate(new Date());
            userMstHistory.setLastModifiedBy(tempDto.getLastUpdatedBy());
            userMstHistory.setLastModifiedDate(tempDto.getLastUpdatedDate());
            userMstHistory.setRequestPayload(tempDto.getRequestPayload());
            userMstHistory.setActionType(tempDto.getActionType());
            userMstHistory.setApprovalId(tempDto.getApprovalId());
            BeanUtils.copyProperties(userMstlist, userMstHistory);
        }catch (IllegalAccessException | InvocationTargetException exception){
            logger.error("UserMst to UserMstHistory data mapping error {}", exception.toString());
            throw new SystemException(
                    messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
                    ErrorCode.DATA_COPY_ERROR);
        }
        userMstHistoryRepository.save(userMstHistory);
        userMstRepository.deleteById(optionalUserMst.get().getUserId());
        logger.info("================== End delete from user master process =================");
    }
}
