package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetCompanyByIdResponse extends CommonResponse {
	CompanyResponse companyBean;

	public GetCompanyByIdResponse(int returnCode, String returnMessage, String errorCode) {
		super(returnCode, returnMessage, errorCode);
	}
}
