package biz.nable.sb.cor.comp.response;

import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanyTempBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTempCompanyResponse extends CommonResponse {
	private List<CompanyTempBean> commonTempBeans;
}
