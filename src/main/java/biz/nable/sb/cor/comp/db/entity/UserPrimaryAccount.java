package biz.nable.sb.cor.comp.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(name = "SB_COR_USER_PRIMARY_ACCOUNT")
public class UserPrimaryAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PRI_ACC_SEQ")
    @SequenceGenerator(name = "USER_PRI_ACC_SEQ", sequenceName = "SB_COR_USER_PRI_ACC", allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference
    private UserMst userMstAcc;

    @Column(length = 16)
    private String accountNo;
}
