package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.comp.bean.UserAccountsBean;
import biz.nable.sb.cor.comp.bean.UserFeaturesBean;
import biz.nable.sb.cor.comp.bean.UserWorkFlowGroupsBean;
import lombok.*;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLinkRequest extends Auditable implements CommonTempBean {

    private String companyId;
    private String allAccountAccessFlag;
    private Set<UserAccountsBean> userAccountBeans;
    private Set<UserFeaturesBean> userFeatureBeans;
    private Set<UserWorkFlowGroupsBean> userWorkFlowGroupBeans;
}
