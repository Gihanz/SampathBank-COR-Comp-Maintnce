package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.comp.utility.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMst extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MST_SEQ")
	@SequenceGenerator(name = "USER_MST_SEQ", sequenceName = "SB_COR_USER_MST_SEQ", allocationSize = 1)
	private Long userId;
	private String userName;
	private String designation;
	private Long branch;
	private StatusUserEnum status;
	private RecordStatuUsersEnum recordStatus;
	private UserType userType;
	private CreateState iamCreateState;
	private String companyId;
	private String allAcctAccessFlg;
	private String email;
	private Long approvalId;

	@OneToMany(mappedBy = "userMst", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private Set<UserLinkedCompany> userLinkedCompanies;

	@OneToMany(mappedBy = "userMstAcc", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private Set<UserPrimaryAccount> userPrimaryAccounts;

	@OneToMany(mappedBy = "userMstFea", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private Set<UserPrimaryFeature> userPrimaryFeatures;
}
