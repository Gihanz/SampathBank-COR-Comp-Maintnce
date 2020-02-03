/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.service.impl.ApprovalService;

/*
 * @Description	:This controller class is acting as controller 
 * 				layer for Companies.
 */

@RestController
public class ApprovelController {

	Logger logger = LoggerFactory.getLogger(ApprovelController.class);
	@Autowired
	private ApprovalService approvalService;
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@GetMapping("/v1/appvoval")
	public ResponseEntity<CommonResponse> getApprovalPendingRecords(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		logger.info("Start get auth pending data");
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = approvalService.getApprovalPendingRecord(userId, userGroup);
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
