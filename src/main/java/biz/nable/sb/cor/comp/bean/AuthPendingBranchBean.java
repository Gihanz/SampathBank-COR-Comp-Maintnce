package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthPendingBranchBean {
	private String companyId;
	private String companyName;
	private String BranchId;
	private String BranchName;
	private ActionTypeEnum actionType;
	private StatusEnum status;
	private Long authorizationId;
	private String signature;
}
