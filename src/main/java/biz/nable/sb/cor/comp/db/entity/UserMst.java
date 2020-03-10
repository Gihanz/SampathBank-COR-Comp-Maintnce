package biz.nable.sb.cor.comp.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class UserMst implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MST_SEQ")
	@SequenceGenerator(name = "USER_MST_SEQ", sequenceName = "SB_COR_USER_MST_SEQ", allocationSize = 1)
	private Long id;
	private String userName;

	@OneToMany(mappedBy = "user")
	@JsonManagedReference
	List<CompanyUser> companyUsers;
}
