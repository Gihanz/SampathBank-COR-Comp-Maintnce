package biz.nable.sb.cor.comp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.SyncAllAccountRequest;
import biz.nable.sb.cor.comp.component.FinacleConnector;
import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.AccountsRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.exception.FinacleCallException;
import biz.nable.sb.cor.comp.soap.schemas.iib.CustomerInfoRecord;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetCustomerInfoResponseType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetGenInqResponseType;
import biz.nable.sb.cor.comp.utility.ErrorCode;

@Service
public class SyncAccounts {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AccountsRepository accountRepo;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	FinacleConnector finacleConnector;

	@Autowired
	MessageSource messageSource;

	@Value("${account.sync.enable}")
	private String isSyncEnable;

	@Async("asyncAccountSyncExecutor")
	public void syncAllAccounts(SyncAllAccountRequest request) {
		if ("Y".equalsIgnoreCase(isSyncEnable)) {
			logger.info("start Account sync CustId : {}", request.getCustId());
			Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(request.getCustId());
			if (!optional.isPresent()) {
				throw new SystemException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
			}
			List<CompanyAccountMst> entities = optional.get().getCompanyAccounts();
			logger.info("Existing DebitAccount count:{} list: {}", entities.size(), entities);

			List<CompanyAccountMst> syncedList = new ArrayList<>();
			List<CompanyAccountMst> syncErrorList = new ArrayList<>();
			GetCustomerInfoResponseType customerInfoResponse = finacleConnector.getCustomerInfo(request.getCustId());
			if (null == customerInfoResponse || !"000".equals(customerInfoResponse.getActionCode())) {
				String actionCode = null != customerInfoResponse ? customerInfoResponse.getActionCode() : "null";
				logger.info("Invalid finacle response actionCode: {}", actionCode);
				throw new FinacleCallException("Invalid finacle response");
			}
			logger.info("Finacle Account count:{} list: {}", customerInfoResponse.getCustomerInfoRecord().size(),
					customerInfoResponse.getCustomerInfoRecord());
			if (!customerInfoResponse.getCustomerInfoRecord().isEmpty()) {
				customerInfoResponse.getCustomerInfoRecord().forEach(finacleModle -> {
					CompanyAccountMst debitAccountEntity = entities.stream()
							.filter(entitie -> finacleModle.getAcctNo().equals(entitie.getAccountNo())).findAny()
							.orElse(new CompanyAccountMst());

					GetGenInqResponseType genInqResponseType = finacleConnector.getGenInq(finacleModle.getAcctNo());
					if (null != genInqResponseType && "000".equals(genInqResponseType.getActionCode())) {
						syncAccont(debitAccountEntity, finacleModle, genInqResponseType);
						syncedList.add(debitAccountEntity);
					} else {
						syncErrorList.add(debitAccountEntity);
						logger.error("Account sync error AccNo : {}", debitAccountEntity.getAccountNo());
					}
				});
			}
		} else {
			logger.info("Account sync call, Sync Disabled");
		}
	}

	private CommonResponse syncAccont(CompanyAccountMst debitAccountEntity, CustomerInfoRecord finacleModle,
			GetGenInqResponseType genInqResponseType) {
		logger.info("Start sync acconut accNo: {}", genInqResponseType.getAcctNo());
		if (null == debitAccountEntity) {
			debitAccountEntity = new CompanyAccountMst();
		}
		debitAccountEntity.setAccountNo(finacleModle.getAcctNo());
		debitAccountEntity.setAccountType(genInqResponseType.getAcctType());
		debitAccountEntity.setCurrency(
				(null != genInqResponseType.getCurrencyCode() || !genInqResponseType.getCurrencyCode().isEmpty())
						? genInqResponseType.getCurrencyCode()
						: "LKR");
		debitAccountEntity.setJointRecType(finacleModle.getJointRecType());
		debitAccountEntity.setSchemaCode(genInqResponseType.getSchemeCode());

		if (null == debitAccountEntity.getId()) {
			debitAccountEntity.setNickName(genInqResponseType.getSystemNickname());
			debitAccountEntity.setJointRecType("");
		}
		debitAccountEntity.setAccBalance(genInqResponseType.getAvailableBalance());
		debitAccountEntity.setJointSerial(finacleModle.getJointSerial());
		debitAccountEntity.setAccBalance(genInqResponseType.getAvailableBalance());
		debitAccountEntity.setJointHolderName(genInqResponseType.getJointHolders());
		debitAccountEntity.setOperMode(genInqResponseType.getModeOfOperation());
		debitAccountEntity.setAcctOpenDate(genInqResponseType.getAcctOpenDate());

		try {
			accountRepo.save(debitAccountEntity);
			logger.info("Account sync success");
			return new CommonResponse(HttpStatus.OK.value(), "Account sync success", ErrorCode.OPARATION_SUCCESS);
		} catch (Exception e) {
			logger.error("Account sync error", e);
			return new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Account sync error",
					ErrorCode.UNKNOWN_ERROR);
		}
	}
}
