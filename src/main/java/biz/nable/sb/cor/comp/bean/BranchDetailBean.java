package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BranchDetailBean extends BranchBean {
	private String companyName;
	private String requestId;
	private StatusEnum status;
}
