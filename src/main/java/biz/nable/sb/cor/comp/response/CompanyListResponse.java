package biz.nable.sb.cor.comp.response;

import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyListResponse extends CommonResponse {
	private List<CompanyResponse> companyResponseList;

	public CompanyListResponse(int returnCode, String returnMessage, String errorCode) {
		super(returnCode, returnMessage, errorCode);
	}
}
