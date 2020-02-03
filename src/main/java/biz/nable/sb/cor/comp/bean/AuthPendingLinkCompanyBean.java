package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthPendingLinkCompanyBean {
	private String companyId;
	private String companyName;
	private String linkedCompanyID;
	private ActionTypeEnum actionType;
	private StatusEnum status;
	private Long authorizationId;
	private String signature;
}
