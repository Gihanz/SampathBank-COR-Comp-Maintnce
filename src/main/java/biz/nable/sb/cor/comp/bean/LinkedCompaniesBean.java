package biz.nable.sb.cor.comp.bean;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkedCompaniesBean {

    private String companyId;
    private String companyName;
    private String allAccountAccessFlag;
    private UserCompanyFeaturesBean userCompanyFeaturesBean;
    private UserCompanyAccountsBean userCompanyAccountsBean;
    private UserCompanyWorkflowGroupsBean userCompanyWorkflowGroupsBean;
}
