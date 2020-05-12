package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.utility.YnFlagEnum;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_COMPANY_MST")
public class CompanyMst extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_MST_SEQ")
	@SequenceGenerator(name = "COMPANY_MST_SEQ", sequenceName = "SB_COR_COMPANY_MST_SEQ", allocationSize = 1)
	private Long id;
	@Column(length = 9)
	private String companyId;
	@Column(length = 9)
	private Long parentCompanyId;
	@Column(length = 80)
	private String companyName;
	private String contactNo;
	@Column(length = 80)
	private String emailAddr;
	@Column(length = 50)
	private String contactPerson;
	@Column(length = 10)
	private String epfCode;
	@Column(length = 10)
	private String etfCode;
	@Column(length = 10)
	private String epfAreaCode;
	@Column(length = 15)
	private String commTemplateId;
	private YnFlagEnum mcAuthFlg;
	@Column(length = 3)
	private String canvassedBranch;
	private Long canvassedUser;
	@Column(length = 15)
	private String treasureCustRef;
	private String mobileCashAuthorization;
	private String webServiceActivationFlag;
	private String bulkDirectDebitFlg;
	private Double bulkPaymentLimit;
	@Column(length = 15)
	private String wsIp;
	@Column(length = 15)
	private String requestId;
	@Column(length = 3)
	private String districtCode;
	private StatusEnum status = StatusEnum.ACTIVE;
	private RecordStatusEnum recordStatus = RecordStatusEnum.CREATE_PENDING;

	@OneToMany(mappedBy = "company")
	@JsonManagedReference
	private List<BranchMst> branchMsts;

	@OneToMany(mappedBy = "company")
	@JsonManagedReference
	private List<CompanyUser> companyUsers;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CompanyFeatures> companyFeatures = new ArrayList<>();

	@OneToMany(mappedBy = "companyId", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private List<CompanyAccountMst> companyAccounts = new ArrayList<>();

	@OneToMany(mappedBy = "companyMst", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private Set<UserLinkedCompany> userLinkedCompanies;

	private Long corporatePaymentsLimit;
	private String deviceLocation;
	private Long authorizationId;
}
