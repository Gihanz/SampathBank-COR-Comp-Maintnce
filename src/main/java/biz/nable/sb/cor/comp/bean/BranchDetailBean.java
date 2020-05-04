package biz.nable.sb.cor.comp.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BranchDetailBean extends BranchBean {
	private String companyName;
	private String requestId;
	private String status;
	protected String createdBy;
	protected Date createdDate;
	protected String lastUpdatedBy;
	protected Date lastUpdatedDate;
	private Date lastVerifiedDate;
	private String lastVerifiedBy;
}
