/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.controller;

import javax.validation.Valid;

import biz.nable.sb.cor.common.exception.RecordNotFoundException;
import biz.nable.sb.cor.comp.db.entity.CompanyUser;
import biz.nable.sb.cor.comp.db.entity.UserFeatures;
import biz.nable.sb.cor.comp.db.entity.UserMst;
import biz.nable.sb.cor.comp.response.*;
import biz.nable.sb.cor.comp.utility.StatusEnum;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private String invalidUserLoggingMsg = "User Id is invalid: {}";
	private static final String REQUEST_ID_HEADER = "request-id";
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@ApiOperation(value = "Create User request", nickname = "Create User", notes = "Create Temp User.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User Request successful", response = CommonResponse.class),
			@ApiResponse(code = 400, message = "Input parameters are not valid"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@PostMapping(value = "/v1/user")
	public ResponseEntity<CommonResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start exicute method createUser");
		CommonResponse commonResponse;
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
				logger.debug("Create User for {}.", createUserRequest.getCompanyId());
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				commonResponse = userService.createTempUser(createUserRequest, userId, userGroup, requestId);
				logger.info(commonResponse.getReturnMessage());
			} catch (SystemException e) {
				logger.info("SystemException occured while createUser for {}.", e.getMessage());
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while createUser. " + e.getMessage(), e.getErrorCode());
			} catch (Exception e) {
				logger.error("Error occured while createUser for {}.", e);
				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error occured while creating User. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("createUser rate: avg_resp={}", (endTime - startTime));
		MDC.clear();
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
	}

//
//	@ApiOperation(value = "Get User List By Company ID", nickname = "Get User List By Company Id", notes = "Get User List By Company ID.", httpMethod = "GET")
	@ApiOperation(value = "Get User List ", nickname = "Get User List", notes = "Get User List.", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fetching Userlist successful", response = CommonGetListResponse.class),
			@ApiResponse(code = 400, message = "Get Userlist fail"),
			@ApiResponse(code = 500, message = "Internal server error") })
//	@GetMapping("/v1/user/{companyId}"
	@GetMapping("/v1/user")
	public ResponseEntity<CommonResponse> getCustomerids(
			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
			@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "userGroup", required = false) String userGroup
