package biz.nable.sb.cor.comp.bean;

import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
@Setter
@ToString
public class BranchBean {
	private String companyId;

	@NotEmpty(message = "BranchId should not be empty")
	@NotNull(message = "BranchId should not be null")
	@Size(max = 6, message = "Branch code character length exceeded")
	private String branchId;

	@NotEmpty(message = "BranchName should not be empty")
	@NotNull(message = "BranchName should not be null")
	@Size(max = 60, message = "Branch name character length exceeded")
	private String branchName;

}
