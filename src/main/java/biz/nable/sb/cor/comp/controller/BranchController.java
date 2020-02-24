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
import biz.nable.sb.cor.comp.request.CreateBranchRequest;
import biz.nable.sb.cor.comp.request.DeleteBranchRequest;
import biz.nable.sb.cor.comp.request.UpdateBranchRequest;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.service.impl.BranchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/*
 * @Description	:This controller class is acting as controller 
 * 				layer for Companies.
 */

@RestController
public class BranchController {

	Logger logger = LoggerFactory.getLogger(BranchController.class);
	@Autowired
	private BranchService branchService;
	private static final String COMMON_USER_GROUP = "SYSTEM";
	private String invalidUserLoggingMsg = "User Id is invalid: {}";
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@ApiOperation(value = "Create Branch request", nickname = "Create Branch", notes = "Create Temp Branch.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Branch Request successful", response = CommonResponse.class),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PostMapping(value = "/v1/branch")
	public ResponseEntity<CommonResponse> createBranch(@Valid @RequestBody CreateBranchRequest createBranchRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method createBranch");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Create Branch for {}.", createBranchRequest.getCompanyId());
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = branchService.createTempBranch(createBranchRequest, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("SystemException occured while createBranch for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while createBranch. " + e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while createBranch for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while creating Branch. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("createBranch rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Branch List By Company ID", nickname = "Get Branch List By Company Id", notes = "Get Branch List By Company ID.", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching Branchlist successful", response = CommonGetListResponse.class),
			@ApiResponse(code = 400, message = "Get Branchlist fail"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping("/v1/branch/{companyId}")
	public ResponseEntity<CommonResponse> getCustomerids(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@RequestParam(name = "status", required = true) StatusEnum status,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCustomerids");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Customerids by CompanyId: {}", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = branchService.getBranches(companyId, status, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getCustomerids for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getCustomerids for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while getCustomerids for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getCustomerids rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Auth pending Branch List", nickname = "Get Auth pending Branch List", notes = "Get Auth pending Branch List.", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching Auth pending Branch list successful", response = CommonGetListResponse.class),
			@ApiResponse(code = 400, message = "Get Auth pending Branchlist fail", response = CommonResponse.class),
			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
	@GetMapping("/v1/branch/pending")
	public ResponseEntity<CommonResponse> getPendingAuthBranches(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getPendingAuthBranches");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Pending auth Customerids");
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = branchService.getPendingAuthBranches(userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getPendingAuthBranches for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while getPendingAuthBranches for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("getPendingAuthBranches rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Delete Branch API", nickname = "Delete Branch API", notes = "Delete Branch API", httpMethod = "DELETE")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted", response = CommonResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@DeleteMapping(value = "/v1/branch")
	public ResponseEntity<CommonResponse> deletBranch(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@RequestBody DeleteBranchRequest deleteBranchRequest) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method deletBranch");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Delete Branch : {} of Company : {}", deleteBranchRequest.getBranchId(),
						deleteBranchRequest.getCompanyId());
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = branchService.deleteBranch(deleteBranchRequest, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while deletCustomerById for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
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

	@ApiOperation(value = "Update Branch request", nickname = "Update Branch", notes = "Update Branch Request.", httpMethod = "PUT")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Branch updated successfully", response = CommonResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PutMapping(value = "/v1/branch/{companyId}/{branchId}")
	public ResponseEntity<CommonResponse> updateBranch(@RequestBody @Valid UpdateBranchRequest updateBranchRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId, @PathVariable("branchId") String branchId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method updateBranch");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Update Branch (CompId: {}, BranchId: {}).", companyId, branchId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				updateBranchRequest.setCompanyId(companyId);
				updateBranchRequest.setBranchId(branchId);
				commonResponse = branchService.updateTempCompany(updateBranchRequest, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("Error occured while updateBranch for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while updateBranch for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Unknown Error occured while updateBranch for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}

		}
		long endTime = System.currentTimeMillis();
		logger.info("updateBranch rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}
}
