/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.controller;

import javax.validation.Valid;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.comp.request.BlockRequest;
import biz.nable.sb.cor.comp.request.DeleteUserRequest;
import biz.nable.sb.cor.comp.response.*;
import biz.nable.sb.cor.comp.utility.ErrorDescription;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import biz.nable.sb.cor.comp.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.request.CreateUserRequest;
import biz.nable.sb.cor.comp.service.impl.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Objects;


/*
 * @Description	:This controller class is acting as controller 
 * 				layer for Companies.
 */

@RestController
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	private static final String COMMON_USER_GROUP = "SYSTEM";

	private static final String REQUEST_ID_HEADER = "request-id";
	private static final String ADMIN_USER_ID = "adminUserId";
	private static final String USER_ID = "userId";
	private static final String USER_GROUP = "userGroup";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private Validator inputValidator;

	@ApiOperation(value = "Create User request", nickname = "Create User", notes = "Create Temp User.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ErrorDescription.REQUEST_SUCCESS, response = CommonResponse.class),
			@ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
			@ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
	@PostMapping(value = "/v1/user")
	public ResponseEntity<CommonResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
			@RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestHeader(name = USER_GROUP, required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start execute method createUser: CreateUserRequest: {} RequestId: {} AdminUserID: {} UserGroup: {}",
				createUserRequest ,requestId ,adminUserId ,userGroup);
		CommonResponse commonResponse;
//		commonResponse =  inputValidator.validateRequest(createUserRequest, userId);
//		if(commonResponse != null){
//			logger.info("CreateUser method validation response: {}",
//					ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
//			return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
//		}else {
			try {
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = userService.createTempUser(createUserRequest, userGroup, requestId, adminUserId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("SystemException occurred while createUser for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occurred while createUser. " + e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occurred while createUser for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occurred while creating User. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
			}

			long endTime = System.currentTimeMillis();
			logger.info("createUser rate: avg_resp={}", (endTime - startTime));
			MDC.clear();
			logger.info("createUser method Response: {}", ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
			return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
//			return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
//		}
	}

	@ApiOperation(value = "Update User request", nickname = "Update User", notes = "Update user request.", httpMethod = "PUT")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ErrorDescription.REQUEST_UPDATE_SUCCESS, response = CommonResponse.class),
			@ApiResponse(code = 404, message = ErrorDescription.RESOURCE_NOT_FOUND),
			@ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
			@ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
	@PutMapping(value = "/v1/user/{userId}")
	public ResponseEntity<CommonResponse> updateUser(@RequestBody CreateUserRequest createUserRequest,
													 @PathVariable(name = USER_ID) String userId,
													 @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
													 @RequestHeader(name = REQUEST_ID_HEADER) String requestId,
													 @RequestHeader(name = USER_GROUP, required = false) String userGroup ) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		logger.info("Start execute method updateUser: CreateUserRequest: {} RequestId: {} AdminUserID: {} UserID: {} UserGroup: {}",
				createUserRequest ,requestId ,adminUserId ,userId ,userGroup);
		CommonResponse commonResponse = new CommonResponse();
//		commonResponse =  inputValidator.validateRequest(createUserRequest, userId);
//		if(commonResponse != null){
//			logger.info("CreateUser method validation response: {}",
//					ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
//			return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
//		}else {
			try {
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = userService.updateTempUser(createUserRequest, userId, userGroup, requestId, adminUserId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("Error occurred while updating Company for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						e.getErrorCode());
			} catch (RecordNotFoundException e) {
				logger.info("RecordNotFoundException occurred while updating Company for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Unknown Error occurred while updating Company for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}
			long endTime = System.currentTimeMillis();
			logger.info("updateCompany rate: avg_resp={}", (endTime - startTime));
			MDC.clear();
			return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
//		}
	}

	@ApiOperation(value = "Get Auth pending User List", nickname = "Get Auth pending User List", notes = "Get Auth pending User List.", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching Auth pending User list successful", response = ApprovalPendingUserResponse.class),
			@ApiResponse(code = 400, message = "Get Auth pending user list fail", response = CommonResponse.class),
			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
	@GetMapping("/v1/user/requests")
	public ResponseEntity<CommonResponse> getPendingAuthUsers(
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
			@RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestHeader(name = USER_GROUP, required = false) String userGroup,
            @RequestParam(name = "approvalStatus", required = false) String approvalStatus) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		logger.info("Start execute method getPendingAuthUser's");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(adminUserId)) {
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(ErrorCode.INVALID_USER_ID, new Object[] { adminUserId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Fetch Pending auth Customer's");
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = userService.getPendingAuthUseres(adminUserId, userGroup, approvalStatus);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occurred while getPendingAuthUser's for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occurred while getPendingAuthUser's for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("getPendingAuthUser's rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
	}

	@ApiOperation(value = "Get User By CompanyID & RecordStatus API", nickname = "Get User By CompanyID & RecordStatus API", notes = "Get User By CompanyID & RecordStatus API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = UserResponseList.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error")})
	@GetMapping(value = "/v1/user")
	public ResponseEntity<UserResponseList> getUserById(
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
			@RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestHeader(name = USER_GROUP, required = false) String userGroup,
			@RequestParam(name = "companyId", required = false) String companyId,
			@RequestParam(name = "recordStatus", required = false) RecordStatuUsersEnum recordStatus){
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
        UserResponseList userListResponse = new UserResponseList();
		CommonResponse commonResponse = new CommonResponse();
		try {
			userListResponse = userService.getUserList(companyId, recordStatus);
			logger.info(userListResponse.getReturnMessage());
		} catch (SystemException e) {
			logger.error("Error occurred while getUserById for {}.", e.toString());
			commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
					e.getErrorCode());
		} catch (Exception e) {
			logger.error("Error occurred while getUserById for {}.", e.toString());
			commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					ErrorCode.UNKNOWN_ERROR);
		}
		long endTime = System.currentTimeMillis();
		logger.info("getUserById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(userListResponse.getReturnCode()))).body(userListResponse);
	}

	@ApiOperation(value = "Delete User API", nickname = "Delete User API", notes = "Delete User API", httpMethod = "PUT")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted", response = CommonResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PutMapping(value = "/v1/user")
	public ResponseEntity<CommonResponse> deletUser(
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
            @RequestHeader(name = USER_GROUP, required = false) String userGroup,
			@RequestHeader(name = USER_ID) String userId,
            @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestBody DeleteUserRequest deleteUserRequest) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		logger.info("Start execute method deleteUser");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(ErrorCode.INVALID_USER_ID, new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
            logger.error("UserID IsEmpty response: {} userID: {}", commonResponse, userId);
		} else {
			try {
				commonResponse = userService.deleteUser(userId, requestId, userGroup, adminUserId, deleteUserRequest);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occurred while deleteUserById for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occurred while deleteUserById for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, new Object[] { e.toString() }, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("deleteUserById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
	}

	@ApiOperation(value = "Get User By UserID API", nickname = "Get User By UserID API", notes = "Get User By UserID API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = UserListResponseByUserID.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error")})
	@GetMapping(value = "/v1/user/{userId}")
	public ResponseEntity<UserListResponseByUserID> getUserByUserId(
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
			@RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestHeader(name = USER_GROUP, required = false) String userGroup,
			@PathVariable(name = USER_ID, required = false) String userId){

		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		UserListResponseByUserID userListResponse = new UserListResponseByUserID();
		try {
			userListResponse = userService.getUserListByUserID(userId);
			logger.info(userListResponse.getReturnMessage());
		} catch (SystemException e) {
			logger.error("Error occurred while getUserById for {}.", e.toString());
            userListResponse = new UserListResponseByUserID(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
					e.getErrorCode());
		} catch (Exception e) {
			logger.error("Error occurred while getUserById for {}.", e.toString());
            userListResponse = new UserListResponseByUserID(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					ErrorCode.UNKNOWN_ERROR);
		}
		long endTime = System.currentTimeMillis();
		logger.info("getUserById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(userListResponse.getReturnCode()))).body(userListResponse);
	}

	@ApiOperation(value = "Change status block or active", nickname = "Change status block or active", notes = "Change status block or active", httpMethod = "PATCH")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully Fetched", response = CommonResponse.class),
			@ApiResponse(code = 404, message = "Resource not found"),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error")})
	@PatchMapping(value = "/v1/user/{companyId}/{userId}")
	public ResponseEntity<CommonResponse> setBlockActive(
			@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
			@RequestHeader(name = ADMIN_USER_ID) String adminUserId,
			@RequestHeader(name = USER_GROUP, required = false) String userGroup,
			@PathVariable(name = USER_ID, required = false) String userId,
			@PathVariable(name = "companyId", required = false) String companyId,
			@RequestBody BlockRequest blockRequest){
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();
		CommonResponse commonResponse = new CommonResponse();

		if (StringUtils.isEmpty(userId)) {
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(ErrorCode.INVALID_USER_ID, new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
			logger.error("UserID IsEmpty response: {} userID: {}", commonResponse, userId);
		} else {
			try {
				commonResponse = userService.changeStatus(userId, companyId, requestId, userGroup, adminUserId, blockRequest);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.error("Error occurred while setBlockActive for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
						e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occurred while setBlockActive for {}.", e.toString());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, new Object[] { e.toString() }, LocaleContextHolder.getLocale())
								+ e.getMessage(),
						ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("deleteUserById rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
	}

}
