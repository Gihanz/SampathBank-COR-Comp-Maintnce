package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.comp.bean.*;
import lombok.*;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLinkRequest extends Auditable implements CommonTempBean {

    private Long userLinkId;
    private String companyId;
    private String allAccountAccessFlag;
    private Set<UserCompanyAccountsBean> userCompanyAccounts;
    private Set<UserCompanyFeaturesBean> userCompanyFeatures;
    private Set<UserCompanyWorkflowGroupsBean> userCompanyWorkflowGroups;
}
