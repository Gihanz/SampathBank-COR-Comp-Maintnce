package biz.nable.sb.cor.comp.bean;

import java.util.Date;

import lombok.Data;

@Data
public class LinkedCompantBean {
	private String customerId;
	private String status;
	protected String createdBy;
	protected Date createdDate;
	protected String lastUpdatedBy;
	protected Date lastUpdatedDate;
	private Date lastVerifiedDate;
	private String lastVerifiedBy;
}
