package biz.nable.sb.cor.comp.db.entity;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_USER_LINKED_COMPANY")
public class UserLinkedCompany extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_LINK_SEQ")
    @SequenceGenerator(name = "USER_LINK_SEQ", sequenceName = "SB_COR_USER_LINKED", allocationSize = 1)
    private Long linkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference
    private UserMst userMst;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "companyId", nullable = false)
    @JsonBackReference
    private CompanyMst companyMst;

    private String allAcctAccessFlg;
    private RecordStatuUsersEnum recordStatus;

    @OneToMany(mappedBy = "userLinkedCompanyAccount", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<UserCompanyAccount> userCompanyAccounts;

    @OneToMany(mappedBy = "userLinkedCompanyFeature", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<UserCompanyFeature> userCompanyFeatures;

}
