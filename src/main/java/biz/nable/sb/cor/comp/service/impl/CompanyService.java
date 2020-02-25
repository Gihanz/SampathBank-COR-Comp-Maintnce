package biz.nable.sb.cor.comp.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import biz.nable.sb.cor.common.bean.FindTempByRefBean;
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.bean.CompanyListResponseBean;
import biz.nable.sb.cor.comp.bean.CompanySummeryBean;
import biz.nable.sb.cor.comp.bean.CompanyTempBean;
import biz.nable.sb.cor.comp.bean.FindCompanyBean;
import biz.nable.sb.cor.comp.component.CompanyTempComponent;
import biz.nable.sb.cor.comp.db.criteria.CompanyCustomRepository;
import biz.nable.sb.cor.comp.db.entity.CompanyDelete;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.CompanyDeleteRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.request.CreateCompanyRequest;
import biz.nable.sb.cor.comp.request.FindCompanyRequest;
import biz.nable.sb.cor.comp.request.UpdateCompanyRequest;
import biz.nable.sb.cor.comp.response.ApprovalPendingResponse;
import biz.nable.sb.cor.comp.response.CompanyListResponse;
import biz.nable.sb.cor.comp.response.CompanyResponse;
import biz.nable.sb.cor.comp.response.CompanySummeryListResponse;
import biz.nable.sb.cor.comp.response.GetCompanyByIdResponse;
import biz.nable.sb.cor.comp.response.GetTempCompanyResponse;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Service
public class CompanyService {

	@Autowired
	CommonConverter commonConverter;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	CompanyCustomRepository companyCustomRepository;

	@Autowired
	CompanyDeleteRepository companyDeleteRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CompanyTempComponent companyTempComponent;

