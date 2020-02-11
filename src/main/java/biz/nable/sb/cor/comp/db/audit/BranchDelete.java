package biz.nable.sb.cor.comp.db.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
@Table(name = "SB_COR_COMPANY_BRANCH_DELETE_TEST")
public class BranchDelete extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_BRANCH_DELETE_TEST_SEQ")
	@SequenceGenerator(name = "COMPANY_BRANCH_DELETE_TEST_SEQ", sequenceName = "SB_COR_COMPANY_BRANCH_DELETE_TEST_SEQ", allocationSize = 1)
	private Long id;
	private Long companyId;
	@Column(length = 6)
	private String branchId;
	@Column(length = 60)
	private String branchName;

	private StatusEnum status = StatusEnum.ACTIVE;
	private RecordStatusEnum recordStatus = RecordStatusEnum.ACTIVE;
}
