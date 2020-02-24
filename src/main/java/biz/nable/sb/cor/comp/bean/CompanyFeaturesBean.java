package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyFeaturesBean {
	
	private Long id;
	private String companyId;
	private String featureId;

}
