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
import biz.nable.sb.cor.comp.bean.CompanySummeryBean;
import biz.nable.sb.cor.comp.bean.CompanyTempBean;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.db.entity.BranchDelete;
import biz.nable.sb.cor.comp.db.entity.BranchMst;
import biz.nable.sb.cor.comp.db.entity.CompanyDelete;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.BranchDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.BranchMstRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.exception.RecordNotFoundException;
import biz.nable.sb.cor.comp.request.CreateBranchRequest;
import biz.nable.sb.cor.comp.request.DeleteBranchRequest;
import biz.nable.sb.cor.comp.request.UpdateBranchRequest;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.response.CompanySummeryListResponse;
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
				logger.info(messageSource.getMessage(ErrorCode.Branch_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.Branch_RECORD_ALREADY_EXISTS);
				commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
				commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.Branch_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
			} else {
				CommonRequestBean commonRequestBean = new CommonRequestBean();
				commonRequestBean.setCommonTempBean(createBranchRequest);
				String hashTags = BRANCH_HASH_TAG.concat(String.valueOf(createBranchRequest.getCompanyId()));
				String referenceNo = String.valueOf(createBranchRequest.getBranchId());
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
//
//	public CommonResponse getCustomerIds(String companyId, String userId, String userGroup, String requestId) {
//		logger.info("================== Start Get Company By Id =================");
//		Optional<CompanyMst> companyMstO = companyMstRepository.findByCompanyId(companyId);
//
//		List<CustomerIdResponseBean> customerIds = getTempList(companyId);
//		GetCustomerIdsResponse customerIdsResponse = new GetCustomerIdsResponse();
//		customerIdsResponse.getListOfLinkedCompanies().addAll(customerIds);
//		if (companyMstO.isPresent()) {
//			List<CompanyCummData> linkCompanies = branchMstRepository.findByParentCompanyId(companyId);
//
//			for (CompanyCummData companyCummData : linkCompanies) {
//				Optional<CompanyMst> customerO = companyMstRepository.findByCompanyId(companyCummData.getCustomerId());
//				if (customerO.isPresent()) {
//					CustomerIdResponseBean customerId = new CustomerIdResponseBean();
//					customerId.setCompanyId(customerO.get().getCompanyId());
//					customerId.setCompanyName(customerO.get().getCompanyName());
//					customerId.setStatus(StatusEnum.ACTIVE.name());
//					customerIdsResponse.getListOfLinkedCompanies().add(customerId);
//				}
//			}
//
//		}
//		customerIdsResponse.setReturnCode(HttpStatus.OK.value());
//		customerIdsResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
//		customerIdsResponse.setReturnMessage(
//				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
//		logger.info("================== End Get Company By Id =================");
//		return customerIdsResponse;
//	}
//
//	private List<CustomerIdResponseBean> getTempList(String companyId) {
//		logger.info("Start get temp Link Companys for {}", companyId);
//
//		CommonSearchBean bean = new CommonSearchBean();
//		bean.setRequestType(REQUEST_TYPE.name());
//		bean.setHashTags(LINK_COMPANY_HASH_TAG.concat(companyId));
//		List<TempDto> tempList = linkCompanyTempComponent.getTempRecord(bean).getTempList();
//		List<CustomerIdResponseBean> customerIdsResponse = new ArrayList<>();
//		for (TempDto tempDto : tempList) {
//			LinkCompanyRequest customerId = commonConverter.mapToPojo(tempDto.getRequestPayload(),
//					LinkCompanyRequest.class);
//			Optional<CompanyMst> customerO = companyMstRepository.findByCompanyId(customerId.getCustomerId());
//			if (customerO.isPresent()) {
//				CustomerIdResponseBean customerIdResopnse = new CustomerIdResponseBean();
//				customerIdResopnse.setCompanyId(customerO.get().getCompanyId());
//				customerIdResopnse.setCompanyName(customerO.get().getCompanyName());
//				customerIdResopnse.setStatus(StatusEnum.PENDING.name());
//				customerIdsResponse.add(customerIdResopnse);
//			}
//
//		}
//		logger.info("End get temp Link Companys ");
//
//		return customerIdsResponse;
//	}

	public CommonResponse getPendingAuthBranches(String userId, String userGroup, String requestId) {
		logger.info("================== Start getPendingAuthBranches =================");

		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		List<TempDto> tempList = branchTempComponent.getTempRecord(bean).getTempList();
		CommonGetListResponse<AuthPendingBranchBean> commonGetListResponse = new CommonGetListResponse<>();
		List<AuthPendingBranchBean> authPendingBranchBeans = new ArrayList<>();
		for (TempDto tempDto : tempList) {
			CreateBranchRequest customerId = commonConverter.mapToPojo(tempDto.getRequestPayload(),
					CreateBranchRequest.class);
			Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(customerId.getCompanyId());
			if (companyO.isPresent()) {
				AuthPendingBranchBean branchBean = new AuthPendingBranchBean();
				branchBean.setCompanyId(companyO.get().getCompanyId());
				branchBean.setCompanyName(companyO.get().getCompanyName());
				branchBean.setBranchId(customerId.getBranchId());
				branchBean.setBranchName(customerId.getBranchName());
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
				optional1.get().getId());
		if (optional.isPresent()) {
			CreateBranchRequest linkCompanyBean = new CreateBranchRequest();
			try {
				BeanUtils.copyProperties(linkCompanyBean, optional.get());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(linkCompanyBean);
			String hashTags = BRANCH_HASH_TAG.concat(linkCompanyBean.getCompanyId());
			String referenceNo = linkCompanyBean.getBranchId();
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
					.findByBranchIdAndCompany(updateBranchRequest.getBranchId(), optional.get().getId());
			if (!optionalB.isPresent()) {
				throw new RecordNotFoundException(messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_BRANCH_RECORD_FOUND);
			}
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(updateBranchRequest);
			String hashTags = BRANCH_HASH_TAG.concat(updateBranchRequest.getCompanyId());
			String referenceNo = updateBranchRequest.getBranchId();
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);

			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);
			commonResponse = branchTempComponent.updateTempCompany(commonRequestBean, requestId);
			optional.get().setRecordStatus(RecordStatusEnum.UPDATE_PENDING);
			companyMstRepository.save(optional.get());
			commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
			commonResponse.setReturnCode(HttpStatus.OK.value());
		}
		logger.info("================== End update  branch =================");
		return commonResponse;
	}

	public CommonResponse getBranches(String companyId, StatusEnum status, String userId, String userGroup,
			String requestId) {
		CommonGetListResponse<BranchDetailBean> branchDetailBeans = new CommonGetListResponse<>();
		logger.info("================== Start Find  branch =================");
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
		if (!optional.isPresent()) {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		} else {
			CompanyMst companyMst = optional.get();
			for (BranchMst branch : companyMst.getBranchMsts()) {
				logger.info("Get records form master table");
				if (status == branch.getStatus()) {
					BranchDetailBean branchDetailBean = new BranchDetailBean();
					branchDetailBean.setBranchId(branch.getBranchId());
					branchDetailBean.setBranchName(branch.getBranchName());
					branchDetailBean.setCompanyId(companyMst.getCompanyId());
					branchDetailBean.setCompanyName(companyMst.getCompanyName());
					branchDetailBean.setStatus(branch.getStatus());
					branchDetailBeans.getPayLoad().add(branchDetailBean);
				}
			}
			if (StatusEnum.DELETED == status) {
				logger.info("Get records form Delete table");
				List<BranchDelete> branchDeletes = branchDeleteRepository.findByCompanyId(companyMst.getId());
				for (BranchDelete branchDelete : branchDeletes) {
					BranchDetailBean branchDetailBean = new BranchDetailBean();
					branchDetailBean.setBranchId(branchDelete.getBranchId());
					branchDetailBean.setBranchName(branchDelete.getBranchName());
					branchDetailBean.setCompanyId(companyMst.getCompanyId());
					branchDetailBean.setCompanyName(companyMst.getCompanyName());
					branchDetailBean.setStatus(StatusEnum.DELETED);
					branchDetailBeans.getPayLoad().add(branchDetailBean);
				}
			}
			if (StatusEnum.PENDING == status || StatusEnum.INACTIVE == status) {
				logger.info("Get records form Temp table");
				CommonSearchBean bean = new CommonSearchBean();
				bean.setHashTags(BRANCH_HASH_TAG.concat(companyId));
				bean.setRequestType(REQUEST_TYPE.name());
				bean.setActionType(ActionTypeEnum.CREATE);
				List<TempDto> tempList = branchTempComponent.getTempRecord(bean).getTempList();
				for (TempDto tempDto : tempList) {
					BranchDetailBean branchDetailBean = new BranchDetailBean();
					BranchBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(),
							BranchBean.class);
					branchDetailBean.setCompanyId(companyMst.getCompanyId());
					branchDetailBean.setCompanyName(companyMst.getCompanyName());
					branchDetailBean.setBranchId(companyTempBean.getBranchId());
					branchDetailBean.setBranchName(companyTempBean.getBranchName());
					branchDetailBean.setStatus(StatusEnum.INACTIVE);
					branchDetailBeans.getPayLoad().add(branchDetailBean);
				}
			}
		}

		branchDetailBeans.setReturnCode(HttpStatus.OK.value());
		branchDetailBeans.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		branchDetailBeans.setErrorCode(ErrorCode.OPARATION_SUCCESS);

		logger.info("================== End Find  branch =================");
		return branchDetailBeans;
	}

}