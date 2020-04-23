package biz.nable.sb.cor.comp.bean;

import lombok.*;

import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkedCompaniesBean {

    private String companyId;
    private String companyName;
    private String allAccountAccessFlag;
    private Set<UserCompanyFeaturesBean> userCompanyFeaturesBean;
    private Set<UserCompanyAccountsBean> userCompanyAccountsBean;
    private Set<UserCompanyWorkflowGroupsBean> userCompanyWorkflowGroupsBean;
}
