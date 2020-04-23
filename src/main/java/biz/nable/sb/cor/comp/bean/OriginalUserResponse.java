package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.comp.db.entity.UserPrimaryAccount;
import biz.nable.sb.cor.comp.db.entity.UserPrimaryFeature;
import biz.nable.sb.cor.comp.utility.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OriginalUserResponse {

//    private String userId;
    private String userName;
    private long branchCode;
    private String designation;
    private String email;
    private UserType userType;
    private String allAccountAccessFlag;
    private String status;
    private Set<UserAccountsBean> userAccounts;
    private Set<UserFeaturesBean> userFeatures;
    private Set<UserWorkFlowGroupsBean> userWorkFlowGroupBeans;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date createDate;
    @JsonIgnore
    private String lastModifiedBy;
    @JsonIgnore
    private Date lastModifiedDate;
    @JsonIgnore
    private String lastVerifiedBy;
    @JsonIgnore
    private Date lastVerifiedDate;
}
