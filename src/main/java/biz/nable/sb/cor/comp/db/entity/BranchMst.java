package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonBackReference;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_COMPANY_BRANCH")
public class BranchMst extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_BRANCH_SEQ")
	@SequenceGenerator(name = "COMPANY_BRANCH_SEQ", sequenceName = "SB_COR_COMPANY_BRANCH_SEQ", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company_id", nullable = false)
	@JsonBackReference
	private CompanyMst company;
	@Column(length = 6)
	private String branchId;
	@Column(length = 60)
	private String branchName;

	private StatusEnum status = StatusEnum.ACTIVE;
	private RecordStatusEnum recordStatus = RecordStatusEnum.ACTIVE;
}
