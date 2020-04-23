package biz.nable.sb.cor.comp.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.bean.CommonSearchBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.bean.AuthPendingBranchBean;
import biz.nable.sb.cor.comp.bean.BranchBean;
import biz.nable.sb.cor.comp.bean.BranchDetailBean;
import biz.nable.sb.cor.comp.bean.BranchResponseBean;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.db.entity.BranchDelete;
import biz.nable.sb.cor.comp.db.entity.BranchMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.BranchDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.BranchMstRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.exception.RecordNotFoundException;
import biz.nable.sb.cor.comp.request.CreateBranchRequest;
import biz.nable.sb.cor.comp.request.DeleteBranchRequest;
import biz.nable.sb.cor.comp.request.UpdateBranchRequest;
import biz.nable.sb.cor.comp.response.BranchResponse;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Service
public class BranchService {

	@Autowired
	CommonConverter commonConverter;

	@Autowired
	BranchMstRepository branchMstRepository;

	@Autowired
	BranchDeleteRepository branchDeleteRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BranchTempComponent branchTempComponent;

	private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.BRANCH;

	private static final String BRANCH_HASH_TAG = "#companyId=";

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse createTempBranch(CreateBranchRequest createBranchRequest, String userId, String userGroup,
			String requestId) {
		logger.info("================== Start Create Branch Request =================");
		logger.info("Create branch {} to {}", createBranchRequest.getBranchId(), createBranchRequest.getCompanyId());
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
				logger.info(messageSource.getMessage(ErrorCode.BRANCH_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.BRANCH_RECORD_ALREADY_EXISTS);
				commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
				commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.BRANCH_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
			} else {
				CommonRequestBean commonRequestBean = new CommonRequestBean();
				commonRequestBean.setCommonTempBean(createBranchRequest);
				String hashTags = BRANCH_HASH_TAG.concat(String.valueOf(createBranchRequest.getCompanyId()));
				String referenceNo = String.valueOf(createBranchRequest.getBranchId()).concat("#")
						.concat(createBranchRequest.getCompanyId());
				commonRequestBean.setHashTags(hashTags);
				commonRequestBean.setReferenceNo(referenceNo);
				commonRequestBean.setRequestType(REQUEST_TYPE.name());
				commonRequestBean.setUserGroup(userGroup);
				commonRequestBean.setUserId(userId);
				logger.info("Create commonRequestBean");
				CommonResponseBean commonResponseBean = branchTempComponent.createTempRecord(commonRequestBean,
						requestId);
				commonResponse.setErrorCode(commonResponseBean.getErrorCode());
				commonResponse.setReturnCode(commonResponseBean.getReturnCode());
				commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
				logger.info("================== End Create Branch Request =================");
			}
		}
		return commonResponse;
	}

	public CommonGetListResponse<AuthPendingBranchBean> getPendingAuthBranches(String userId, String userGroup,
			String requestId) {
		logger.info("================== Start getPendingAuthBranches =================");
		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		bean.setUserGroup(userGroup);
		bean.setUserId(userId);

		List<TempDto> tempList = branchTempComponent.getAuthPendingRecord(bean).getTempList();
		CommonGetListResponse<AuthPendingBranchBean> commonGetListResponse = new CommonGetListResponse<>();
		List<AuthPendingBranchBean> authPendingBranchBeans = new ArrayList<>();
		for (TempDto tempDto : tempList) {
			CreateBranchRequest branchRequest = commonConverter.mapToPojo(tempDto.getRequestPayload(),
					CreateBranchRequest.class);
			Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(branchRequest.getCompanyId());
			if (companyO.isPresent()) {
				AuthPendingBranchBean branchBean = new AuthPendingBranchBean();
				branchBean.setCompanyId(companyO.get().getCompanyId());
				branchBean.setCompanyName(companyO.get().getCompanyName());
				branchBean.setBranchId(branchRequest.getBranchId());
				BranchResponseBean branchResponse = new BranchResponseBean();
				branchResponse.setBranchName(branchRequest.getBranchName());
				branchBean.setModifiedBranch(branchResponse);
				if (ActionTypeEnum.UPDATE.equals(tempDto.getActionType())) {
					Optional<BranchMst> optional = branchMstRepository
							.findByBranchIdAndCompany(tempDto.getReferenceNo().split("#")[0], companyO.get());
					if (optional.isPresent()) {
						BranchResponseBean currentBranchResponse = new BranchResponseBean();
						currentBranchResponse.setBranchName(optional.get().getBranchName());
						branchBean.setCurrentBranch(currentBranchResponse);
					}
				}

				branchBean.setAuthorizationId(Long.parseLong(tempDto.getApprovalId()));
				branchBean.setSignature(tempDto.getSignature());
				branchBean.setActionType(tempDto.getActionType());
				branchBean.setStatus(StatusEnum.PENDING);
				authPendingBranchBeans.add(branchBean);
			}
		}
		commonGetListResponse.setPayLoad(authPendingBranchBeans);
		commonGetListResponse.setReturnCode(HttpStatus.OK.value());
		commonGetListResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonGetListResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End getPendingAuthBranches =================");

		return commonGetListResponse;
	}

	public CommonResponse deleteBranch(DeleteBranchRequest deleteBranchRequest, String userId, String userGroup,
			String requestId) {

		logger.info("================== Start Delete branch =================");
		Optional<CompanyMst> optional1 = companyMstRepository.findByCompanyId(deleteBranchRequest.getCompanyId());

		if (!optional1.isPresent()) {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		}
		Optional<BranchMst> optional = branchMstRepository.findByBranchIdAndCompany(deleteBranchRequest.getBranchId(),
				optional1.get());
		if (optional.isPresent()) {
			CreateBranchRequest branchRequest = new CreateBranchRequest();
			try {
				BeanUtils.copyProperties(branchRequest, optional.get());
				branchRequest.setCompanyId(optional.get().getCompany().getCompanyId());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(branchRequest);
			String hashTags = BRANCH_HASH_TAG.concat(deleteBranchRequest.getCompanyId());
			String referenceNo = branchRequest.getBranchId().concat("#").concat(branchRequest.getCompanyId());
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);
			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);

			CommonResponseBean commonResponseBean = branchTempComponent.deleteBranchTemp(commonRequestBean, requestId);
			optional.get().setRecordStatus(RecordStatusEnum.DELETE_PENDING);
			branchMstRepository.save(optional.get());
			CommonResponse commonResponse = new CommonResponse();
			commonResponse.setErrorCode(commonResponseBean.getErrorCode());
			commonResponse.setReturnCode(commonResponseBean.getReturnCode());
			commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());

			logger.info("================== End Delete  branch =================");
			return commonResponse;
		} else {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_BRANCH_RECORD_FOUND);
		}
	}

	public CommonResponse updateTempCompany(@Valid UpdateBranchRequest updateBranchRequest, String userId,
			String userGroup, String requestId) {

		logger.info("================== Start Update  branch =================");
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(updateBranchRequest.getCompanyId());
		CommonResponse commonResponse;
		if (!optional.isPresent()) {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		} else {
			Optional<BranchMst> optionalB = branchMstRepository
					.findByBranchIdAndCompany(updateBranchRequest.getBranchId(), optional.get());
			if (!optionalB.isPresent()) {
				throw new RecordNotFoundException(messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_BRANCH_RECORD_FOUND);
			}

			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(updateBranchRequest);
			String hashTags = BRANCH_HASH_TAG.concat(updateBranchRequest.getCompanyId());
			String referenceNo = updateBranchRequest.getBranchId().concat("#")
					.concat(updateBranchRequest.getCompanyId());
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);

			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);
			commonResponse = branchTempComponent.updateTempCompany(commonRequestBean, requestId);
			optionalB.get().setRecordStatus(RecordStatusEnum.UPDATE_PENDING);
			branchMstRepository.save(optionalB.get());
			commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
			commonResponse.setReturnCode(HttpStatus.OK.value());
		}
		logger.info("================== End update  branch =================");
		return commonResponse;
	}

	public CommonGetListResponse<BranchDetailBean> getBranches(String companyId, RecordStatusEnum status, String userId,
			String userGroup, String requestId) {
		CommonGetListResponse<BranchDetailBean> branchDetailBeans = new CommonGetListResponse<>();
		logger.info("================== Start Find  branch =================");
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
		if (!optional.isPresent()) {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		} else {
			CompanyMst companyMst = optional.get();
			if (RecordStatusEnum.ACTIVE.equals(status) || RecordStatusEnum.UPDATE_PENDING.equals(status)
					|| RecordStatusEnum.DELETE_PENDING.equals(status)) {
				addActiveBranchList(companyMst, branchDetailBeans, status);
			} else if (RecordStatusEnum.DELETED.equals(status)) {
				addDeletedBranchList(companyMst, branchDetailBeans, status);
			} else if (RecordStatusEnum.CREATE_PENDING.equals(status)) {
				addPendingBranchList(companyMst, branchDetailBeans, status, ActionTypeEnum.CREATE);
			}
		}

		branchDetailBeans.setReturnCode(HttpStatus.OK.value());
		branchDetailBeans.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		branchDetailBeans.setErrorCode(ErrorCode.OPARATION_SUCCESS);

		logger.info("================== End Find  branch =================");
		return branchDetailBeans;
	}

	public CommonGetListResponse<BranchResponse> getAllBranches(String userId, String userGroup, String requestId) {
		CommonGetListResponse<BranchResponse> branchDetailBeans = new CommonGetListResponse<>();
		logger.info("================== Start Find  branch =================");
		List<BranchMst> listBranchMsts = (List<BranchMst>) branchMstRepository.findAll();
		List<BranchBean> commonTempBeans = getCompanyTemp();
		if (listBranchMsts.isEmpty()) {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		} else {
			branchDetailBeans.setPayLoad(buildBranchListResponse(listBranchMsts, commonTempBeans));
		}

		branchDetailBeans.setReturnCode(HttpStatus.OK.value());
		branchDetailBeans.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		branchDetailBeans.setErrorCode(ErrorCode.OPARATION_SUCCESS);

		logger.info("================== End Find  branch =================");
		return branchDetailBeans;
	}

	private void addActiveBranchList(CompanyMst companyMst, CommonGetListResponse<BranchDetailBean> branchDetailBeans,
			RecordStatusEnum status) {
		for (BranchMst branch : companyMst.getBranchMsts()) {
			logger.info("Get records form master table");
			if (status == branch.getRecordStatus()) {
				BranchDetailBean branchDetailBean = new BranchDetailBean();
				branchDetailBean.setBranchId(branch.getBranchId());
				branchDetailBean.setBranchName(branch.getBranchName());
				branchDetailBean.setCompanyId(companyMst.getCompanyId());
				branchDetailBean.setCompanyName(companyMst.getCompanyName());
				branchDetailBean.setStatus(branch.getRecordStatus().name());

				branchDetailBean.setCreatedBy(branch.getCreatedBy());
				branchDetailBean.setCreatedDate(branch.getCreatedDate());
				branchDetailBean.setLastUpdatedBy(branch.getLastUpdatedBy());
				branchDetailBean.setLastUpdatedDate(branch.getLastUpdatedDate());
				branchDetailBean.setLastVerifiedBy(branch.getLastVerifiedBy());
				branchDetailBean.setLastVerifiedDate(branch.getLastVerifiedDate());
				branchDetailBeans.getPayLoad().add(branchDetailBean);
			}
		}
	}

	private List<BranchBean> getCompanyTemp() {
		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		List<TempDto> tempList = branchTempComponent.getTempRecord(bean).getTempList();
		List<BranchBean> commonTempBeans = new ArrayList<>();
		for (TempDto tempDto : tempList) {
			BranchBean branchBean = commonConverter.mapToPojo(tempDto.getRequestPayload(), BranchBean.class);
			commonTempBeans.add(branchBean);
		}
		return commonTempBeans;
	}

	private void addPendingBranchList(CompanyMst companyMst, CommonGetListResponse<BranchDetailBean> branchDetailBeans,
			RecordStatusEnum status, ActionTypeEnum actionTypeEnum) {
		if (RecordStatusEnum.CREATE_PENDING == status) {
			logger.info("Get records form Temp table");
			CommonSearchBean bean = new CommonSearchBean();
			bean.setHashTags(BRANCH_HASH_TAG.concat(companyMst.getCompanyId()));
			bean.setRequestType(REQUEST_TYPE.name());
			bean.setActionType(actionTypeEnum);
			List<TempDto> tempList = branchTempComponent.getTempRecord(bean).getTempList();
			for (TempDto tempDto : tempList) {
				BranchDetailBean branchDetailBean = new BranchDetailBean();
				BranchBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(), BranchBean.class);
				branchDetailBean.setCompanyId(companyMst.getCompanyId());
				branchDetailBean.setCompanyName(companyMst.getCompanyName());
				branchDetailBean.setBranchId(companyTempBean.getBranchId());
				branchDetailBean.setBranchName(companyTempBean.getBranchName());
				branchDetailBean.setStatus(RecordStatusEnum.CREATE_PENDING.name());

				branchDetailBean.setCreatedBy(tempDto.getCreatedBy());
				branchDetailBean.setCreatedDate(tempDto.getCreatedDate());

				branchDetailBeans.getPayLoad().add(branchDetailBean);
			}
		}

	}

	private void addDeletedBranchList(CompanyMst companyMst, CommonGetListResponse<BranchDetailBean> branchDetailBeans,
			RecordStatusEnum status) {
		if (RecordStatusEnum.DELETED == status) {
			logger.info("Get records form Delete table");
			List<BranchDelete> branchDeletes = branchDeleteRepository.findByCompanyId(companyMst.getId());
			for (BranchDelete branchDelete : branchDeletes) {
				BranchDetailBean branchDetailBean = new BranchDetailBean();
				branchDetailBean.setBranchId(branchDelete.getBranchId());
				branchDetailBean.setBranchName(branchDelete.getBranchName());
				branchDetailBean.setCompanyId(companyMst.getCompanyId());
				branchDetailBean.setCompanyName(companyMst.getCompanyName());
				branchDetailBean.setStatus(RecordStatusEnum.DELETED.name());

				branchDetailBean.setCreatedBy(branchDelete.getCreatedBy());
				branchDetailBean.setCreatedDate(branchDelete.getCreatedDate());
				branchDetailBean.setLastUpdatedBy(branchDelete.getLastUpdatedBy());
				branchDetailBean.setLastUpdatedDate(branchDelete.getLastUpdatedDate());
				branchDetailBean.setLastVerifiedBy(branchDelete.getLastVerifiedBy());
				branchDetailBean.setLastVerifiedDate(branchDelete.getLastVerifiedDate());
				branchDetailBeans.getPayLoad().add(branchDetailBean);
			}
		}
	}

	private List<BranchResponse> buildBranchListResponse(List<BranchMst> listBranchMsts,
			List<BranchBean> commonTempBeans) {
		List<BranchResponse> branchResponses = new ArrayList<>();

		logger.info("<====== Start mapping companyMst to companyResponse and companyTempResponses =======>");
		for (BranchMst branchMst : listBranchMsts) {
			BranchResponse branchResponse = new BranchResponse();
			BranchResponseBean branchResponseBean = new BranchResponseBean();
			branchResponseBean.setBranchName(branchMst.getBranchName());
			branchResponse.setCompanyID(branchMst.getCompany().getCompanyId());
			branchResponse.setBranchCode(branchMst.getBranchId());
			branchResponse.setCurrent(branchResponseBean);
			if (RecordStatusEnum.UPDATE_PENDING.equals(branchMst.getRecordStatus())) {
				BranchBean branchBean = commonTempBeans.stream()
						.filter(branch -> branch.getBranchId().equals(branchMst.getBranchId())).findAny().orElse(null);
				if (null != branchBean) {
					BranchResponseBean modified = new BranchResponseBean();
					modified.setBranchName(branchBean.getBranchName());
					branchResponse.setModified(modified);
				}
			}
			branchResponses.add(branchResponse);
		}
		return branchResponses;
	}
}