	private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.COMPANY;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse createTempCompany(CreateCompanyRequest createCompanyRequest, String userId, String userGroup,
			String requestId) {
		logger.info("================== Start Create company =================");
		CommonResponse commonResponse = new CommonResponse();
		String companyId = createCompanyRequest.getCompanyId();
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
		if (optional.isPresent()) {
			logger.info(messageSource.getMessage(ErrorCode.COMPANY_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.COMPANY_RECORD_ALREADY_EXISTS);
			commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
			commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.COMPANY_RECORD_ALREADY_EXISTS, null,
					LocaleContextHolder.getLocale()));
		} else {
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			createCompanyRequest.setCreateDate(new Date());
			createCompanyRequest.setCreateBy(userId);
			createCompanyRequest.setUserGroup(userGroup);
			commonRequestBean.setCommonTempBean(createCompanyRequest);
			String hashTags = "";
			String referenceNo = createCompanyRequest.getCompanyId();
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);
			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);

			CommonResponseBean commonResponseBean = companyTempComponent.createTempCompany(commonRequestBean,
					requestId);
			commonResponse.setErrorCode(commonResponseBean.getErrorCode());
			commonResponse.setReturnCode(commonResponseBean.getReturnCode());
			commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
		}
		logger.info("================== End Create company =================");
		return commonResponse;
	}

	public CommonResponse updateTempCompany(UpdateCompanyRequest updateCompanyRequest, String companyId, String userId,
			String userGroup, String requestId) {

		logger.info("================== Start Update company =================");
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
		CommonResponse commonResponse = new CommonResponse();
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
			commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			commonResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
		} else {
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			updateCompanyRequest.setCompanyName(optional.get().getCompanyName());
			updateCompanyRequest.setCompanyId(optional.get().getCompanyId());
			commonRequestBean.setCommonTempBean(updateCompanyRequest);
			String hashTags = "";
			String referenceNo = companyId;
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);

			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);
			commonResponse = companyTempComponent.updateTempCompany(commonRequestBean, requestId);
			optional.get().setRecordStatus(RecordStatusEnum.UPDATE_PENDING);
			companyMstRepository.save(optional.get());
			commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
			commonResponse.setReturnCode(HttpStatus.OK.value());
		}
		logger.info("================== End update company =================");
		return commonResponse;
	}

	public CompanyListResponse getCompanyList(FindCompanyBean findCompanyBean, String userId, String userGroup,
			String requestId) {
		logger.info("================== Start Get Company List =================");
		CompanyListResponse companyListResponse = new CompanyListResponse();
		logger.info("Start getCompanyList method");
		List<CompanyMst> listCompanyMsts = companyCustomRepository.findCompanyList(findCompanyBean);

		logger.info("Start get Company temp List");
		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		bean.setUserGroup(userGroup);
		List<CompanyTempBean> commonTempBeans = getCompanyTemp(bean);

		List<CompanyListResponseBean> companyListResponseBeans = buildCompanyListResponse(listCompanyMsts,
				commonTempBeans);

		logger.info("End getCompanyList method execution with {} records", companyListResponseBeans.size());
		companyListResponse.setReturnCode(HttpStatus.OK.value());
		companyListResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		companyListResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		companyListResponse.setCompanyResponseList(companyListResponseBeans);

		logger.info("================== End Get Company List =================");
		return companyListResponse;
	}

	private List<CompanyListResponseBean> buildCompanyListResponse(List<CompanyMst> listCompanyMsts,
			List<CompanyTempBean> commonTempBeans) {
		List<CompanyListResponseBean> companyListResponseBeans = new ArrayList<>();

		logger.info("<====== Start mapping companyMst to companyResponse and companyTempResponses =======>");
		for (CompanyMst companyMst : listCompanyMsts) {
			CompanyListResponseBean companyListResponseBean = new CompanyListResponseBean();
			CompanyResponse companyResponse = new CompanyResponse();
			try {
				BeanUtils.copyProperties(companyResponse, companyMst);
				companyListResponseBean.setCompanyResponse(companyResponse);
				if (!RecordStatusEnum.ACTIVE.equals(companyMst.getRecordStatus())) {
					CompanyTempBean tempCompanyResponse = commonTempBeans.stream()
							.filter(company -> company.getCompanyId().equals(companyResponse.getCompanyId())).findAny()
							.orElse(null);
					companyListResponseBean.setTempCompanyResponse(tempCompanyResponse);
				}
				companyListResponseBeans.add(companyListResponseBean);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
		}

		logger.info("<====== End mapping companyMst to companyResponse and companyTempResponses =======>");
		logger.info("<====== Start mapping new create request to companyTempResponses =======>");
		for (CompanyTempBean companyTempBean : commonTempBeans) {
			if (ActionTypeEnum.CREATE.equals(companyTempBean.getActionType())) {
				CompanyListResponseBean companyListResponseBean = new CompanyListResponseBean();
				companyListResponseBean.setTempCompanyResponse(companyTempBean);
				companyListResponseBeans.add(companyListResponseBean);
			}
		}
		logger.info("<====== Start mapping new create request to companyTempResponses =======>");

		return companyListResponseBeans;
	}

	public CommonResponse getCompanySummeryList(StatusEnum status, String userId, String userGroup) {
		logger.info("================== Start Get Company By Id =================");
		CompanySummeryListResponse summeryListResponse = new CompanySummeryListResponse();
		if (StatusEnum.ACTIVE == status) {
			List<CompanyMst> listCompanyMsts = companyMstRepository.findAllByStatus(status);
			for (CompanyMst companyMst : listCompanyMsts) {
				CompanySummeryBean summeryBean = new CompanySummeryBean();
				summeryBean.setCompanyId(companyMst.getCompanyId());
				summeryBean.setCompanyName(companyMst.getCompanyName());
				summeryBean.setStatus(companyMst.getStatus().name());
				summeryListResponse.getCompanySummeryBeans().add(summeryBean);
			}
		} else if (StatusEnum.DELETED == status) {
			Iterable<CompanyDelete> listCompanyDeletes = companyDeleteRepository.findAll();
			for (CompanyDelete companyDelete : listCompanyDeletes) {
				CompanySummeryBean summeryBean = new CompanySummeryBean();
				summeryBean.setCompanyId(companyDelete.getCompanyId());
				summeryBean.setCompanyName(companyDelete.getCompanyName());
				summeryBean.setStatus(StatusEnum.DELETED.name());
				summeryListResponse.getCompanySummeryBeans().add(summeryBean);
			}
		} else if (StatusEnum.PENDING == status || StatusEnum.INACTIVE == status) {
			CommonSearchBean bean = new CommonSearchBean();
			bean.setUserGroup(userGroup);
			bean.setRequestType(REQUEST_TYPE.name());
			bean.setActionType(ActionTypeEnum.CREATE);
			List<TempDto> tempList = companyTempComponent.getTempRecord(bean).getTempList();
			for (TempDto tempDto : tempList) {
				CompanySummeryBean summeryBean = new CompanySummeryBean();
				CompanyTempBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(),
						CompanyTempBean.class);
				summeryBean.setCompanyId(companyTempBean.getCompanyId());
				summeryBean.setCompanyName(companyTempBean.getCompanyName());
				summeryBean.setStatus(StatusEnum.INACTIVE.name());
				summeryListResponse.getCompanySummeryBeans().add(summeryBean);
			}
		}
		summeryListResponse.setReturnCode(HttpStatus.OK.value());
		summeryListResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		summeryListResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		logger.info("================== End Get Company List =================");
		return summeryListResponse;
	}

	public CommonResponse getCompanyById(String companyId, String userId, String userGroup, String requestId) {
		logger.info("================== Start Get Company By Id =================");
		Optional<CompanyMst> companyMstO = companyMstRepository.findByCompanyId(companyId);
		GetCompanyByIdResponse getCompanyByIdResponse = new GetCompanyByIdResponse();
		CompanyResponse companyBean = new CompanyResponse();
		if (companyMstO.isPresent()) {
			try {
				BeanUtils.copyProperties(companyBean, companyMstO.get());
				getCompanyByIdResponse.setCompanyBean(companyBean);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
		} else {
			FindTempByRefBean bean = new FindTempByRefBean();
			bean.setReferenceNo(companyId);
			bean.setRequestType(REQUEST_TYPE.name());
			TempDto tempDto = companyTempComponent.getTempRecordByRef(bean);
			if (null != tempDto) {
				CompanyTempBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(),
						CompanyTempBean.class);
				try {
					BeanUtils.copyProperties(companyBean, companyTempBean);
					companyBean.setStatus(StatusEnum.INACTIVE);
					getCompanyByIdResponse.setCompanyBean(companyBean);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new SystemException(
							messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()),
							e, ErrorCode.DATA_COPY_ERROR);
				}
			} else {
				throw new RecordNotFoundException(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()), ErrorCode.NO_COMPANY_RECORD_FOUND);
			}
		}
		getCompanyByIdResponse.setReturnCode(HttpStatus.OK.value());
		getCompanyByIdResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		getCompanyByIdResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End Get Company By Id =================");
		return getCompanyByIdResponse;
	}

	public CommonResponse deleteCompany(String companyId, String userId, String userGroup, String requestId) {
		logger.info("================== Start Delete Company =================");
		Optional<CompanyMst> companyMstO = companyMstRepository.findByCompanyId(companyId);
		if (companyMstO.isPresent()) {
			UpdateCompanyRequest companyBean = new UpdateCompanyRequest();
			try {
				BeanUtils.copyProperties(companyBean, companyMstO.get());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(companyBean);
			String hashTags = "";
			String referenceNo = companyMstO.get().getCompanyId();
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);
			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);

			CommonResponseBean commonResponseBean = companyTempComponent.deleteCompanyTemp(commonRequestBean,
					requestId);
			companyMstO.get().setRecordStatus(RecordStatusEnum.DELETE_PENDING);
			companyMstRepository.save(companyMstO.get());
			CommonResponse commonResponse = new CommonResponse();
			commonResponse.setErrorCode(commonResponseBean.getErrorCode());
			commonResponse.setReturnCode(commonResponseBean.getReturnCode());
			commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());

			logger.info("================== End Delete Company =================");
			return commonResponse;
		} else {
			throw new RecordNotFoundException(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()),
					ErrorCode.NO_COMPANY_RECORD_FOUND);
		}

	}

	public CommonResponse getTempRecord(FindCompanyRequest searchBy, String userId, String userGroup) {
		GetTempCompanyResponse commonResponse = new GetTempCompanyResponse();
		logger.info("================== Start Get Temp data =================");

		CommonSearchBean bean = new CommonSearchBean();
		bean.setReferenceNo(null != searchBy.getReferenceNo() ? searchBy.getReferenceNo() : null);
		bean.setRequestType(REQUEST_TYPE.name());
		bean.setHashTags(searchBy.getHashTags());
		bean.setUserGroup(userGroup);
		// bean.setUserId(userId);
		List<CompanyTempBean> commonTempBeans = getCompanyTemp(bean);
		commonResponse.setCommonTempBeans(commonTempBeans);
		commonResponse.setReturnCode(HttpStatus.OK.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End get temp data =================");
		return commonResponse;
	}

	private List<CompanyTempBean> getCompanyTemp(CommonSearchBean bean) {
		List<TempDto> tempList = companyTempComponent.getTempRecord(bean).getTempList();
		List<CompanyTempBean> commonTempBeans = new ArrayList<>();
		for (TempDto tempDto : tempList) {
			CompanyTempBean companyTempBean = commonConverter.mapToPojo(tempDto.getRequestPayload(),
					CompanyTempBean.class);
			companyTempBean.setTempId(tempDto.getId());
			companyTempBean.setRequestedBy(tempDto.getCreatedBy());
			companyTempBean.setRequestedDate(tempDto.getCreatedDate());
			companyTempBean.setActionType(tempDto.getActionType());
			commonTempBeans.add(companyTempBean);
		}
		return commonTempBeans;
	}

	public CommonResponse getApprovalPendingRecord(FindCompanyRequest searchBy, String userId, String userGroup) {
		ApprovalPendingResponse commonResponse = new ApprovalPendingResponse();
		logger.info("================== Start Get Temp data =================");

		CommonSearchBean bean = new CommonSearchBean();
		try {
			BeanUtils.copyProperties(bean, searchBy);
		} catch (Exception e) {
			throw new SystemException(
					messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
					ErrorCode.DATA_COPY_ERROR);
		}
		bean.setUserGroup(userGroup);
		bean.setUserId(userId);
		bean.setRequestType(REQUEST_TYPE.name());

		List<TempDto> tempList = companyTempComponent.getAuthPendingRecord(bean).getTempList();

		commonResponse.setTempDtos(tempList);
		commonResponse.setReturnCode(HttpStatus.OK.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End get temp data =================");
		return commonResponse;
	}

}
