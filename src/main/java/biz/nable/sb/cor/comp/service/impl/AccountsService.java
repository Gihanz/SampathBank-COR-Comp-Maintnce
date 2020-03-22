package biz.nable.sb.cor.comp.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.AccountsRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.response.CompanyAccountResponse;
import biz.nable.sb.cor.comp.utility.ErrorCode;

@Service
public class AccountsService {
	@Autowired
	AccountsRepository accountsRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	MessageSource messageSource;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonGetListResponse<CompanyAccountResponse> getAccountListByCompanyId(String companyId) {
		logger.info("start fetching accounts for companyId : {}", companyId);
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);

		if (!optional.isPresent()) {
			throw new SystemException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		}
		CommonGetListResponse<CompanyAccountResponse> commonGetListResponse = new CommonGetListResponse<>();
		for (CompanyAccountMst companyAccountMst : optional.get().getCompanyAccounts()) {
			CompanyAccountResponse accountResponse = new CompanyAccountResponse();
			accountResponse.setAccountName(companyAccountMst.getAccountName());
			accountResponse.setCurrency(companyAccountMst.getCurrency());
			accountResponse.setAccountNumber(companyAccountMst.getAccountNo());
			accountResponse.setClosedFlag(companyAccountMst.getAcctClsFlag());
			accountResponse.setBalance(companyAccountMst.getAccBalance());
			accountResponse.setAccOpenDate(companyAccountMst.getAcctOpenDate());

			commonGetListResponse.getPayLoad().add(accountResponse);
		}
		logger.info("end fetching accounts");
		commonGetListResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonGetListResponse.setReturnCode(HttpStatus.OK.value());
		commonGetListResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		return commonGetListResponse;
	}

}
