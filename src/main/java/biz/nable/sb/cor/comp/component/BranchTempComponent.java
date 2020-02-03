package biz.nable.sb.cor.comp.component;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import biz.nable.sb.cor.common.annotation.Approve;
import biz.nable.sb.cor.common.annotation.FindTempRecord;
import biz.nable.sb.cor.common.annotation.TempRecord;
import biz.nable.sb.cor.common.bean.ApprovalBean;
import biz.nable.sb.cor.common.bean.ApprovalResponseBean;
import biz.nable.sb.cor.common.bean.CommonRequestBean;
import biz.nable.sb.cor.common.bean.CommonResponseBean;
import biz.nable.sb.cor.common.bean.CommonSearchBean;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;

@Component
public class BranchTempComponent {

	@TempRecord(actionType = ActionTypeEnum.CREATE)
	public CommonResponseBean createTempRecord(CommonRequestBean commonRequestBean, String requestId) {
		CommonResponseBean commonResponseBean = new CommonResponseBean();
		commonResponseBean.setReturnCode(HttpStatus.OK.value());
		return commonResponseBean;
	}

	@TempRecord(actionType = ActionTypeEnum.UPDATE)
	public CommonResponseBean updateTempCompany(CommonRequestBean commonRequestBean, String requestId) {
		CommonResponseBean commonResponseBean = new CommonResponseBean();
		commonResponseBean.setReturnCode(HttpStatus.OK.value());
		return commonResponseBean;
	}

	@FindTempRecord
	public CommonSearchBean getTempRecord(CommonSearchBean commonSearchBean) {
		return commonSearchBean;
	}

	@TempRecord(actionType = ActionTypeEnum.DELETE)
	public CommonResponseBean deleteBranchTemp(CommonRequestBean commonRequestBean, String requestId) {
		CommonResponseBean commonResponseBean = new CommonResponseBean();
		commonResponseBean.setReturnCode(HttpStatus.OK.value());
		return commonResponseBean;
	}

	@Approve
	public ApprovalResponseBean doApprove(ApprovalBean approvalBean) {
		return new ApprovalResponseBean();
	}
}
