package biz.nable.sb.cor.comp.factory.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import biz.nable.sb.cor.common.bean.ApprovalBean;
import biz.nable.sb.cor.common.bean.ApprovalResponseBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.template.CommonApprovalTemplate;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.ApprovalStatus;
import biz.nable.sb.cor.comp.bean.SyncAllAccountRequest;
import biz.nable.sb.cor.comp.component.CompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.CompanyDelete;
import biz.nable.sb.cor.comp.db.entity.CompanyFeatures;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMstHis;
import biz.nable.sb.cor.comp.db.entity.Features;
import biz.nable.sb.cor.comp.db.repository.CompanyDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyFeaturesRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstHstRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.db.repository.FeaturesRepository;
import biz.nable.sb.cor.comp.request.CreateCompanyRequest;
import biz.nable.sb.cor.comp.request.UpdateCompanyRequest;
import biz.nable.sb.cor.comp.service.impl.SyncAccounts;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;

@Transactional
@Component
public class CompanyApproval implements CommonApprovalTemplate {

	@Autowired
	private CompanyMstRepository companyMstRepository;
	@Autowired
	private CompanyDeleteRepository companyDeleteRepository;
	@Autowired
	private CompanyMstHstRepository companyMstHstRepository;
	@Autowired
	private FeaturesRepository featuresRepository;
	@Autowired
	private CompanyFeaturesRepository companyFeaturesRepository;
	@Autowired
	private CommonConverter commonConverter;
	@Autowired
	private MessageSource messageSource;

	@Autowired
	SyncAccounts syncAccounts;

