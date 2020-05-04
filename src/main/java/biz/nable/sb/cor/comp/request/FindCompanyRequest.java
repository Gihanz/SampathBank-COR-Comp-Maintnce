package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindCompanyRequest {
	private String referenceNo;
	private String hashTags;
	private String tempId;
	private String requestType;
	ActionTypeEnum actionType;

}
