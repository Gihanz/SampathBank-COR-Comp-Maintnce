package biz.nable.sb.cor.comp.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.comp.component.BranchTempComponent;
import biz.nable.sb.cor.comp.component.UserTempComponent;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.entity.UserMst;
import biz.nable.sb.cor.comp.db.repository.BranchDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.BranchMstRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.db.repository.UserMstRepository;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Service
public class UserService {

	@Autowired
	CommonConverter commonConverter;

	@Autowired
	BranchMstRepository branchMstRepository;

	@Autowired
	UserMstRepository userMstRepository;

	@Autowired
	BranchDeleteRepository branchDeleteRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BranchTempComponent branchTempComponent;

	@Autowired
	UserTempComponent userTempComponent;

	private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.USER;

	private static final String USER_HASH_TAG = "#companyId=";

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse createTempUser(CreateUserRequest createUserRequest, String userId, String userGroup,
			String requestId) {
		logger.info("================== Start Create User Request =================");
		logger.info("Create User {} to {}", createUserRequest.getUserName(), createUserRequest.getCompanyId());
		CommonResponse commonResponse = new CommonResponse();
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(createUserRequest.getCompanyId());
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
		} else {
			Optional<UserMst> optional2 = userMstRepository.findByUserName(createUserRequest.getUserName());
			Boolean isExist = optional2.isPresent();

			if (Boolean.TRUE.equals(isExist)) {
				logger.info(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.USER_RECORD_ALREADY_EXISTS);
				commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
				commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.USER_RECORD_ALREADY_EXISTS, null,
						LocaleContextHolder.getLocale()));
			} else {
				CommonRequestBean commonRequestBean = new CommonRequestBean();
				commonRequestBean.setCommonTempBean(createUserRequest);
				String hashTags = USER_HASH_TAG.concat(String.valueOf(createUserRequest.getCompanyId()));
				String referenceNo = String.valueOf(createUserRequest.getUserName());
				commonRequestBean.setHashTags(hashTags);
				commonRequestBean.setReferenceNo(referenceNo);
				commonRequestBean.setRequestType(REQUEST_TYPE.name());
				commonRequestBean.setUserGroup(userGroup);
				commonRequestBean.setUserId(userId);
				logger.info("Create commonRequestBean");
				CommonResponseBean commonResponseBean = userTempComponent.createTempRecord(commonRequestBean,
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
//	public CommonResponse getPendingAuthBranches(String userId, String userGroup, String requestId) {
//		logger.info("================== Start getPendingAuthBranches =================");
//
//		CommonSearchBean bean = new CommonSearchBean();
//		bean.setRequestType(REQUEST_TYPE.name());
//		List<TempDto> tempList = branchTempComponent.getAuthPendingRecord(bean).getTempList();
//		CommonGetListResponse<AuthPendingBranchBean> commonGetListResponse = new CommonGetListResponse<>();
//		List<AuthPendingBranchBean> authPendingBranchBeans = new ArrayList<>();
//		for (TempDto tempDto : tempList) {
//			CreateBranchRequest customerId = commonConverter.mapToPojo(tempDto.getRequestPayload(),
//					CreateBranchRequest.class);
//			Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(customerId.getCompanyId());
//			if (companyO.isPresent()) {
//				AuthPendingBranchBean branchBean = new AuthPendingBranchBean();
//				branchBean.setCompanyId(companyO.get().getCompanyId());
//				branchBean.setCompanyName(companyO.get().getCompanyName());
//				branchBean.setBranchId(customerId.getBranchId());
//				branchBean.setBranchName(customerId.getBranchName());
//				branchBean.setAuthorizationId(Long.parseLong(tempDto.getApprovalId()));
//				branchBean.setSignature(tempDto.getSignature());
//				branchBean.setActionType(tempDto.getActionType());
//				branchBean.setStatus(StatusEnum.PENDING);
//				authPendingBranchBeans.add(branchBean);
//			}
//
//		}
//		commonGetListResponse.setPayLoad(authPendingBranchBeans);
//		commonGetListResponse.setReturnCode(HttpStatus.OK.value());
//		commonGetListResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
//		commonGetListResponse.setReturnMessage(
//				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
//		logger.info("================== End getPendingAuthBranches =================");
//
//		return commonGetListResponse;
//	}
//
//	public CommonResponse deleteBranch(DeleteBranchRequest deleteBranchRequest, String userId, String userGroup,
//			String requestId) {
//
//		logger.info("================== Start Delete branch =================");
//		Optional<CompanyMst> optional1 = companyMstRepository.findByCompanyId(deleteBranchRequest.getCompanyId());
//
//		if (!optional1.isPresent()) {
//			throw new RecordNotFoundException(
//					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
//					ErrorCode.NO_COMPANY_RECORD_FOUND);
//		}
//		Optional<BranchMst> optional = branchMstRepository.findByBranchIdAndCompany(deleteBranchRequest.getBranchId(),
//				optional1.get().getId());
//		if (optional.isPresent()) {
//			CreateBranchRequest linkCompanyBean = new CreateBranchRequest();
//			try {
//				BeanUtils.copyProperties(linkCompanyBean, optional.get());
//			} catch (IllegalAccessException | InvocationTargetException e) {
//				throw new SystemException(
//						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
//						ErrorCode.DATA_COPY_ERROR);
//			}
//			CommonRequestBean commonRequestBean = new CommonRequestBean();
//			commonRequestBean.setCommonTempBean(linkCompanyBean);
//			String hashTags = BRANCH_HASH_TAG.concat(linkCompanyBean.getCompanyId());
//			String referenceNo = linkCompanyBean.getBranchId();
//			commonRequestBean.setHashTags(hashTags);
//			commonRequestBean.setReferenceNo(referenceNo);
//			commonRequestBean.setRequestType(REQUEST_TYPE.name());
//			commonRequestBean.setUserGroup(userGroup);
//			commonRequestBean.setUserId(userId);
//
//			CommonResponseBean commonResponseBean = branchTempComponent.deleteBranchTemp(commonRequestBean, requestId);
//			optional.get().setRecordStatus(RecordStatusEnum.DELETE_PENDING);
//			branchMstRepository.save(optional.get());
//			CommonResponse commonResponse = new CommonResponse();
//			commonResponse.setErrorCode(commonResponseBean.getErrorCode());
//			commonResponse.setReturnCode(commonResponseBean.getReturnCode());
//			commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
//
//			logger.info("================== End Delete  branch =================");
//			return commonResponse;
//		} else {
//			throw new RecordNotFoundException(
//					messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
//					ErrorCode.NO_BRANCH_RECORD_FOUND);
//		}
//	}
//
//	public CommonResponse updateTempCompany(@Valid UpdateBranchRequest updateBranchRequest, String userId,
//			String userGroup, String requestId) {
//
//		logger.info("================== Start Update  branch =================");
//		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(updateBranchRequest.getCompanyId());
//		CommonResponse commonResponse;
//		if (!optional.isPresent()) {
//			throw new RecordNotFoundException(
//					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
//					ErrorCode.NO_COMPANY_RECORD_FOUND);
//		} else {
//			Optional<BranchMst> optionalB = branchMstRepository
//					.findByBranchIdAndCompany(updateBranchRequest.getBranchId(), optional.get().getId());
//			if (!optionalB.isPresent()) {
//				throw new RecordNotFoundException(messageSource.getMessage(ErrorCode.NO_BRANCH_RECORD_FOUND, null,
//						LocaleContextHolder.getLocale()), ErrorCode.NO_BRANCH_RECORD_FOUND);
//			}
//			CommonRequestBean commonRequestBean = new CommonRequestBean();
//			commonRequestBean.setCommonTempBean(updateBranchRequest);
//			String hashTags = BRANCH_HASH_TAG.concat(updateBranchRequest.getCompanyId());
//			String referenceNo = updateBranchRequest.getBranchId();
//			commonRequestBean.setHashTags(hashTags);
//			commonRequestBean.setReferenceNo(referenceNo);
//
//			commonRequestBean.setRequestType(REQUEST_TYPE.name());
//			commonRequestBean.setUserGroup(userGroup);
//			commonRequestBean.setUserId(userId);
//			commonResponse = branchTempComponent.updateTempCompany(commonRequestBean, requestId);
//			optional.get().setRecordStatus(RecordStatusEnum.UPDATE_PENDING);
//			companyMstRepository.save(optional.get());
//			commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
//			commonResponse.setReturnCode(HttpStatus.OK.value());
//		}
//		logger.info("================== End update  branch =================");
//		return commonResponse;
//	}
//
//	public CommonResponse getBranches(String companyId, StatusEnum status, String userId, String userGroup,
//			String requestId) {
//		CommonGetListResponse<BranchDetailBean> branchDetailBeans = new CommonGetListResponse<>();
//		logger.info("================== Start Find  branch =================");
//		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
//		if (!optional.isPresent()) {
//			throw new RecordNotFoundException(
//					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
//					ErrorCode.NO_COMPANY_RECORD_FOUND);
//		} else {
//			CompanyMst companyMst = optional.get();
//			for (BranchMst branch : companyMst.getBranchMsts()) {
//				logger.info("Get records form master table");
//				if (status == branch.getStatus()) {
//					BranchDetailBean branchDetailBean = new BranchDetailBean();
//					branchDetailBean.setBranchId(branch.getBranchId());
//					branchDetailBean.setBranchName(branch.getBranchName());
//					branchDetailBean.setCompanyId(companyMst.getCompanyId());
//					branchDetailBean.setCompanyName(companyMst.getCompanyName());
//					branchDetailBean.setStatus(branch.getStatus());
//					branchDetailBeans.getPayLoad().add(branchDetailBean);
//				}
//			}
//			if (StatusEnum.DELETED == status) {
//				logger.info("Get records form Delete table");
//				List<BranchDelete> branchDeletes = branchDeleteRepository.findByCompanyId(companyMst.getId());
//				for (BranchDelete branchDelete : branchDeletes) {
//					BranchDetailBean branchDetailBean = new BranchDetailBean();
//					branchDetailBean.setBranchId(branchDelete.getBranchId());
//					branchDetailBean.setBranchName(branchDelete.getBranchName());
//					branchDetailBean.setCompanyId(companyMst.getCompanyId());
//					branchDetailBean.setCompanyName(companyMst.getCompanyName());
//					branchDetailBean.setStatus(StatusEnum.DELETED);
//					branchDetailBeans.getPayLoad().add(branchDetailBean);
//				}
//			}
//			if (StatusEnum.PENDING == status || StatusEnum.INACTIVE == status) {
//				logger.info("Get records form Temp table");
//				CommonSearchBean bean = new CommonSearchBean();
//				bean.setHashTags(BRANCH_HASH_TAG.concat(companyId));
//				bean.setRequestType(REQUEST_TYPE.name());
//				bean.setActionType(ActionTypeEnum.CREATE);
//				List<TempDto> tempList = branchTempComponent.getTempRecord(bean).getTempList();
//				for (TempDto tempDto : tempList) {
//					BranchDetailBean branchDetailBean = new BranchDetailBean();
//					BranchBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(),
//							BranchBean.class);
//					branchDetailBean.setCompanyId(companyMst.getCompanyId());
//					branchDetailBean.setCompanyName(companyMst.getCompanyName());
//					branchDetailBean.setBranchId(companyTempBean.getBranchId());
//					branchDetailBean.setBranchName(companyTempBean.getBranchName());
//					branchDetailBean.setStatus(StatusEnum.INACTIVE);
//					branchDetailBeans.getPayLoad().add(branchDetailBean);
//				}
//			}
//		}
//
//		branchDetailBeans.setReturnCode(HttpStatus.OK.value());
//		branchDetailBeans.setReturnMessage(
//				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
//		branchDetailBeans.setErrorCode(ErrorCode.OPARATION_SUCCESS);
//
//		logger.info("================== End Find  branch =================");
//		return branchDetailBeans;
//	}

}