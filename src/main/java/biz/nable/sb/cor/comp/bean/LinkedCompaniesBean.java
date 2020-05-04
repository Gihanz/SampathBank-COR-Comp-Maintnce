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
    private Set<UserCompanyAccountsBean> userCompanyAccounts;
    private Set<UserCompanyFeaturesBean> userCompanyFeatures;
    private Set<UserCompanyWorkflowGroupsBean> userCompanyWorkflowGroups;
}