//			@RequestParam(name = "status", required = true) StatusEnum status,
//			@PathVariable("companyId") String companyId
	) {
		MDC.put(REQUEST_ID_HEADER, requestId);
		long startTime = System.currentTimeMillis();

		logger.info("Start execute method getCustomerids");
		CommonResponse commonResponse = new CommonResponse();
		UserCommonResponse userCommonResponse ;
		UserResponse userResponse = new UserResponse();
		List<UserCommonResponse> userCommonResponseList =  new ArrayList<>();
		if (StringUtils.isEmpty(userId)) {
			logger.error(invalidUserLoggingMsg, userId);
			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
							LocaleContextHolder.getLocale()),
					ErrorCode.INVALID_USER_ID);
		} else {
			try {
//				logger.debug("Fetch Customerids by CompanyId: {}", companyId);
				if (StringUtils.isEmpty(userGroup)) {
					userGroup = COMMON_USER_GROUP;
				}
				List<UserMst> allUsers = userService.getUseres(userId, userGroup, requestId);
				CompanyUserResponse companyUserResponse;
				UserFeaturesResponse userFeaturesResponse;

				for (int i=0; allUsers.size()>i;i++){
					userCommonResponse = new UserCommonResponse();
					List<CompanyUser> companyUsers = allUsers.get(i).getCompanyUsers();

					List<CompanyUserResponse> companyUserResponseList = new ArrayList<>();
					for (int y=0; companyUsers.size()>y;y++){
						companyUserResponse = new CompanyUserResponse();
						List<UserFeatures> userFeatures = companyUsers.get(y).getUserFeatures();

						List<UserFeaturesResponse> userFeaturesResponseList = new ArrayList<>();
						for (int z=0; userFeatures.size()>z;z++){
							userFeaturesResponse = new UserFeaturesResponse();
							userFeaturesResponse.setId(userFeatures.get(z).getId());
							userFeaturesResponse.setCreatedBy(userFeatures.get(z).getCreatedBy());
							userFeaturesResponse.setCreatedDate(userFeatures.get(z).getCreatedDate());
							userFeaturesResponse.setLastUpdatedBy(userFeatures.get(z).getLastUpdatedBy());
							userFeaturesResponse.setLastUpdatedDate(userFeatures.get(z).getLastUpdatedDate());
							userFeaturesResponse.setLastVerifiedBy(userFeatures.get(z).getLastVerifiedBy());
							userFeaturesResponse.setLastVerifiedDate(userFeatures.get(z).getLastVerifiedDate());
							userFeaturesResponse.setUserGroup(userFeatures.get(z).getUserGroup());
							userFeaturesResponseList.add(z, userFeaturesResponse);
						}
						companyUserResponse.setId(companyUsers.get(y).getId());
						companyUserResponse.setStatus(companyUsers.get(y).getStatus());
						companyUserResponse.setRecordStatus(companyUsers.get(y).getRecordStatus());
						companyUserResponse.setUser(companyUsers.get(y).getUser());
						companyUserResponse.setRole(companyUsers.get(y).getRole());
						companyUserResponse.setIsPrimeryUser(companyUsers.get(y).getIsPrimeryUser());
						companyUserResponse.setRemark(companyUsers.get(y).getRemark());
						companyUserResponse.setCreatedBy(companyUsers.get(y).getCreatedBy());
						companyUserResponse.setCreatedDate(companyUsers.get(y).getCreatedDate());
						companyUserResponse.setLastUpdatedBy(companyUsers.get(y).getLastUpdatedBy());
						companyUserResponse.setLastUpdatedDate(companyUsers.get(y).getLastUpdatedDate());
						companyUserResponse.setLastVerifiedBy(companyUsers.get(y).getLastVerifiedBy());
						companyUserResponse.setLastVerifiedDate(companyUsers.get(y).getLastVerifiedDate());
						companyUserResponse.setUserFeatures(userFeaturesResponseList);
						companyUserResponseList.add(y,companyUserResponse);


					}
					userCommonResponse.setId(allUsers.get(i).getId());
					userCommonResponse.setUserName(allUsers.get(i).getUserName());
					userCommonResponse.setBranch(allUsers.get(i).getBranch());
					userCommonResponse.setRemark(allUsers.get(i).getRemark());
					userCommonResponse.setRecordStatus(allUsers.get(i).getRecordStatus());
					userCommonResponse.setStatus(allUsers.get(i).getStatus());
					userCommonResponse.setCreatedBy(allUsers.get(i).getCreatedBy());
					userCommonResponse.setCreatedDate(allUsers.get(i).getCreatedDate());
					userCommonResponse.setLastUpdatedBy(allUsers.get(i).getLastUpdatedBy());
					userCommonResponse.setLastUpdatedDate(allUsers.get(i).getLastUpdatedDate());
					userCommonResponse.setLastVerifiedBy(allUsers.get(i).getLastVerifiedBy());
					userCommonResponse.setLastVerifiedDate(allUsers.get(i).getLastVerifiedDate());
					userCommonResponse.setCompanyUsers(companyUserResponseList);
					userCommonResponseList.add(i,userCommonResponse);

				}

//				userCommonResponseList.set(userCommonResponse)
				userResponse.setUserResponseList(Collections.singletonList(userCommonResponseList));
				userResponse.setReturnCode(HttpStatus.OK.value());
				userResponse.setReturnMessage("Successfully retrieved the User");
				commonResponse.setReturnCode(200);
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
		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(userResponse);
	}
//
//	@ApiOperation(value = "Get Auth pending User List", nickname = "Get Auth pending User List", notes = "Get Auth pending User List.", httpMethod = "GET")
//	@ApiResponses(value = {
//			@ApiResponse(code = 200, message = "Fetching Auth pending User list successful", response = CommonGetListResponse.class),
//			@ApiResponse(code = 400, message = "Get Auth pending Userlist fail", response = CommonResponse.class),
//			@ApiResponse(code = 500, message = "Internal server error", response = CommonResponse.class) })
//	@GetMapping("/v1/user/pending")
//	public ResponseEntity<CommonResponse> getPendingAuthUseres(
//			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
//			@RequestHeader(name = "userId", required = true) String userId,
//			@RequestHeader(name = "userGroup", required = false) String userGroup) {
//		MDC.put(REQUEST_ID_HEADER, requestId);
//		long startTime = System.currentTimeMillis();
//
//		logger.info("Start exicute method getPendingAuthUseres");
//		CommonResponse commonResponse;
//		if (StringUtils.isEmpty(userId)) {
//			logger.error(invalidUserLoggingMsg, userId);
//			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
//					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
//							LocaleContextHolder.getLocale()),
//					ErrorCode.INVALID_USER_ID);
//		} else {
//			try {
//				logger.debug("Fetch Pending auth Customerids");
//				if (StringUtils.isEmpty(userGroup)) {
//					userGroup = COMMON_USER_GROUP;
//				}
//				commonResponse = userService.getPendingAuthUseres(userId, userGroup, requestId);
//				logger.info(commonResponse.getReturnMessage());
//			} catch (SystemException e) {
//				logger.error("Error occured while getPendingAuthUseres for {}.", e);
//				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
//						e.getErrorCode());
//			} catch (Exception e) {
//				logger.error("Error occured while getPendingAuthUseres for {}.", e);
//				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
//						ErrorCode.UNKNOWN_ERROR);
//			}
//
//		}
//		long endTime = System.currentTimeMillis();
//		logger.info("getPendingAuthUseres rate: avg_resp={}", (endTime - startTime));
//		MDC.clear();
//		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
//	}
//
//	@ApiOperation(value = "Delete User API", nickname = "Delete User API", notes = "Delete User API", httpMethod = "DELETE")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted", response = CommonResponse.class),
//			@ApiResponse(code = 404, message = "Resource not found"),
//			@ApiResponse(code = 400, message = "Input parameters are not valid"),
//			@ApiResponse(code = 500, message = "Internal server error") })
//	@DeleteMapping(value = "/v1/user")
//	public ResponseEntity<CommonResponse> deletUser(
//			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
//			@RequestHeader(name = "userId", required = true) String userId,
//			@RequestHeader(name = "userGroup", required = false) String userGroup,
//			@RequestBody DeleteUserRequest deleteUserRequest) {
//		MDC.put(REQUEST_ID_HEADER, requestId);
//		long startTime = System.currentTimeMillis();
//
//		logger.info("Start exicute method deletUser");
//		CommonResponse commonResponse;
//		if (StringUtils.isEmpty(userId)) {
//			logger.error(invalidUserLoggingMsg, userId);
//			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
//					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
//							LocaleContextHolder.getLocale()),
//					ErrorCode.INVALID_USER_ID);
//		} else {
//			try {
//				logger.debug("Delete User : {} of Company : {}", deleteUserRequest.getUserId(),
//						deleteUserRequest.getCompanyId());
//				if (StringUtils.isEmpty(userGroup)) {
//					userGroup = COMMON_USER_GROUP;
//				}
//				commonResponse = userService.deleteUser(deleteUserRequest, userId, userGroup, requestId);
//				logger.info(commonResponse.getReturnMessage());
//			} catch (SystemException e) {
//				logger.error("Error occured while deletCustomerById for {}.", e);
//				commonResponse = new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(),
//						e.getErrorCode());
//			} catch (Exception e) {
//				logger.error("Error occured while deletCustomerById for {}.", e);
//				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//						messageSource.getMessage(ErrorCode.UNKNOWN_ERROR, null, LocaleContextHolder.getLocale())
//								+ e.getMessage(),
//						ErrorCode.UNKNOWN_ERROR);
//			}
//
//		}
//		long endTime = System.currentTimeMillis();
//		logger.info("deletCustomerById rate: avg_resp={}", (endTime - startTime));
//		MDC.clear();
//		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
//	}
//
//	@ApiOperation(value = "Update User request", nickname = "Update User", notes = "Update User Request.", httpMethod = "PUT")
//	@ApiResponses(value = {
//			@ApiResponse(code = 200, message = "User updated successfully", response = CommonResponse.class),
//			@ApiResponse(code = 404, message = "Resource not found"),
//			@ApiResponse(code = 400, message = "Input parameters are not valid"),
//			@ApiResponse(code = 500, message = "Internal server error") })
//	@PutMapping(value = "/v1/user/{companyId}/{userId}")
//	public ResponseEntity<CommonResponse> updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest,
//			@RequestHeader(name = REQUEST_ID_HEADER, required = true) String requestId,
//			@RequestHeader(name = "userId", required = true) String userId,
//			@RequestHeader(name = "userGroup", required = false) String userGroup,
//			@PathVariable("companyId") String companyId, @PathVariable("userId") String userId) {
//		MDC.put(REQUEST_ID_HEADER, requestId);
//		long startTime = System.currentTimeMillis();
//
//		logger.info("Start exicute method updateUser");
//		CommonResponse commonResponse;
//		if (StringUtils.isEmpty(userId)) {
//			logger.error(invalidUserLoggingMsg, userId);
//			commonResponse = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
//					messageSource.getMessage(String.valueOf(ErrorCode.INVALID_USER_ID), new Object[] { userId },
//							LocaleContextHolder.getLocale()),
//					ErrorCode.INVALID_USER_ID);
//		} else {
//			try {
//				logger.debug("Update User (CompId: {}, UserId: {}).", companyId, userId);
//				if (StringUtils.isEmpty(userGroup)) {
//					userGroup = COMMON_USER_GROUP;
//				}
//				updateUserRequest.setCompanyId(companyId);
//				updateUserRequest.setUserId(userId);
//				commonResponse = userService.updateTempCompany(updateUserRequest, userId, userGroup, requestId);
//				logger.info(commonResponse.getReturnMessage());
//			} catch (SystemException e) {
//				logger.info("Error occured while updateUser for {}.", e.getMessage());
//				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
//						e.getErrorCode());
//			} catch (RecordNotFoundException e) {
//				logger.info("RecordNotFoundException occured while updateUser for {}.", e.getMessage());
//				commonResponse = new CommonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), e.getErrorCode());
//			} catch (Exception e) {
//				logger.error("Unknown Error occured while updateUser for {}.", e);
//				commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
//						ErrorCode.UNKNOWN_ERROR);
//			}
//
//		}
//		long endTime = System.currentTimeMillis();
//		logger.info("updateUser rate: avg_resp={}", (endTime - startTime));
//		MDC.clear();
//		return ResponseEntity.status(HttpStatus.resolve(commonResponse.getReturnCode())).body(commonResponse);
//	}
}
