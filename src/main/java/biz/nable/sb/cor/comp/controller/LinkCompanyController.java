/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.bean.AuthPendingLinkCompanyBean;
import biz.nable.sb.cor.comp.request.LinkCompanyDeleteRequest;
import biz.nable.sb.cor.comp.request.LinkCompanyRequest;
import biz.nable.sb.cor.comp.response.GetCompanyByIdResponse;
import biz.nable.sb.cor.comp.response.GetCustomerIdsResponse;
import biz.nable.sb.cor.comp.service.impl.LinkCompanyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/*
 * @Description	:This controller class is acting as controller 
 * 				layer for Companies.
 */

@RestController
public class LinkCompanyController {

	Logger logger = LoggerFactory.getLogger(LinkCompanyController.class);
	@Autowired
	private LinkCompanyService linkCompanyService;
	private static final String COMMON_USER_GROUP = "SYSTEM";
	private String invalidUserLoggingMsg = "User Id is invalid: {}";
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@ApiOperation(value = "Create Link Company request", nickname = "Create Link Company", notes = "Create Temp Link Company.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Link Company Request successful", response = CommonResponse.class),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PostMapping(value = "/v1/customer/link")
	public ResponseEntity<CommonResponse> linkCompany(@Valid @RequestBody LinkCompanyRequest linkCompanyRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method linkCompany");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Link Company for {}.", linkCompanyRequest.getParentCompanyId());
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = linkCompanyService.createTempLink(linkCompanyRequest, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("SystemException occured while linkCompany for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while linkCompany. " + e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while linkCompany for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while creating Company. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("updateBranch rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Link Company List", nickname = "Get Link Company List", notes = "Get Link Company List.", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching link company list successful", response = GetCustomerIdsResponse.class),
			@ApiResponse(code = 400, message = "Get link company list fail"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping("/v1/customer/{companyId}")
	public ResponseEntity<GetCustomerIdsResponse> getCustomerids(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCustomerids");
		GetCustomerIdsResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new GetCustomerIdsResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Customerids by CompanyId: {}", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = linkCompanyService.getCustomerIds(companyId, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getCustomerids for {}.", e);
				commonResponse = new GetCustomerIdsResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getCustomerids for {}.", e.getMessage());
				commonResponse = new GetCustomerIdsResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while getCustomerids for {}.", e);
				commonResponse = new GetCustomerIdsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("getCustomerids rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Auth pending Link Company List", nickname = "Get Auth pending Link Company List", notes = "Get Auth pending Link Company List.", httpMethod = "GET")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Fetching Auth pending link company list successful"),
			@ApiResponse(code = 400, message = "Get Auth pending link company list fail"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping("/v1/customer/pending")
	public ResponseEntity<AuthPendingLinkCompanyBean> getPendingAuthCustomerids(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getPendingAuthCustomerids");
		AuthPendingLinkCompanyBean commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new AuthPendingLinkCompanyBean(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Pending auth Customerids");
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = linkCompanyService.getPendingAuthCustomerIds(userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getPendingAuthCustomerids for {}.", e);
				commonResponse = new AuthPendingLinkCompanyBean(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getPendingAuthCustomerids for {}.", e.getMessage());
				commonResponse = new AuthPendingLinkCompanyBean(HttpStatus.NOT_FOUND.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while getPendingAuthCustomerids for {}.", e);
				commonResponse = new AuthPendingLinkCompanyBean(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						e.getMessage(), ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getCustomerids rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Delete Customer ID API", nickname = "Delete Customer ID API", notes = "Delete Customer ID API", httpMethod = "DELETE")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully deleted", response = GetCompanyByIdResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@DeleteMapping(value = "/v1/customer/{customerId}")
	public ResponseEntity<CommonResponse> deletCustomerById(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("customerId") String customerId,
			@RequestBody LinkCompanyDeleteRequest linkCompanyDeleteRequest) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method deletCustomerById");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Delete Customer By Id: {}", customerId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = linkCompanyService.deleteCompany(linkCompanyDeleteRequest, userId, userGroup,
						requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while deletCustomerById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while deletCustomerById for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while deletCustomerById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, null, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("deletCustomerById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

}
