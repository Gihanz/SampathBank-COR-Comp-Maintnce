package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCompanyByIdResponse extends CommonResponse {
	CompanyResponse companyBean;
}
