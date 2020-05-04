package biz.nable.sb.cor.comp.factory.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Optional;

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
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.component.CompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.CompanyCummData;
import biz.nable.sb.cor.comp.db.entity.CompanyCummDataDelete;
import biz.nable.sb.cor.comp.db.repository.LinkCompanyDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.LinkCompanyRepository;
import biz.nable.sb.cor.comp.request.LinkCompanyDeleteRequest;
import biz.nable.sb.cor.comp.request.LinkCompanyRequest;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;

@Transactional
@Component
public class LinkCompanyApproval implements CommonApprovalTemplate {

	@Autowired
	private LinkCompanyRepository linkCompanyRepository;
	@Autowired
	private LinkCompanyDeleteRepository linkCompanyDeleteRepository;

	@Autowired
	private CommonConverter commonConverter;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	CompanyTempComponent companyTempComponent;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse doApprove(ApprovalBean approvalBean) {
		logger.info("Start Link Company approval process");
		CommonResponse commonResponse = new CommonResponse();
		ApprovalResponseBean approvalResponseBean = companyTempComponent.doApprove(approvalBean);
		TempDto commonTemp = approvalResponseBean.getTempDto();
		if (ApprovalStatus.VERIFIED.name().equalsIgnoreCase(approvalBean.getApprovalStatus())) {
			if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				addToLinkCompanyMst(approvalBean, commonTemp);
			} else if (ActionTypeEnum.DELETE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				deleteLinkCompany(approvalBean, commonTemp);
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
			Optional<CompanyCummData> optional = linkCompanyRepository.findByParentCompanyIdAndCustomerId(
					approvalBean.getReferenceId().split("#")[1], approvalBean.getReferenceId().split("#")[0]);
			if (!optional.isPresent()) {
				throw new SystemException(messageSource.getMessage(ErrorCode.NO_LINK_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_LINK_COMPANY_RECORD_FOUND);
			}
			CompanyCummData companyCummData = optional.get();
			companyCummData.setRecordStatus(RecordStatusEnum.ACTIVE);
			linkCompanyRepository.save(companyCummData);
		}
	}

	private void deleteLinkCompany(ApprovalBean approvalBean, TempDto tempDto) {
		logger.info("Start Deleting Link");
		Optional<CompanyCummData> companyO = linkCompanyRepository.findByParentCompanyIdAndCustomerId(
				approvalBean.getReferenceId().split("#")[1], approvalBean.getReferenceId().split("#")[0]);
		CompanyCummData companyCummData;
		CompanyCummDataDelete companyCummDataDelete = new CompanyCummDataDelete();
		if (companyO.isPresent()) {
			logger.info("Start Deleting existing Link");
			companyCummData = companyO.get();
			try {
				LinkCompanyDeleteRequest linkCompanyRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
						LinkCompanyDeleteRequest.class);
				BeanUtils.copyProperties(companyCummDataDelete, companyCummData);
				companyCummDataDelete.setLastVerifiedBy(approvalBean.getVerifiedBy());
				companyCummDataDelete.setReason(linkCompanyRequest.getReason());
				companyCummDataDelete.setLastUpdatedBy(tempDto.getLastUpdatedBy());
				companyCummDataDelete.setLastUpdatedDate(tempDto.getLastUpdatedDate());
				companyCummDataDelete.setStatus(StatusEnum.DELETED);
				companyCummDataDelete.setId(null);
				linkCompanyDeleteRepository.save(companyCummDataDelete);
				linkCompanyRepository.delete(companyCummData);
				logger.info("Link Deletion compleated");
			} catch (IllegalAccessException | InvocationTargetException e) {
				logger.error("companyMst to companyMstHis data mapping error {}", e);
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
						ErrorCode.DATA_COPY_ERROR);
			}
		} else {
			logger.error("Link record not found for delete");
		}

	}

	private void addToLinkCompanyMst(ApprovalBean approvalBean, TempDto tempDto) {
		LinkCompanyRequest linkCompanyRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
				LinkCompanyRequest.class);
		Optional<CompanyCummData> optional = linkCompanyRepository.findByParentCompanyIdAndCustomerId(
				approvalBean.getReferenceId().split("#")[1], approvalBean.getReferenceId().split("#")[0]);
		CompanyCummData companyCummData;
		if (optional.isPresent()) {
			logger.info("Company already linked");
			throw new SystemException(messageSource.getMessage(ErrorCode.LINK_COMPANY_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()), ErrorCode.LINK_COMPANY_RECORD_ALREADY_EXISTS);
		}
		logger.info("Start Inserting a link");
		companyCummData = new CompanyCummData();

		buildCompanyCummData(companyCummData, linkCompanyRequest);
		saveLink(companyCummData, approvalBean, tempDto);

	}

	private void saveLink(CompanyCummData companyCummData, ApprovalBean approvalBean, TempDto tempDto) {
		logger.info("start save to db");
		companyCummData.setUserGroup(approvalBean.getUserGroup());

		companyCummData.setLastVerifiedBy(approvalBean.getVerifiedBy());
		companyCummData.setLastVerifiedDate(new Date());

		companyCummData.setLastUpdatedBy(tempDto.getLastUpdatedBy());
		companyCummData.setLastUpdatedDate(tempDto.getLastUpdatedDate());
		companyCummData.setCreatedBy(
				null == companyCummData.getCreatedBy() ? tempDto.getCreatedBy() : companyCummData.getCreatedBy());
		companyCummData.setCreatedDate(
				null == companyCummData.getCreatedDate() ? tempDto.getCreatedDate() : companyCummData.getCreatedDate());

		companyCummData.setRecordStatus(RecordStatusEnum.ACTIVE);
		companyCummData.setStatus(StatusEnum.ACTIVE);
		linkCompanyRepository.save(companyCummData);
		logger.info("succefully saved to db");

	}

	private CompanyCummData buildCompanyCummData(CompanyCummData companyCummData,
			LinkCompanyRequest linkCompanyRequest) {
		logger.info("Start build companyCummData");
		companyCummData.setCustomerId(linkCompanyRequest.getCustomerId());
		companyCummData.setParentCompanyId(linkCompanyRequest.getParentCompanyId());
		return companyCummData;
	}

}
