package biz.nable.sb.cor.comp.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_USER_PRIMARY_FEATURE")
public class UserPrimaryFeature implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PRI_FEA_SEQ")
    @SequenceGenerator(name = "USER_PRI_FEA_SEQ", sequenceName = "SB_COR_USER_PRI_FEA", allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference
    private UserMst userMstFea;

    private Long feature;
}
