package biz.nable.sb.cor.comp.db.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_COMPANY_FEATURES")
public class CompanyFeatures {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_FET_SEQ")
	@SequenceGenerator(name = "COMPANY_FET_SEQ", sequenceName = "SB_COR_COMPANY_FEA", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "company", nullable = false)
	private CompanyMst company;
	private Long feature;
	private String featureDescription;
}
