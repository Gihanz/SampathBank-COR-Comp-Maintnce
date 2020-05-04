package biz.nable.sb.cor.comp.component;

import biz.nable.sb.cor.common.annotation.Approve;
import biz.nable.sb.cor.common.annotation.AuthPending;
import biz.nable.sb.cor.common.annotation.FindTempRecord;
import biz.nable.sb.cor.common.annotation.TempRecord;
import biz.nable.sb.cor.common.bean.*;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserLinkCompanyTempComponent {

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


    @AuthPending
    public CommonSearchBean getAuthPendingRecord(CommonSearchBean commonSearchBean) {
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
