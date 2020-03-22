package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
public class UserMst extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MST_SEQ")
	@SequenceGenerator(name = "USER_MST_SEQ", sequenceName = "SB_COR_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	private String userName;
	private String designation;
	private Long branch;
	@Length(max = 1000)
	private String remark;
	private StatusEnum status;
	private RecordStatusEnum recordStatus;

	@OneToMany(mappedBy = "user")
	@JsonManagedReference
	private List<CompanyUser> companyUsers;

}
