package biz.nable.sb.cor.comp.db.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Data;

@Entity
@Data
public class CompanyUser {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_USER_MST_SEQ")
	@SequenceGenerator(name = "COMPANY_USER_MST_SEQ", sequenceName = "SB_COR_COMPANY_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user", nullable = false)
	private UserMst user;
	private StatusEnum statusEnum;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company", nullable = false)
	private CompanyMst company;
}
