package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.db.entity.UserMst;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import biz.nable.sb.cor.comp.utility.UserRoleEnum;
import biz.nable.sb.cor.comp.utility.YnFlagEnum;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class CompanyUserResponse {

    private Long id;
    private UserMst user;
    private StatusEnum status;
    private RecordStatusEnum recordStatus;
    private UserRoleEnum role;
    private YnFlagEnum isPrimeryUser;
    private String remark;
    private  String createdBy;
    private Date createdDate;
    private String lastUpdatedBy;
    private  Date lastUpdatedDate;
    private  Date lastVerifiedDate;
    private  String lastVerifiedBy;
    private  String userGroup;
    private List<UserFeaturesResponse> userFeatures;
}
