package biz.nable.sb.cor.comp.bean;

import java.util.Date;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyTempBean extends CompanyBean implements CommonTempBean {
	private String companyId;
	private ActionTypeEnum actionType;
	private String requestedBy;
	private Date requestedDate;
	private Long tempId;
}
