package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanySummeryBean implements CommonTempBean {
	private String companyId;
	private String companyName;
	private String status;

}
