package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import biz.nable.sb.cor.comp.utility.UserRoleEnum;
import biz.nable.sb.cor.comp.utility.YnFlagEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class CompanyUser extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_USER_MST_SEQ")
	@SequenceGenerator(name = "COMPANY_USER_MST_SEQ", sequenceName = "SB_COR_COMPANY_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user", nullable = false)
	@JsonBackReference
	private UserMst user;
	private StatusEnum status;
	private RecordStatusEnum recordStatus;
	private UserRoleEnum role;
	private YnFlagEnum isPrimeryUser;
	@Length(max = 1000)
	private String remark;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company", nullable = false)
	@JsonBackReference
	private CompanyMst company;

	@OneToMany(mappedBy = "companyUser")
	@JsonManagedReference
	private List<UserFeatures> userFeatures;
}
