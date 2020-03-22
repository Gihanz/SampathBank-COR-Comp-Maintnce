package biz.nable.sb.cor.comp.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.bean.AuthPendingLinkCompanyBean;
import biz.nable.sb.cor.comp.bean.CustomerIdResponseBean;
import biz.nable.sb.cor.comp.bean.LinkCompanyResponseBean;
import biz.nable.sb.cor.comp.component.LinkCompanyTempComponent;
import biz.nable.sb.cor.comp.db.entity.CompanyCummData;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.db.repository.LinkCompanyRepository;
import biz.nable.sb.cor.comp.request.LinkCompanyDeleteRequest;
import biz.nable.sb.cor.comp.request.LinkCompanyRequest;
import biz.nable.sb.cor.comp.response.GetCustomerIdsResponse;
import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Service
public class LinkCompanyService {

	@Autowired
	CommonConverter commonConverter;

	@Autowired
	LinkCompanyRepository linkCompanyRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	MessageSource messageSource;

	@Autowired
	LinkCompanyTempComponent linkCompanyTempComponent;

	private static final RequestTypeEnum REQUEST_TYPE = RequestTypeEnum.LINK_COMPANY;

	private static final String LINK_COMPANY_HASH_TAG = "#parentCompanyId=";

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse createTempLink(LinkCompanyRequest linkCompanyRequest, String userId, String userGroup,
			String requestId) {
		logger.info("================== Start Link Company Request =================");
		logger.info("Link company {} to {}", linkCompanyRequest.getCustomerId(),
				linkCompanyRequest.getParentCompanyId());
		CommonResponse commonResponse = new CommonResponse();
		Optional<CompanyCummData> optional = linkCompanyRepository.findByParentCompanyIdAndCustomerId(
				linkCompanyRequest.getParentCompanyId(), linkCompanyRequest.getCustomerId());
		Optional<CompanyMst> optional2 = companyMstRepository.findByCompanyId(linkCompanyRequest.getParentCompanyId());
		if (optional.isPresent()) {

			logger.info(messageSource.getMessage(ErrorCode.CUSTOMER_ID_ALREADY_LINK, null,
					LocaleContextHolder.getLocale()));
			commonResponse.setErrorCode(ErrorCode.CUSTOMER_ID_ALREADY_LINK);
			commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
			commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.CUSTOMER_ID_ALREADY_LINK, null,
					LocaleContextHolder.getLocale()));
		} else {
			if (!optional2.isPresent()) {
				logger.info(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()));
				commonResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
				commonResponse.setReturnCode(HttpStatus.CONFLICT.value());
				commonResponse.setReturnMessage(messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null,
						LocaleContextHolder.getLocale()));

			} else {
				CommonRequestBean commonRequestBean = new CommonRequestBean();
				commonRequestBean.setCommonTempBean(linkCompanyRequest);
				String hashTags = LINK_COMPANY_HASH_TAG.concat(String.valueOf(linkCompanyRequest.getParentCompanyId()));
				String referenceNo = String.valueOf(linkCompanyRequest.getCustomerId()).concat("#")
						.concat(linkCompanyRequest.getParentCompanyId());
				commonRequestBean.setHashTags(hashTags);
				commonRequestBean.setReferenceNo(referenceNo);
				commonRequestBean.setRequestType(REQUEST_TYPE.name());
				commonRequestBean.setUserGroup(userGroup);
				commonRequestBean.setUserId(userId);
				logger.info("Create commonRequestBean");
				CommonResponseBean commonResponseBean = linkCompanyTempComponent.createTempRecord(commonRequestBean,
						requestId);

				commonResponse.setErrorCode(commonResponseBean.getErrorCode());
				commonResponse.setReturnCode(commonResponseBean.getReturnCode());
				commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());
				logger.info("================== End Link Company Request =================");
			}
		}
		return commonResponse;
	}

	public GetCustomerIdsResponse getCustomerIds(String companyId, String userId, String userGroup, String requestId) {
		logger.info("================== Start Get Company By Id =================");
		Optional<CompanyMst> companyMstO = companyMstRepository.findByCompanyId(companyId);

		List<CustomerIdResponseBean> customerIds = getTempList(companyId, userId, userGroup);
		GetCustomerIdsResponse customerIdsResponse = new GetCustomerIdsResponse();
		customerIdsResponse.getListOfLinkedCompanies().addAll(customerIds);
		if (companyMstO.isPresent()) {
			List<CompanyCummData> linkCompanies = linkCompanyRepository.findByParentCompanyId(companyId);

			for (CompanyCummData companyCummData : linkCompanies) {
				Optional<CompanyMst> customerO = companyMstRepository.findByCompanyId(companyCummData.getCustomerId());
				CustomerIdResponseBean customerId = new CustomerIdResponseBean();
				if (customerO.isPresent()) {
					customerId.setCompanyId(customerO.get().getCompanyId());
					customerId.setCompanyName(customerO.get().getCompanyName());
					customerId.setStatus(companyCummData.getStatus().name());
				} else {
					customerId.setCompanyId(companyCummData.getCustomerId());
					customerId.setStatus(companyCummData.getStatus().name());
				}
				customerIdsResponse.getListOfLinkedCompanies().add(customerId);
			}

		}
		customerIdsResponse.setReturnCode(HttpStatus.OK.value());
		customerIdsResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		customerIdsResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End Get Company By Id =================");
		return customerIdsResponse;
	}

	private List<CustomerIdResponseBean> getTempList(String companyId, String userId, String userGroup) {
		logger.info("Start get temp Link Companys for {}", companyId);

		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		bean.setHashTags(LINK_COMPANY_HASH_TAG.concat(companyId));
		bean.setUserGroup(userGroup);
		bean.setUserId(userId);
		List<TempDto> tempList = linkCompanyTempComponent.getAuthPendingRecord(bean).getTempList();
		List<CustomerIdResponseBean> customerIdsResponse = new ArrayList<>();
		for (TempDto tempDto : tempList) {
			LinkCompanyRequest customerId = commonConverter.mapToPojo(tempDto.getRequestPayload(),
					LinkCompanyRequest.class);
			Optional<CompanyMst> customerO = companyMstRepository.findByCompanyId(customerId.getCustomerId());
			if (customerO.isPresent()) {
				CustomerIdResponseBean customerIdResopnse = new CustomerIdResponseBean();
				customerIdResopnse.setCompanyId(customerO.get().getCompanyId());
				customerIdResopnse.setCompanyName(customerO.get().getCompanyName());
				customerIdResopnse.setStatus(StatusEnum.PENDING.name());
				customerIdsResponse.add(customerIdResopnse);
			}

		}
		logger.info("End get temp Link Companys ");

		return customerIdsResponse;
	}

	public AuthPendingLinkCompanyBean getPendingAuthCustomerIds(String userId, String userGroup, String requestId) {
		logger.info("================== Start Get Company By Id =================");

		CommonSearchBean bean = new CommonSearchBean();
		bean.setRequestType(REQUEST_TYPE.name());
		List<TempDto> tempList = linkCompanyTempComponent.getTempRecord(bean).getTempList();
		AuthPendingLinkCompanyBean authPendingLinkCompanyBean = new AuthPendingLinkCompanyBean();
		for (TempDto tempDto : tempList) {
			LinkCompanyRequest customerId = commonConverter.mapToPojo(tempDto.getRequestPayload(),
					LinkCompanyRequest.class);
			Optional<CompanyMst> companyO = companyMstRepository.findByCompanyId(customerId.getParentCompanyId());
			if (companyO.isPresent()) {
				LinkCompanyResponseBean linkCompanyBean = new LinkCompanyResponseBean();
				linkCompanyBean.setCompanyId(companyO.get().getCompanyId());
				linkCompanyBean.setCompanyName(companyO.get().getCompanyName());
				linkCompanyBean.setLinkedCompanyID(customerId.getCustomerId());
				linkCompanyBean.setAuthorizationId(Long.parseLong(tempDto.getApprovalId()));
				linkCompanyBean.setSignature(tempDto.getSignature());
				linkCompanyBean.setActionType(tempDto.getActionType());
				linkCompanyBean.setStatus(StatusEnum.PENDING);
				if (ActionTypeEnum.CREATE.equals(tempDto.getActionType())) {
					authPendingLinkCompanyBean.getNewCustomers().add(linkCompanyBean);
				} else if (ActionTypeEnum.DELETE.equals(tempDto.getActionType())) {
					authPendingLinkCompanyBean.getDeletedCustomers().add(linkCompanyBean);
				}

			}

		}
		authPendingLinkCompanyBean.setReturnCode(HttpStatus.OK.value());
		authPendingLinkCompanyBean.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		authPendingLinkCompanyBean.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End getPendingAuthCustomerIds =================");

		return authPendingLinkCompanyBean;
	}

	public CommonResponse deleteCompany(LinkCompanyDeleteRequest linkCompanyDeleteRequest, String userId,
			String userGroup, String requestId) {

		logger.info("================== Start Delete Company =================");
		Optional<CompanyCummData> optional = linkCompanyRepository
				.findBycustomerId(linkCompanyDeleteRequest.getCustomerId());
		if (optional.isPresent()) {
			LinkCompanyRequest linkCompanyBean = new LinkCompanyRequest();
			try {
				BeanUtils.copyProperties(linkCompanyBean, optional.get());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATA_COPY_ERROR, null, LocaleContextHolder.getLocale()), e,
						ErrorCode.DATA_COPY_ERROR);
			}
			CommonRequestBean commonRequestBean = new CommonRequestBean();
			commonRequestBean.setCommonTempBean(linkCompanyBean);
			String hashTags = LINK_COMPANY_HASH_TAG.concat(linkCompanyBean.getParentCompanyId());
			String referenceNo = linkCompanyBean.getCustomerId().concat("#")
					.concat(linkCompanyDeleteRequest.getParentCompanyId());
			commonRequestBean.setHashTags(hashTags);
			commonRequestBean.setReferenceNo(referenceNo);
			commonRequestBean.setRequestType(REQUEST_TYPE.name());
			commonRequestBean.setUserGroup(userGroup);
			commonRequestBean.setUserId(userId);

			CommonResponseBean commonResponseBean = linkCompanyTempComponent.deleteLinkCompanyTemp(commonRequestBean,
					requestId);
			optional.get().setRecordStatus(RecordStatusEnum.DELETE_PENDING);
			linkCompanyRepository.save(optional.get());
			CommonResponse commonResponse = new CommonResponse();
			commonResponse.setErrorCode(commonResponseBean.getErrorCode());
			commonResponse.setReturnCode(commonResponseBean.getReturnCode());
			commonResponse.setReturnMessage(commonResponseBean.getReturnMessage());

			logger.info("================== End Delete Company =================");
			return commonResponse;
		} else {
			throw new RecordNotFoundException(messageSource.getMessage(ErrorCode.NO_LINK_COMPANY_RECORD_FOUND, null,
					LocaleContextHolder.getLocale()), ErrorCode.NO_LINK_COMPANY_RECORD_FOUND);
		}
	}

}
