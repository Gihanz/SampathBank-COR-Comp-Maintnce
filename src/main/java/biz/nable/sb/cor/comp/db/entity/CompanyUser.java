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

import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Data;

@Entity
@Data
public class CompanyUser implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_USER_MST_SEQ")
	@SequenceGenerator(name = "COMPANY_USER_MST_SEQ", sequenceName = "SB_COR_COMPANY_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user", nullable = false)
	@JsonBackReference
	private UserMst user;
	private StatusEnum statusEnum;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company", nullable = false)
	@JsonBackReference
	private CompanyMst company;
}
