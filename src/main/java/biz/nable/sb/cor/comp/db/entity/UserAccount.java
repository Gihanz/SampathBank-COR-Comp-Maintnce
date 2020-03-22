package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import biz.nable.sb.cor.common.db.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class UserAccount extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_FET_SEQ")
	@SequenceGenerator(name = "USER_FET_SEQ", sequenceName = "SB_COR_USER_FEA", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "companyUser", nullable = false)
	@JsonBackReference
	private CompanyUser companyUser;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "companyAccount", nullable = false)
	@JsonBackReference
	private CompanyAccountMst companyAccount;
}
