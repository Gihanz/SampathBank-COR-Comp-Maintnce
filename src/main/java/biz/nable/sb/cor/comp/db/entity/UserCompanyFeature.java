package biz.nable.sb.cor.comp.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_USER_COMPANY_FEATURE")
public class UserCompanyFeature implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_COM_FEA_SEQ")
    @SequenceGenerator(name = "USER_COM_FEA_SEQ", sequenceName = "SB_COR_USER_COM_FEA", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linkId", nullable = false)
    @JsonBackReference
    private UserLinkedCompany userLinkedCompanyFeature;

    private Long feature;
}
