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
import biz.nable.sb.cor.comp.bean.BranchBean;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.db.entity.BranchDelete;
import biz.nable.sb.cor.comp.db.entity.BranchMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.BranchDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.BranchMstRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.request.CreateBranchRequest;
import biz.nable.sb.cor.comp.request.DeleteBranchRequest;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;

@Transactional
@Component
public class BranchApproval implements CommonApprovalTemplate {

	@Autowired
	private BranchMstRepository branchMstRepository;
	@Autowired
	private BranchDeleteRepository branchDeleteRepository;

	@Autowired
	private CompanyMstRepository companyMstRepository;

	@Autowired
	private CommonConverter commonConverter;
	@Autowired
	private MessageSource messageSource;

	@Autowired
	BranchTempComponent branchTempComponent;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse doApprove(ApprovalBean approvalBean) {
		logger.info("Start Link Company approval process");
		CommonResponse commonResponse = new CommonResponse();
		ApprovalResponseBean approvalResponseBean = branchTempComponent.doApprove(approvalBean);
		TempDto commonTemp = approvalResponseBean.getTempDto();
		if (ApprovalStatus.VERIFIED.name().equalsIgnoreCase(approvalBean.getApprovalStatus())) {
			if (ActionTypeEnum.CREATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				addToBranchMst(approvalBean, commonTemp);
			} else if (ActionTypeEnum.UPDATE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				updateBranchMst(approvalBean, commonTemp);
			} else if (ActionTypeEnum.DELETE.name().equalsIgnoreCase(approvalBean.getActionType())) {
				deleteBranch(approvalBean, commonTemp);
			}
		} else {
			changeMstStatus(approvalBean, commonTemp);
		}
		commonResponse.setReturnCode(HttpStatus.ACCEPTED.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		return commonResponse;
	}

	private void updateBranchMst(ApprovalBean approvalBean, TempDto commonTemp) {
		// TODO Auto-generated method stub

	}

	private void changeMstStatus(ApprovalBean approvalBean, TempDto tempDto) {
		BranchBean branchBean = commonConverter.mapToPojo(tempDto.getRequestPayload(), BranchBean.class);
		Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(branchBean.getCompanyId());
		if (Boolean.FALSE.equals(companyO.isPresent())) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			throw new SystemException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		}
		if (!ActionTypeEnum.CREATE.name().equals(approvalBean.getActionType())) {
			Optional<BranchMst> optional = branchMstRepository.findByBranchIdAndCompany(approvalBean.getReferenceId(),
					companyO.get().getId());
			if (!optional.isPresent()) {
				throw new SystemException(messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_BRANCH_RECORD_FOUND);
			}
			BranchMst branchMst = optional.get();
			branchMst.setRecordStatus(RecordStatusEnum.ACTIVE);
			branchMstRepository.save(branchMst);
		}
	}

	private void deleteBranch(ApprovalBean approvalBean, TempDto tempDto) {
		logger.info("Start Deleting Link");
		DeleteBranchRequest deleteBranchRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
				DeleteBranchRequest.class);

		Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(deleteBranchRequest.getCompanyId());
		if (companyO.isPresent()) {
			logger.info("Start Deleting existing Branch");

			Optional<BranchMst> optional = branchMstRepository.findByBranchIdAndCompany(approvalBean.getReferenceId(),
					companyO.get().getId());
			if (!optional.isPresent()) {
				throw new SystemException(messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_BRANCH_RECORD_FOUND);
			}
			try {
				BranchDelete branchDelete = new BranchDelete();
				BeanUtils.copyProperties(branchDelete, optional.get());
				branchDelete.setLastVerifiedBy(approvalBean.getVerifiedBy());
				branchDelete.setLastUpdatedBy(tempDto.getLastUpdatedBy());
				branchDelete.setLastUpdatedDate(tempDto.getLastUpdatedDate());
				branchDelete.setStatus(StatusEnum.DELETED);
				branchDelete.setId(null);
				branchDeleteRepository.save(branchDelete);
				branchMstRepository.delete(optional.get());
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

	private void addToBranchMst(ApprovalBean approvalBean, TempDto tempDto) {
		CreateBranchRequest createBranchRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
				CreateBranchRequest.class);
		CommonResponse commonResponse = new CommonResponse();
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(createBranchRequest.getCompanyId());
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
		} else {
			Boolean isExist = optional.get().getBranchMsts().stream()
					.anyMatch(x -> x.getBranchId().equals(createBranchRequest.getBranchId()));

			if (Boolean.TRUE.equals(isExist)) {
				logger.info("Company already linked");
				throw new SystemException(messageSource.getMessage(ErrorCode.Branch_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()), ErrorCode.Branch_RECORD_ALREADY_EXISTS);
			}
			logger.info("Start Inserting a link");
			BranchMst branchMst = new BranchMst();

			buildCompanyCummData(branchMst, createBranchRequest, optional.get());
			saveBranch(branchMst, approvalBean, tempDto);

		}
	}

	private void saveBranch(BranchMst branchMst, ApprovalBean approvalBean, TempDto tempDto) {
		logger.info("start save to db");
		branchMst.setUserGroup(approvalBean.getUserGroup());

		branchMst.setLastVerifiedBy(approvalBean.getVerifiedBy());
		branchMst.setLastVerifiedDate(new Date());

		branchMst.setLastUpdatedBy(tempDto.getLastUpdatedBy());
		branchMst.setLastUpdatedDate(tempDto.getLastUpdatedDate());
		branchMst.setCreatedBy(null == branchMst.getCreatedBy() ? tempDto.getCreatedBy() : branchMst.getCreatedBy());
		branchMst.setCreatedDate(
				null == branchMst.getCreatedDate() ? tempDto.getCreatedDate() : branchMst.getCreatedDate());

		branchMst.setRecordStatus(RecordStatusEnum.ACTIVE);
		branchMst.setStatus(StatusEnum.ACTIVE);
		branchMstRepository.save(branchMst);
		logger.info("succefully saved to db");

	}

	private BranchMst buildCompanyCummData(BranchMst branchMst, CreateBranchRequest createBranchRequest,
			CompanyMst company) {
		logger.info("Start build companyCummData");
		branchMst.setBranchId(createBranchRequest.getBranchId());
		branchMst.setBranchName(createBranchRequest.getBranchName());
		branchMst.setCompany(company);

		return branchMst;
	}

}
