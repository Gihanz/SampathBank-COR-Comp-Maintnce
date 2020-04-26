package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseBean extends Auditable {

    private String Id;
    private String approvalId;
    private String signature;
    private String approvalStatus;
    private String requestType;
    private String referenceNo;
    private ActionTypeEnum actionType;
    private OriginalUserResponse originalUserResponseSet;
    private ModifiedUserResponse modifiedUserResponseSet;

}
