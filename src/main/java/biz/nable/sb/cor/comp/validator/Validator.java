/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.validator;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;
import biz.nable.sb.cor.comp.db.entity.CompanyFeatures;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.entity.UserMst;
import biz.nable.sb.cor.comp.db.repository.AccountsRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyFeaturesRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.db.repository.UserMstRepository;
import biz.nable.sb.cor.comp.request.CreateCompanyRequest;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/*
 * @Description	:This validator class is to validate Approval related parameters.
 */

@Repository
public class Validator {

	@Autowired
	AccountsRepository accountsRepository;

	@Autowired
	CompanyFeaturesRepository companyFeaturesRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	UserMstRepository userMstRepository;



	Logger logger = LoggerFactory.getLogger(this.getClass());


	public static boolean createCompanyValidateRequest(CreateCompanyRequest createCompanyRequest) {
		boolean isSuccess = false;

		if (!StringUtils.isEmpty(createCompanyRequest.getCompanyName())
				&& null != createCompanyRequest.getCompanyName()) {
			isSuccess = true;
		}
		return isSuccess;
	}

	public CommonResponse validateRequest(CreateUserRequest createUserRequest){
		CompanyMst companyMst = new CompanyMst();
		CommonResponse commonResponse = new CommonResponse();
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(createUserRequest.getPrimaryCompanyId());
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			return commonResponse;
		}
		if (createUserRequest.getAllAccountAccessFlag().equals("N")) {
			companyMst.setCompanyId(optional.get().getCompanyId());
			Optional<CompanyAccountMst> companyAccountMst = accountsRepository.findByCompanyId(companyMst);
			if (!companyAccountMst.isPresent()) {
				logger.info(
						messageSource.getMessage(ErrorCode.NO_COMPANY_ACCOUNT_FOUND, null, LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.NO_COMPANY_ACCOUNT_FOUND);
				commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
				commonResponse.setReturnMessage(
						messageSource.getMessage(ErrorCode.NO_COMPANY_ACCOUNT_FOUND, null, LocaleContextHolder.getLocale()));
				return commonResponse;
			}
		}
		companyMst.setCompanyId(optional.get().getCompanyId());
		Optional<CompanyAccountMst> companyAccountMstAll = accountsRepository.findByCompanyId(companyMst);
		AtomicLong company = new AtomicLong();
		companyAccountMstAll.ifPresent(companyAccountMst -> company.set(companyAccountMst.getId()));
		Optional<CompanyFeatures> responseFeature = getCompanyFeatures(company.get());
		if (!responseFeature.isPresent()){
			logger.info(
					messageSource.getMessage(ErrorCode.NO_FEATURE_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_FEATURE_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_FEATURE_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			return commonResponse;
		}
		Optional<UserMst> userMaster = userMstRepository.findByUserName(createUserRequest.getUserName());
		Boolean isExist = userMaster.isPresent();
		if (Boolean.TRUE.equals(isExist)) {
			logger.info(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.USER_RECORD_ALREADY_EXISTS);
			commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
			commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()));
			return commonResponse;
		}
//		if (StringUtils.isEmpty(userId)) {
//			logger.error(ErrorDescription.INVALID_USER_LOGGING_MASSAGE, userId);
//			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
//					messageSource.getMessage(biz.nable.sb.cor.common.utility.ErrorCode.INVALID_USER_ID, new Object[] { userId },
//							LocaleContextHolder.getLocale()),
//					biz.nable.sb.cor.common.utility.ErrorCode.INVALID_USER_ID);
//		}
		return commonResponse;
	}

	private Optional<CompanyFeatures> getCompanyFeatures(long companyID){
		CompanyMst companyMst = new CompanyMst();
		companyMst.setCompanyId(String.valueOf(companyID));
		return companyFeaturesRepository.findByCompany(companyMst);
	}
}
