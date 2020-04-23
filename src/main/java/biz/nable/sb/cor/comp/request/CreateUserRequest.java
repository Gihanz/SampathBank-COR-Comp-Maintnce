package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.comp.bean.UserAccountsBean;
import biz.nable.sb.cor.comp.bean.UserFeaturesBean;
import biz.nable.sb.cor.comp.bean.UserWorkFlowGroupsBean;
import biz.nable.sb.cor.comp.utility.CreateState;
import biz.nable.sb.cor.comp.utility.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest implements CommonTempBean {


    private long userId;
    private String userName;
    private long branchCode;
    private String designation;
    private String email;
    private UserType userType;
    private String primaryCompanyId;
    private String allAccountAccessFlag;
//    private CreateState iamCreateState;
    private Set<UserAccountsBean> userAccountBeans;
    private Set<UserFeaturesBean> userFeatureBeans;
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
    @JsonIgnore
    private String userGroup;
}
