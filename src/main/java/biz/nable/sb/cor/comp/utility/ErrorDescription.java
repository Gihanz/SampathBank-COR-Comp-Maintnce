package biz.nable.sb.cor.comp.utility;

public class ErrorDescription {

    private ErrorDescription() {
        throw new IllegalStateException("Error Code class");
    }

    public static final String INVALID_USER_LOGGING_MASSAGE = "User Id is invalid: {}";

    public static final String REQUEST_SUCCESS = "User Request successful";
    public static final String REQUEST_UPDATE_SUCCESS = "User updated successfully";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String INPUT_PARAMETERS_NOT_VALID = "Input parameters are not valid";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String USER_RECORD_ALREADY_EXISTS = "User recode already exists";
    public static final String USER_RECORD_ALREADY_LINKED = "User recode already linked";
    public static final String NO_USER_LINK_COMPANY_RECORD = "No user link company recoded found.";
    public static final String SUCCESS = "SUCCESS";
}
