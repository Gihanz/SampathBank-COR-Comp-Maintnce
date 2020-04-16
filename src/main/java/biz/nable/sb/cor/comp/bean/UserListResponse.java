package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.comp.utility.*;
import lombok.*;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse  {

    private Long id;
    private String userName;
    private String designation;
    private Long branch;
    private String remark;
    private StatusUserEnum status;
    private RecordStatuUsersEnum recordStatus;
    private UserType userType;
    private CreateState iamCreateState;
    private String email;
    private String createdBy;
    private Date createdDate;
    private String lastUpdatedBy;
    private Date lastUpdatedDate;
    private Date lastVerifiedDate;
    private String lastVerifiedBy;
}
