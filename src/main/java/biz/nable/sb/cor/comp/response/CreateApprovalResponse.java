package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.bean.ApprovalBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateApprovalResponse extends CommonResponse {
	private ApprovalBean approvalBean;

	public CreateApprovalResponse(int returnCode, String returnMessage, String errorCode, ApprovalBean approvalBean) {
		super(returnCode, returnMessage, errorCode);
		this.approvalBean = approvalBean;
	}

}
