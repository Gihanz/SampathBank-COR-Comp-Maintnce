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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.bean.FindCompanyBean;
import biz.nable.sb.cor.comp.request.CreateCompanyRequest;
import biz.nable.sb.cor.comp.request.FindCompanyRequest;
import biz.nable.sb.cor.comp.request.UpdateCompanyRequest;
import biz.nable.sb.cor.comp.response.CompanyListResponse;
import biz.nable.sb.cor.comp.response.GetCompanyByIdResponse;
import biz.nable.sb.cor.comp.service.impl.CompanyService;
import biz.nable.sb.cor.comp.validator.Validator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/*
 * @Description	:This controller class is acting as controller 
 * 				layer for Companies.
 */

@RestController
public class CompanyController {

	Logger logger = LoggerFactory.getLogger(CompanyController.class);
	@Autowired
	private CompanyService companyService;
	private static final String COMMON_USER_GROUP = "SYSTEM";
	private String invalidUserLoggingMsg = "User Id is invalid: {}";
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@ApiOperation(value = "Create Company request", nickname = "Create Company", notes = "Create Temp Company.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Create new Company successful", response = CommonResponse.class),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PostMapping(value = "/v1/company")
	public ResponseEntity<CommonResponse> createCompany(@Valid @RequestBody CreateCompanyRequest createCompanyRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method createCompany");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			if (Validator.createCompanyValidateRequest(createCompanyRequest)) {
				try {
					logger.debug("Creating Company for {}.", createCompanyRequest.getCompanyName());
					if (StringUtils.isEmpty(userGroup)) {
						userGroup = COMMON_USER_GROUP;
					}
					commonResponse = companyService.createTempCompany(createCompanyRequest, userId, userGroup,
							requestId);
					logger.info(commonResponse.getReturnMessage());
				} catch (SystemException e) {
					logger.info("SystemException occured while creating Company for {}.", e.getMessage());
					commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error occured while creating Company. " + e.getMessage(), e.getErrorCode());
				} catch (Exception e) {
					logger.error("Error occured while creating Company for {}.", e);
					commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error occured while creating Company. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
				}
			} else {
				logger.error(messageSource.getMessage(ErrorCode.INVALID_REQUEST_PARAMETER, null,
						LocaleContextHolder.getLocale()));
				commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(), "Input parameters are not valid.",
						"");
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("createCompany rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Update Company request", nickname = "Update Company", notes = "Update compay Request.", httpMethod = "PUT")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Company updated successfully", response = CommonResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PutMapping(value = "/v1/company/{companyId}")
	public ResponseEntity<CommonResponse> updateCompany(@RequestBody UpdateCompanyRequest updateCompanyRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method updateCompany");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Update Company (CompId: {}).", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = companyService.updateTempCompany(updateCompanyRequest, companyId, userId, userGroup,
						requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("Error occured while updating Company for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while updating Company for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Unknown Error occured while updating Company for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("updateCompany rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Company List API", nickname = "Get Company List API", notes = "Get Company List API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = CompanyListResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/v1/company")
	public ResponseEntity<CommonResponse> getCompanyList(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup, FindCompanyBean findCompanyBean) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCompanyList");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				String log = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(findCompanyBean);
				logger.debug("Fetch Company List with request criteris: {}", log);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = companyService.getCompanyList(findCompanyBean, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("Error occured while getCompanyList for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getCompanyById for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Unknown Error occured while getCompanyList for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, null, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getCompanyList rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Company Summery List API", nickname = "Get Company Summery List API", notes = "Get Company Summery List API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = CompanyListResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/v1/company/summery")
	public ResponseEntity<CommonResponse> getCompanySummeryList(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@RequestParam(name = "status", required = true) StatusEnum status) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCompanyList");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Company Summery List with tatus: {}", status.name());
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = companyService.getCompanySummeryList(status, userId, userGroup);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("Error occured while getCompanyList for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getCompanyById for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Unknown Error occured while getCompanyList for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, null, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getCompanyList rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Delete Company By ID API", nickname = "Delete Company By ID API", notes = "Delete Company By ID API", httpMethod = "DELETE")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = GetCompanyByIdResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@DeleteMapping(value = "/v1/company/{companyId}")
	public ResponseEntity<CommonResponse> deletCompanyById(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method deletCompanyById");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Delete Company by Id: {}", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = companyService.deleteCompany(companyId, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while deletCompanyById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while deletCompanyById for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while deleteCompanyById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, null, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("deletCompanyById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Company By ID API", nickname = "Get Company By ID API", notes = "Get Company By ID API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = GetCompanyByIdResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/v1/company/{companyId}")
	public ResponseEntity<CommonResponse> getCompanyById(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCompanyById");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Company by Id: {}", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = companyService.getCompanyById(companyId, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getCompanyById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getCompanyById for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while getCompanyById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getCompanyById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@GetMapping("/v1/company/temp")
	public ResponseEntity<CommonResponse> getTempRecords(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup, FindCompanyRequest searchBy) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		String search = null == searchBy ? "EMPTY" : searchBy.toString();
		logger.info("Start get temp data (serchBy: {})", search);
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = companyService.getTempRecord(searchBy, userId, userGroup);
			logger.info("Successfuly fetched ");
		} catch (SystemException e) {
			logger.info("Error occured while geting Temp record {}.", e.getMessage());
			commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					e.getErrorCode());
		} catch (RecordNotFoundException e) {
			logger.info("RecordNotFoundException occured while geting Temp record, {}.", e.getMessage());
			commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
		} catch (Exception e) {
			logger.error("Unknown Error occured while  geting Temp record.", e);
			commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					ErrorCode.UNKNOWN_ERROR);
		}
		long endTime = System.currentTimeMillis();
		logger.info("updateCompany rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

}
