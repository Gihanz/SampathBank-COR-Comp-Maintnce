package biz.nable.sb.cor.comp.controller;


import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.request.UserLinkRequest;
import biz.nable.sb.cor.comp.response.ApprovalPendingUserLinkResponse;
import biz.nable.sb.cor.comp.response.UserLinkListByApprovalIDResponse;
import biz.nable.sb.cor.comp.service.impl.UserLinkCompanyService;
import biz.nable.sb.cor.comp.utility.ErrorDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@RestController
public class UserLinkCompanyController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String REQUEST_ID_HEADER = "requestId";
    private static final String ADMIN_USER_ID = "adminUserId";
    private static final String USER_ID = "userId";
    private static final String USER_GROUP = "userGroup";
    private static final String COMMON_USER_GROUP = "SYSTEM";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    UserLinkCompanyService userLinkCompanyService;

    @ApiOperation(value = "Create User Link Company request", nickname = "Create User Link Company", notes = "Create User Link Company.", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ErrorDescription.REQUEST_SUCCESS, response = CommonResponse.class),
            @ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
            @ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
    @PostMapping(value = "/user/{userId}/company-link")
    public ResponseEntity<CommonResponse> createUserLinkCompany(@Valid @RequestBody UserLinkRequest userLinkRequest,
                                                     @RequestHeader(name = REQUEST_ID_HEADER) String requestId,
                                                     @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
                                                     @PathVariable(name = USER_ID) String userId,
                                                     @RequestHeader(name = USER_GROUP, required = false) String userGroup) {
        MDC.put(REQUEST_ID_HEADER, requestId);
        long startTime = System.currentTimeMillis();
        logger.info("Start execute method createUserLinkCompany: UserLinkRequest: {} RequestId: {} AdminUserID: {} UserGroup: {}",
                userLinkRequest ,requestId ,adminUserId ,userGroup);
        CommonResponse commonResponse;
        try {
            if (StringUtils.isEmpty(userGroup)) {
                userGroup = COMMON_USER_GROUP;
            }
            commonResponse = userLinkCompanyService.createUserLinkTemp(userLinkRequest, userId, userGroup, requestId, adminUserId);
            logger.info(commonResponse.getReturnMessage());
        } catch (SystemException e) {
            logger.error("SystemException occurred while createUserLinkCompany for {}.", e.getMessage());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while createUser. " + e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Error occurred while createUserLinkCompany for {}.", e.toString());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while createUserLinkCompany . " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
        }

        long endTime = System.currentTimeMillis();
        logger.info("createUserLinkCompany rate: avg_resp={}", (endTime - startTime));
        MDC.clear();
        logger.info("createUserLinkCompany method Response: {}", ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
        return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);

    }

    @ApiOperation(value = "Update User Link Company request", nickname = "Update User Link Company", notes = "Update User Link Company.", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ErrorDescription.REQUEST_SUCCESS, response = CommonResponse.class),
            @ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
            @ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
    @PutMapping (value = "/user/{userId}/company-link")
    public ResponseEntity<CommonResponse> updateUserLinkCompany(@Valid @RequestBody UserLinkRequest userLinkRequest,
                                                     @RequestHeader(name = REQUEST_ID_HEADER) String requestId,
                                                     @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
                                                     @PathVariable(name = USER_ID) String userId,
                                                     @RequestHeader(name = USER_GROUP, required = false) String userGroup) {
        MDC.put(REQUEST_ID_HEADER, requestId);
        long startTime = System.currentTimeMillis();
        logger.info("Start execute method updateUserLinkCompany: CreateUserRequest: {} RequestId: {} AdminUserID: {} UserGroup: {}",
                userLinkRequest , requestId ,adminUserId ,userGroup);
        CommonResponse commonResponse;
        try {
            if (StringUtils.isEmpty(userGroup)) {
                userGroup = COMMON_USER_GROUP;
            }
            commonResponse = userLinkCompanyService.updateUserLinkTemp(userLinkRequest, userId, userGroup, requestId, adminUserId);
            logger.info(commonResponse.getReturnMessage());
        } catch (SystemException e) {
            logger.error("SystemException occurred while updateUserLinkCompany for {}.", e.getMessage());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while updateUserLinkCompany. " + e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Error occurred while updateUserLinkCompany for {}.", e.toString());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while updateUserLinkCompany. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
        }
        long endTime = System.currentTimeMillis();
        logger.info("updateUserLinkCompany rate: avg_resp={}", (endTime - startTime));
        MDC.clear();
        logger.info("updateUserLinkCompany method Response: {}", ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
        return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);
    }

    @ApiOperation(value = "Get Pending User Link Company request", nickname = "Pending User Link Company", notes = "Pending User Link Company.", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ErrorDescription.REQUEST_SUCCESS, response = ApprovalPendingUserLinkResponse.class),
            @ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
            @ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
    @GetMapping(value = "/user/company-link/requests")
    public ResponseEntity<CommonResponse> getPendingUserLinkCompany(@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
                                                                    @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
                                                                    @RequestParam(name = "approvalStatus", required = false) String approvalStatus,
                                                                    @RequestHeader(name = USER_GROUP, required = false) String userGroup) {

        MDC.put(REQUEST_ID_HEADER, requestId);
        long startTime = System.currentTimeMillis();
        logger.info("Start execute method getPendingUserLinkCompany: RequestId: {} AdminUserID: {} UserGroup: {}",
                 requestId ,adminUserId ,userGroup);
        CommonResponse commonResponse;
        try {
            if (StringUtils.isEmpty(userGroup)) {
                userGroup = COMMON_USER_GROUP;
            }
            commonResponse = userLinkCompanyService.pendingUserLinkCompany(userGroup, requestId, adminUserId, approvalStatus);
            logger.info(commonResponse.getReturnMessage());
        } catch (SystemException e) {
            logger.error("SystemException occurred while getPendingUserLinkCompany for {}.", e.getMessage());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while getPendingUserLinkCompany. " + e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Error occurred while updateUserLinkCompany for {}.", e.toString());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while getPendingUserLinkCompany. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
        }
        long endTime = System.currentTimeMillis();
        logger.info("getPendingUserLinkCompany rate: avg_resp={}", (endTime - startTime));
        MDC.clear();
        logger.info("getPendingUserLinkCompany method Response: {}", ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
        return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);

    }

    @ApiOperation(value = "Retrieves single user company link request with details", nickname = "Retrieves single user company link", notes = "Retrieves single user company link.", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ErrorDescription.REQUEST_SUCCESS, response = UserLinkListByApprovalIDResponse.class),
            @ApiResponse(code = 400, message = ErrorDescription.INPUT_PARAMETERS_NOT_VALID),
            @ApiResponse(code = 500, message = ErrorDescription.INTERNAL_SERVER_ERROR) })
    @GetMapping(value = "/user/company-link/requests/{approvalId}")
    public ResponseEntity<CommonResponse> getPendingUserLinkByApprovalID(@RequestHeader(name = REQUEST_ID_HEADER) String requestId,
                                                                         @RequestHeader(name = ADMIN_USER_ID) String adminUserId,
                                                                         @PathVariable(name = "approvalId", required = false) String approvalId,
                                                                         @RequestHeader(name = USER_GROUP, required = false) String userGroup) {

        MDC.put(REQUEST_ID_HEADER, requestId);
        long startTime = System.currentTimeMillis();
        logger.info("Start execute method getPendingUserLinkByApprovalID: RequestId: {} AdminUserID: {} UserGroup: {}",
                requestId ,adminUserId ,userGroup);
        CommonResponse commonResponse;
        try {
            if (StringUtils.isEmpty(userGroup)) {
                userGroup = COMMON_USER_GROUP;
            }
            commonResponse = userLinkCompanyService.pendingUserLinkByApprovalID(userGroup, requestId, adminUserId, approvalId);
            logger.info(commonResponse.getReturnMessage());
        } catch (SystemException e) {
            logger.error("SystemException occurred while getPendingUserLinkByApprovalID for {}.", e.getMessage());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while getPendingUserLinkCompany. " + e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Error occurred while getPendingUserLinkByApprovalID for {}.", e.toString());
            commonResponse = new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error occurred while getPendingUserLinkByApprovalID. " + e.getMessage(), ErrorCode.UNKNOWN_ERROR);
        }
        long endTime = System.currentTimeMillis();
        logger.info("getPendingUserLinkByApprovalID rate: avg_resp={}", (endTime - startTime));
        MDC.clear();
        logger.info("getPendingUserLinkByApprovalID method Response: {}", ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse));
        return ResponseEntity.status(Objects.requireNonNull(HttpStatus.resolve(commonResponse.getReturnCode()))).body(commonResponse);

    }
}
