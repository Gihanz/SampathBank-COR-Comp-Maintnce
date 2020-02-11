package biz.nable.sb.cor.comp.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;
import biz.nable.sb.cor.comp.db.repository.AccountsRepository;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.response.CompanyAccountResponse;

@Service
public class AccountsService {
	@Autowired
	AccountsRepository accountsRepository;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonGetListResponse<CompanyAccountResponse> getAccountListByCompanyId(String companyId) {
		logger.info("start fetching accounts for companyId : {}", companyId);
		List<CompanyAccountMst> companyAccountMsts = accountsRepository.findByCompanyId(companyId);
		CommonGetListResponse<CompanyAccountResponse> commonGetListResponse = new CommonGetListResponse<>();
		for (CompanyAccountMst companyAccountMst : companyAccountMsts) {
			CompanyAccountResponse accountResponse = new CompanyAccountResponse();
			try {
				BeanUtils.copyProperties(accountResponse, companyAccountMst);
				commonGetListResponse.getPayLoad().add(accountResponse);
			} catch (IllegalAccessException e) {
				logger.error("IllegalAccessException occred while Data copping error", e);
			} catch (InvocationTargetException e) {
				logger.error("InvocationTargetException occred while Data copping", e);
			}
		}
		logger.info("end fetching accounts");
		return commonGetListResponse;
	}

}
