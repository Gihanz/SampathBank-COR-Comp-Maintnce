package biz.nable.sb.cor.comp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.response.CompanyAccountResponse;
import biz.nable.sb.cor.comp.service.impl.AccountsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class AccountsController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountsService accountsService;
	private String invalidUserLoggingMsg = "User Id is invalid: {}";
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@ApiOperation(value = "Get AccountsList By ID API", nickname = "Get AccountsList By ID API", notes = "Get AccountsList By ID API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = CommonGetListResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/v1/accounts/{companyId}")
	public ResponseEntity<CommonGetListResponse<CompanyAccountResponse>> getAccountListById(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getAccountListById");
		CommonGetListResponse<CompanyAccountResponse> commonResponse = new CommonGetListResponse<>();
		;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse.setReturnCode(HttpStatus.BAD_REQUEST.value());
			commonResponse.setErrorCode(ErrorCode.INVALID_USER_ID);
			commonResponse.setReturnMessage(messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID),
					new Object[] { userId }, LocaleContextHolder.getLocale()));
		} else {
			try {
				logger.debug("Fetch Get AccountsList By ID : {}", companyId);

				commonResponse = accountsService.getAccountListByCompanyId(companyId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occured while getAccountListById for {}.", e);
				commonResponse.setReturnCode(HttpStatus.NOT_ACCEPTABLE.value());
				commonResponse.setErrorCode(e.getErrorCode());
				commonResponse.setReturnMessage(e.getMessage());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occured while getAccountListById for {}.", e.getMessage());
				commonResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
				commonResponse.setErrorCode(e.getErrorCode());
				commonResponse.setReturnMessage(e.getMessage());
			} catch (Exception e) {
				logger.error("Error occured while getAccountListById for {}.", e);
				commonResponse.setReturnCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				commonResponse.setErrorCode(ErrorCode.UNKNOWN_ERROR);
				commonResponse.setReturnMessage(e.getMessage());
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("getCompanyById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}
}
