package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.ModifiedUserLinkBean;
import biz.nable.sb.cor.comp.bean.OriginalUserLinkBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLinkListByApprovalIDResponse extends CommonResponse {

    private String userId;
    private String approvalId;
    private String signature;
    private String approvalStatus;
    private OriginalUserLinkBean originalUserLinkBean;
    private ModifiedUserLinkBean modifiedUserLinkBean;
}
