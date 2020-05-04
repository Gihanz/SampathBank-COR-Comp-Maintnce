package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.LinkedCompaniesBean;
import biz.nable.sb.cor.comp.bean.PrimaryCompanyAccounts;
import biz.nable.sb.cor.comp.bean.PrimaryCompanyFeatures;
import biz.nable.sb.cor.comp.bean.PrimaryCompanyWorkflowGroups;
import biz.nable.sb.cor.comp.utility.CreateState;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import biz.nable.sb.cor.comp.utility.StatusUserEnum;
import biz.nable.sb.cor.comp.utility.UserType;
import lombok.*;

import java.util.Set;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponseByUserID extends CommonResponse {

    public UserListResponseByUserID(int returnCode, String returnMessage, String errorCode) {
        super(returnCode, returnMessage, errorCode);
    }

    private Long userId;
    private String userName;
    private String designation;
    private Long branch;
    private String remark;
    private StatusUserEnum status;
    private RecordStatuUsersEnum recordStatus;
    private UserType userType;
    private CreateState iamCreateState;
    private Set<PrimaryCompanyFeatures> primaryCompanyFeatures;
    private Set<PrimaryCompanyAccounts> primaryCompanyAccounts;
    private Set<PrimaryCompanyWorkflowGroups> primaryCompanyWorkflowGroups;
    private LinkedCompaniesBean linkedCompaniesBean;

}