	@Autowired
	CompanyTempComponent companyTempComponent;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse doApprove(ApprovalBean approvalBean) {
		logger.info("Start Company approval process");
		CommonResponse commonResponse = new CommonResponse();
		ApprovalResponseBean approvalResponseBean = companyTempComponent.doApprove(approvalBean);
		TempDto commonTemp = approvalResponseBean.getTempDto();
		if (ApprovalStatus.VERIFIED.name().equalsIgnoreCase(approvalBean.getApprovalStatus())) {
			if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				addToCompanyMst(approvalBean, commonTemp);

				logger.info("Start Sync accounts");
				SyncAllAccountRequest request = new SyncAllAccountRequest();
				request.setCustId(commonTemp.getReferenceNo());
				syncAccounts.syncAllAccounts(request);
			} else if (ActionTypeEnum.UPDATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				updateCompanyMst(approvalBean, commonTemp);
			} else if (ActionTypeEnum.DELETE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				deleteFromCompanyMst(approvalBean, commonTemp);
			}
		} else {
			changeMstStatus(approvalBean);
		}
		commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		return commonResponse;
	}

	private void changeMstStatus(ApprovalBean approvalBean) {
		if (!ActionTypeEnum.CREATE.name().equals(approvalBean.getActionType())) {
			Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(approvalBean.getReferenceId());
			if (!companyO.isPresent()) {
				throw new SystemException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
			}
			CompanyMst companyMst = companyO.get();
			companyMst.setRecordStatus(RecordStatusEnum.ACTIVE);
			companyMstRepository.save(companyMst);
		}
	}

	private void deleteFromCompanyMst(ApprovalBean approvalBean, TempDto tempDto) {
		Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(approvalBean.getReferenceId());
		CompanyMst companyMst;
		CompanyDelete companyDelete = new CompanyDelete();
		CompanyMstHis companyMstHis = new CompanyMstHis();
		if (companyO.isPresent()) {
			logger.info("Start Deleting existing company");
			companyMst = companyO.get();
		} else {
			logger.error("Company record not found for delete");
			companyMst = new CompanyMst();
		}

		try {
			BeanUtils.copyProperties(companyDelete, companyMst);
			companyDelete.setLastVerifiedBy(approvalBean.getVerifiedBy());
			companyDelete.setLastVerifiedDate(new Date());
			companyDelete.setLastUpdatedBy(tempDto.getLastUpdatedBy());
			companyDelete.setLastUpdatedDate(tempDto.getLastUpdatedDate());
			BeanUtils.copyProperties(companyMstHis, companyDelete);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("companyMst to companyMstHis data mapping error {}", e);
			throw new SystemException(
					messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
					ErrorCode.DATA_COPY_ERROR);
		}

		companyMstHis.setId(null);
		companyMstHis.setMstId(companyMst.getId());
		companyMstHstRepository.save(companyMstHis);
		companyDeleteRepository.save(companyDelete);
		companyMstRepository.delete(companyMst);
	}

	private void updateCompanyMst(ApprovalBean approvalBean, TempDto tempDto) {
		UpdateCompanyRequest updateCompanyRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
				UpdateCompanyRequest.class);
		Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(approvalBean.getReferenceId());
		CompanyMst companyMst;
		if (!companyO.isPresent()) {
			throw new SystemException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		}
		logger.info("Start updating existing company");
		companyMst = companyO.get();
		try {
			BeanUtils.copyProperties(companyMst, updateCompanyRequest);
			companyMst.setCompanyFeatures(null);
			addFeatureList(companyMst, updateCompanyRequest.getCompanyFeatures());
		} catch (Exception e) {
			logger.error("updateCompanyRequest to companyMst data mapping error {}", e);
			throw new SystemException(
					messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
					ErrorCode.DATA_COPY_ERROR);
		}
		saveCompany(companyMst, approvalBean, tempDto);
	}

	private void addToCompanyMst(ApprovalBean approvalBean, TempDto tempDto) {
		CreateCompanyRequest createCompanyRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
				CreateCompanyRequest.class);
		Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(approvalBean.getReferenceId());
		CompanyMst companyMst;
		if (companyO.isPresent()) {
			logger.info("Company Record already exist");
			throw new SystemException(messageSource.getMessage(ErrorCode.COMPANY_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()), ErrorCode.COMPANY_RECORD_ALREADY_EXISTS);
		}
		logger.info("Start Inserting a new company");
		companyMst = new CompanyMst();

		buildCompanyMst(companyMst, createCompanyRequest, approvalBean);
		saveCompany(companyMst, approvalBean, tempDto);

	}

	private void saveCompany(CompanyMst companyMst, ApprovalBean approvalBean, TempDto tempDto) {

		CompanyMstHis companyMstHis = new CompanyMstHis();
		companyMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
		companyMst.setUserGroup(tempDto.getUserGroup());
		companyMst.setLastVerifiedDate(new Date());
		companyMst.setAuthorizationId(Long.parseLong(approvalBean.getApprovalId()));
		companyMst.setLastUpdatedBy(tempDto.getLastUpdatedBy());
		companyMst.setLastUpdatedDate(tempDto.getLastUpdatedDate());
		companyMst.setCreatedBy(null == companyMst.getCreatedBy() ? tempDto.getCreatedBy() : companyMst.getCreatedBy());
		companyMst.setCreatedDate(
				null == companyMst.getCreatedDate() ? tempDto.getCreatedDate() : companyMst.getCreatedDate());
		companyMst
				.setCompanyId(null == companyMst.getCompanyId() ? tempDto.getReferenceNo() : companyMst.getCompanyId());
		companyMst.setRecordStatus(RecordStatusEnum.ACTIVE);

		if (null != companyMst.getId()) {
			companyFeaturesRepository.deleteFeaturesByCompany(companyMst);
		}
		companyMstRepository.save(companyMst);

		try {
			BeanUtils.copyProperties(companyMstHis, companyMst);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("companyMst to companyMstHis data mapping error {}", e);
			throw new SystemException(
					messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
					ErrorCode.DATA_COPY_ERROR);
		}
		companyMstHis.setId(null);
		companyMstHis.setMstId(companyMst.getId());
		companyMstHstRepository.save(companyMstHis);
	}

	private CompanyMst buildCompanyMst(CompanyMst companyMst, CreateCompanyRequest companyRequest,
			ApprovalBean approvalBean) {
		logger.info("Start buildCompanyMst");
		if (null != companyRequest.getParentCompanyId()) {
			Optional<CompanyMst> parentCompany = companyMstRepository.findById(companyRequest.getParentCompanyId());
			companyMst.setParentCompanyId(parentCompany.isPresent() ? parentCompany.get().getParentCompanyId() : null);
		}
		companyMst.setCompanyId(companyRequest.getCompanyId());
		companyMst.setCompanyName(companyRequest.getCompanyName());
		companyMst.setContactNo(companyRequest.getContactNo());
		companyMst.setEmailAddr(companyRequest.getEmailAddr());
		companyMst.setContactPerson(companyRequest.getContactPerson());
		companyMst.setEpfCode(companyRequest.getEpfCode());
		companyMst.setEpfAreaCode(companyRequest.getEpfAreaCode());
		companyMst.setEtfCode(companyRequest.getEtfCode());
		companyMst.setDistrictCode(companyRequest.getDistrictCode());
		companyMst.setCommTemplateId(companyRequest.getCommTemplateId());
		companyMst.setMcAuthFlg(companyRequest.getMcAuthFlg());
		companyMst.setTreasureCustRef(companyRequest.getTreasureCustRef());
		companyMst.setBulkDirectDebitFlg(companyRequest.getBulkDirectDebitFlg());
		companyMst.setWebServiceActivationFlag(companyRequest.getWebServiceActivationFlag());
		companyMst.setBulkPaymentLimit(companyRequest.getBulkPaymentLimit());
		companyMst.setWsIp(companyRequest.getWsIp());
		companyMst.setRequestId(companyRequest.getRequestId());
		companyMst.setCreatedDate(companyRequest.getCreateDate());
		companyMst.setCreatedBy(companyRequest.getCreateBy());
		companyMst.setUserGroup(companyRequest.getUserGroup());
		companyMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
		companyMst.setLastVerifiedDate(new Date());
		companyMst.setCorporatePaymentsLimit(companyRequest.getCorporatePaymentsLimit());
		companyMst.setDeviceLocation(companyRequest.getDeviceLocation());

		if (null != companyRequest.getCompanyFeatures() && !companyRequest.getCompanyFeatures().isEmpty()) {
			addFeatureList(companyMst, companyRequest.getCompanyFeatures());
		}

		if (null == companyMst.getId()) {
			companyMst.setCanvassedBranch(companyRequest.getCanvassedBranch());
			companyMst.setCanvassedUser(companyRequest.getCanvassedUser());
		}
		return companyMst;
	}

	private void addFeatureList(CompanyMst companyMst, List<Long> requestedFeatures) {
//		List<Features> features = (List<Features>) featuresRepository.findAll();
//		List<CompanyFeatures> companyFeatures = new ArrayList<>();

		Iterable<Features> features = featuresRepository.findAll();
		Set<CompanyFeatures> companyFeatures = new HashSet<>();

		for (Features feature : features) {
			if (requestedFeatures.stream().anyMatch(x -> x.equals(feature.getId()))) {
				CompanyFeatures companyFeature = new CompanyFeatures();
				companyFeature.setCompany(companyMst);
				companyFeature.setFeature(feature.getId());
				companyFeature.setFeatureDescription(feature.getDescription());
				companyFeatures.add(companyFeature);
			}
		}
		companyMst.setCompanyFeatures(companyFeatures);
	}
}
