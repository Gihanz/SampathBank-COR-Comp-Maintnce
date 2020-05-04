package biz.nable.sb.cor.comp.bean;

import java.util.Date;

import biz.nable.sb.cor.common.utility.ActionTypeEnum;
import biz.nable.sb.cor.comp.response.CompanyResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyListResponseBean {
	private Long id;
	private String signature;
	private String approvalId;
	private String requestType;
	private String referenceNo;
	private String createdBy;
	private Date createdDate;
	private String lastUpdatedBy;
	private Date lastUpdatedDate;
	private String userGroup;
	private ActionTypeEnum actionType;
	private CompanyResponse companyResponse;
	private CompanyTempBean tempCompanyResponse;
}