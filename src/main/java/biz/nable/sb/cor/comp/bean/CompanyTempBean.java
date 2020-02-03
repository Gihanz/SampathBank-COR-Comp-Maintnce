package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyTempBean extends CompanyBean implements CommonTempBean {
	private String companyId;
	private Long tempId;
}
