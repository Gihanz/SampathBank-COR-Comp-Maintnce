package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class UserFeaturesResponse  {

    private Long id;
    private  String createdBy;
    private Date createdDate;
    private String lastUpdatedBy;
    private  Date lastUpdatedDate;
    private  Date lastVerifiedDate;
    private  String lastVerifiedBy;
    private  String userGroup;
}
