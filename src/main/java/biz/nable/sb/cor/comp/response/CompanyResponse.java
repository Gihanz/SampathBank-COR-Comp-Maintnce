package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.bean.CompanyBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse extends CompanyBean {
	private String companyId;
	private StatusEnum status;
}
