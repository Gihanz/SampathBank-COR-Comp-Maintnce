package biz.nable.sb.cor.comp.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(name = "SB_COR_USER_COMPANY_ACCOUNT")
public class UserCompanyAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_COM_ACC_SEQ")
    @SequenceGenerator(name = "USER_COM_ACC_SEQ", sequenceName = "SB_COR_USER_COM_ACC", allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linkId", nullable = false)
    @JsonBackReference
    private UserLinkedCompany userLinkedCompanyAccount;

    @Column(length = 16)
    private String accountNo;

}
