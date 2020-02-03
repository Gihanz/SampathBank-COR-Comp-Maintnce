package biz.nable.sb.cor.comp.utility;

public class ErrorCode {

	private ErrorCode() {
		throw new IllegalStateException("Error Code class");
	}

	// Business
	public static final String VALIDATION_ERROR = "B400";
	public static final String INVALID_USER_ID = "B410";
	public static final String INVALID_REQUEST_PARAMETER = "B411";

	public static final String NO_TEMP_RECORD_FOUND = "B4041";
	public static final String NO_COMPANY_RECORD_FOUND = "B4042";
	public static final String NO_LINK_COMPANY_RECORD_FOUND = "B4043";
	public static final String NO_BRANCH_RECORD_FOUND = "B4043";

	public static final String OPARATION_SUCCESS = "B200";
	public static final String RETRIVE_COMPANY_SUCCESS = "B201";

	public static final String COMPANY_RECORD_ALREADY_EXISTS = "B4091";
	public static final String LINK_COMPANY_RECORD_ALREADY_EXISTS = "B4092";
	public static final String Branch_RECORD_ALREADY_EXISTS = "B4093";

	public static final String CUSTOMER_ID_ALREADY_LINK = "BCMT10";

	// Runtime
	public static final String UNKNOWN_ERROR = "T500";
	public static final String DATA_COPY_ERROR = "T520";
	public static final String DATE_FORMATING_ERROR = "T521";
	public static final String STRING_TO_MAP_ERROR = "T522";

	// Network
	public static final String CREATE_APPROVAL_ERROR = "T421";
	public static final String FINACLE_REQUEST_ERROR = "T422";
}
