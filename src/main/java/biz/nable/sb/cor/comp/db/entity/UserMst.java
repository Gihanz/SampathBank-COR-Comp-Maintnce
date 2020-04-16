package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

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
public class UserMst implements Serializable {

	private static final long serialVersionUID = 1L;
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MST_SEQ")
	@SequenceGenerator(name = "USER_MST_SEQ", sequenceName = "SB_COR_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	@Id
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
