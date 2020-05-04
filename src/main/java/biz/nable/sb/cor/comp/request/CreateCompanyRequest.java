package biz.nable.sb.cor.comp.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.comp.bean.CompanyBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCompanyRequest extends CompanyBean implements CommonTempBean {
	@NotEmpty(message = "companyId should not be empty")
	@NotNull(message = "companyId should not be null")
	private String companyId;
}
