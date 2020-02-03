package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanyResponseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCompanyByIdResponse extends CommonResponse {
	CompanyResponseBean companyBean;
}
