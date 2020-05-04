package biz.nable.sb.cor.comp.db.entity;

import java.util.Date;

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
@Table(name = "SB_COR_COMPANY_CUMM_DATA")
public class CompanyCummData extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_CUMM_DATA_SEQ")
	@SequenceGenerator(name = "COMPANY_CUMM_DATA_SEQ", sequenceName = "SB_COR_COMPANY_CUMM_DATA_SEQ", allocationSize = 1)
	private Long id;
	private String parentCompanyId;
	private String customerId;

	private String lastVerifiedBy;
	private Date lastVerifiedDate;
	private StatusEnum status = StatusEnum.ACTIVE;
	private RecordStatusEnum recordStatus = RecordStatusEnum.CREATE_PENDING;
}
