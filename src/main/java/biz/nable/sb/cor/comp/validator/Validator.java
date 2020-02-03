/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp.validator;

import org.springframework.util.StringUtils;

import biz.nable.sb.cor.comp.request.CreateCompanyRequest;
import biz.nable.sb.cor.comp.request.UpdateCompanyRequest;

/*
 * @Description	:This validator class is to validate Approval related parameters.
 */
public class Validator {

	private Validator() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean createCompanyValidateRequest(CreateCompanyRequest createCompanyRequest) {
		boolean isSuccess = false;

		if (!StringUtils.isEmpty(createCompanyRequest.getCompanyName())
				&& null != createCompanyRequest.getCompanyName()) {
			isSuccess = true;
		}
		return isSuccess;
	}

	public static boolean updateCompanyValidateRequest(UpdateCompanyRequest updateCompanyRequest) {
		boolean isSuccess = false;
		/*
		 * if (!StringUtils.isEmpty(updateCompanyRequest.getCompanyName()) && null !=
		 * updateCompanyRequest.getCompanyName()) { isSuccess = true; }
		 */
		return isSuccess;
	}
}
