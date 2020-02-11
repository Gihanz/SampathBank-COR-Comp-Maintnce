package biz.nable.sb.cor.comp.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import biz.nable.sb.cor.common.db.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_COMPANY_ACCOUNTS")
public class CompanyAccountMst extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_ACC_SEQ")
	@SequenceGenerator(name = "COMPANY_ACC_SEQ", sequenceName = "SB_COR_COMPANY_ACC_SEQ", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company_id", nullable = false)
	private CompanyMst companyId;
	@Column(length = 16)
	private String accountNo;
	@Column(length = 80)
	private String accountName;
	private Double accBalance;
	@Column(length = 3)
	private String jointSerial;
	@Column(length = 1)
	private String jointRecType;
	@Column(length = 80)
	private String jointHolderName;
	@Column(length = 1)
	private String jntDelFlag;
	@Column(length = 1)
	private String schemaType;
	@Column(length = 6)
	private String schemaCode;
	@Column(length = 1)
	private String acctClsFlag;
	@Column(length = 1)
	private String delFlag;
	private Double clearBalAmt;
	@Column(length = 6)
	private String operMode;
	@Column(length = 3)
	private String acctCurr;
	@Column(length = 3)
	private String acctSolId;

	private Date acctOpenDate;
}
