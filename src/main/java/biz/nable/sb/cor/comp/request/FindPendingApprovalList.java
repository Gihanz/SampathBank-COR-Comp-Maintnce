package biz.nable.sb.cor.comp.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindPendingApprovalList {
	private String referenceNo;
	private String hashTags;
	private String tempId;
	private String requestType;

}
