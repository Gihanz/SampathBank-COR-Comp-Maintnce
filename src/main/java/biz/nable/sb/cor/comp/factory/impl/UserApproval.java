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
import biz.nable.sb.cor.comp.component.UserTempComponent;
import biz.nable.sb.cor.comp.db.entity.*;
import biz.nable.sb.cor.comp.db.repository.UserMstHistoryRepository;
import biz.nable.sb.cor.comp.db.repository.UserMstRepository;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.bean.UserAccountsBean;
import biz.nable.sb.cor.comp.bean.UserFeaturesBean;
import biz.nable.sb.cor.comp.utility.*;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
            changeMstUserStatus(approvalBean);
        }
        commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
        commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
        commonResponse.setReturnMessage(
                messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
        logger.info("User DoApprove method success response: {}", commonResponse);
        logger.info("================== Start user approval process =================");
        return commonResponse;
    }

    private void changeMstUserStatus(ApprovalBean approvalBean) {
        logger.info("================== Start change master user status request =================");
        if (!ActionTypeEnum.CREATE.name().equals(approvalBean.getActionType())) {
            Optional<UserMst> userMstOptional = userMstRepository.findByUserId(Long.parseLong(approvalBean.getReferenceId()));
            if (!userMstOptional.isPresent()) {
                throw new SystemException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
                        LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
            }
            UserMst userMst = userMstOptional.get();
            userMstRepository.save(userMst);
            logger.info("================== End change master user status request =================");
        }
    }
    private void addToUserMaster(ApprovalBean approvalBean, TempDto tempDto) {
        logger.info("================== Start user addToUserMaster process =================");
        CreateUserRequest createUserRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),CreateUserRequest.class);
//        Optional<UserMst> UserMasterResponse = userMstRepository.findByUserId(approvalBean.getReferenceId());
        UserMst userMst;
//        if (UserMasterResponse.isPresent()) {
//            logger.info("User record already exist: ErrorCode: {} ErrorDescription: {}",
//                    ErrorCode.USER_RECORD_ALREADY_EXISTS, ErrorDescription.USER_RECORD_ALREADY_EXISTS);
//            throw new SystemException(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS, null,
//                    LocaleContextHolder.getLocale()), ErrorCode.USER_RECORD_ALREADY_EXISTS);
//        }
        logger.info("Start Inserting a new user");
        userMst = new UserMst();
        userMst = setUserMasterTable(approvalBean, createUserRequest, userMst);
        userMst.setCreatedBy(approvalBean.getEnteredBy());
        userMst.setCreatedDate(approvalBean.getEnteredDate());
        userMst.setLastUpdatedBy(createUserRequest.getLastModifiedBy());
        userMst.setLastUpdatedDate(createUserRequest.getLastModifiedDate());
        userMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
        userMst.setLastVerifiedDate(new Date());
        userMstRepository.save(userMst);
    }

    private UserMst setUserMasterTable(ApprovalBean approvalBean, CreateUserRequest createUserRequest, UserMst userMst){

//        return UserMst.builder()
        userMst.setUserName(createUserRequest.getUserName());
        userMst.setDesignation(createUserRequest.getDesignation());
        userMst.setBranch(createUserRequest.getBranchCode());
        userMst.setEmail(createUserRequest.getEmail());
        userMst.setUserType(createUserRequest.getUserType());
        userMst.setCompanyId(createUserRequest.getPrimaryCompanyId());
        userMst.setAllAcctAccessFlg(createUserRequest.getAllAccountAccessFlag());
        userMst.setIamCreateState(CreateState.ACTIVATED);
        userMst.setUserLinkedCompanies(null);
        userMst.setUserPrimaryAccounts(createUserRequest.getUserAccountBeans() != null ? setUserPrimaryAccount(createUserRequest, userMst) : null);
        userMst.setUserPrimaryFeatures(createUserRequest.getUserFeatureBeans() != null ? setUserPrimaryFeature(createUserRequest, userMst) : null);
        userMst.setApprovalId(Long.parseLong(approvalBean.getApprovalId()));
        userMst.setRecordStatus(RecordStatuUsersEnum.VERIFIED);
        userMst.setStatus(StatusUserEnum.ACTIVE);
        return userMst;
//                .build();
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
        Optional<UserMst> userMasterResponse = userMstRepository.findByUserId(Long.parseLong(approvalBean.getReferenceId()));
        UserMst userMst;
        if (!userMasterResponse.isPresent()) {
            throw new SystemException(
                    messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
                    ErrorCode.NO_COMPANY_RECORD_FOUND);
        }
        userMst = userMasterResponse.get();

        try {
            BeanUtils.copyProperties(userMst, createUserRequest);
            userMst = setUserMasterTable(approvalBean, createUserRequest, userMst);
        }catch (Exception exception){
            logger.error("createUserRequest to userMst data mapping error {}", exception.toString());
            throw new SystemException(
                    messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
                    ErrorCode.DATA_COPY_ERROR);
        }
        logger.info("================== End user updateUserMaster process =================");
        userMstRepository.save(userMst);
    }

    private void deleteFromUserMst(ApprovalBean approvalBean, TempDto tempDto) {
        logger.info("================== Start delete from user master process =================");
        Set<UserMst> optionalUserMst = userMstRepository.findByCompanyId(approvalBean.getReferenceId());
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
        userMstRepository.delete(userMstlist);
        logger.info("================== End delete from user master process =================");
    }
}
