package biz.nable.sb.cor.comp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.response.CommonGetListResponse;
import biz.nable.sb.cor.comp.response.FeatureResponse;
import biz.nable.sb.cor.comp.service.impl.FeaturesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class FeaturesController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FeaturesService featuresService;

	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@ApiOperation(value = "Get All Features", nickname = "Get All Featuress", notes = "Get All Features", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching All Features successful", response = CommonGetListResponse.class, responseContainer = "Res conta"),
			@ApiResponse(code = 400, message = "Get All Features fail", response = CommonResponse.class),
			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
	@GetMapping("/v1/features")
	public ResponseEntity<FeatureResponse> getAllFeatures(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getAllFeatures");
		FeatureResponse commonResponse;

		try {
			commonResponse = featuresService.getAllFeatures();
			logger.info(commonResponse.getReturnMessage());
		} catch (SystemException e) {
			logger.error("Error occured while getAllFeatures for {}.", e);
			commonResponse = new FeatureResponse();
			commonResponse.setErrorCode(e.getErrorCode());
			commonResponse.setReturnMessage(e.getMessage());
			commonResponse.setReturnCode(HttpStatus.NOT_ACCEPTABLE.value());
		} catch (Exception e) {
			logger.error("Error occured while getAllFeatures for {}.", e);
			commonResponse = new FeatureResponse();
			commonResponse.setErrorCode(ErrorCode.UNKNOWN_ERROR);
			commonResponse.setReturnMessage(e.getMessage());
			commonResponse.setReturnCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		long endTime = System.currentTimeMillis();
		logger.info("getAllFeatures rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

	@ApiOperation(value = "Get Company Features", nickname = "Get Company Features", notes = "Get Company Features", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching Company Features successful", response = CommonGetListResponse.class, responseContainer = "Res conta"),
			@ApiResponse(code = 400, message = "Get Company Features fail", response = CommonResponse.class),
			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
	@GetMapping("/v1/features/company/{companyId}")
	public ResponseEntity<FeatureResponse> getCompanayFeatures(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup,
			@PathVariable("companyId") String companyId) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method getCompanyFeatures");
		FeatureResponse commonResponse;

		try {
			commonResponse = featuresService.getCompanyFeatures(companyId);
			logger.info(commonResponse.getReturnMessage());
		} catch (SystemException e) {
			logger.error("Error occured while getCompanyFeatures for {}.", e);
			commonResponse = new FeatureResponse();
			commonResponse.setErrorCode(e.getErrorCode());
			commonResponse.setReturnMessage(e.getMessage());
			commonResponse.setReturnCode(HttpStatus.NOT_ACCEPTABLE.value());
		} catch (Exception e) {
			logger.error("Error occured while getCompanyFeatures for {}.", e);
			commonResponse = new FeatureResponse();
			commonResponse.setErrorCode(ErrorCode.UNKNOWN_ERROR);
			commonResponse.setReturnMessage(e.getMessage());
			commonResponse.setReturnCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		long endTime = System.currentTimeMillis();
		logger.info("getCompanyFeatures rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

//	@ApiOperation(value = "Get User Features", nickname = "Get User Features", notes = "Get User Features", httpMethod = "GET")
//	@ApiResponses(value = {
//			@ApiResponse(code = 200, message = "Fetching User Features successful", response = CommonGetListResponse.class, responseContainer = "Res conta"),
//			@ApiResponse(code = 400, message = "Get User Features fail", response = CommonResponse.class),
//			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
//	@GetMapping("/v1/features/user/{corpUserId}")
//	public ResponseEntity<FeatureResponse> getUserFeatures(
//			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
//			@RequestHeader(name = "userId", required = true) String userId,
//			@RequestHeader(name = "userGroup", required = false) String userGroup,
//			@PathVariable("corpUserId") String corpUserId) {
//		MDC.put(REQUEST_ID_HEADER, requestId);
//		long startTime = System.currentTimeMillis();
//
//		logger.info("Start exicute method getUserFeatures");
//		FeatureResponse commonResponse;
//
//		try {
//			commonResponse = featuresService.getUserFeatures(corpUserId);
//			logger.info(commonResponse.getReturnMessage());
//		} catch (SystemException e) {
//			logger.error("Error occured while getUserFeatures for {}.", e);
//			commonResponse = new FeatureResponse();
//			commonResponse.setErrorCode(e.getErrorCode());
//			commonResponse.setReturnMessage(e.getMessage());
//			commonResponse.setReturnCode(HttpStatus.NOT_ACCEPTABLE.value());
//		} catch (Exception e) {
//			logger.error("Error occured while getUserFeatures for {}.", e);
//			commonResponse = new FeatureResponse();
//			commonResponse.setErrorCode(ErrorCode.UNKNOWN_ERROR);
//			commonResponse.setReturnMessage(e.getMessage());
//			commonResponse.setReturnCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//
//		long endTime = System.currentTimeMillis();
//		logger.info("getUserFeatures rate: avg_resp={}", (endTime - startTime));
//		MDC.clear();
//		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
//	}

}
