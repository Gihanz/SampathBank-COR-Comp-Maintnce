package biz.nable.sb.cor.comp.db.entity;


import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.common.utility.ApprovalStatus;
import biz.nable.sb.cor.common.utility.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "SB_COR_USER_MST_HISTORY")
public class UserMstHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MST_HIS")
    @SequenceGenerator(name = "USER_MST_HIS", sequenceName = "SB_COR_USER_MST_HISTORY_SEQ", allocationSize = 1)
    private Long id;
    private String referenceNo;
    private String approvalId;
    private String reason;
    private String hashTags;
    private ApprovalStatus status;
    private String requestType;
    private ActionTypeEnum actionType;
    @Convert(converter = HashMapConverter.class)
    @Column(length = 4000)
    private Map<String, Object> requestPayload;
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



