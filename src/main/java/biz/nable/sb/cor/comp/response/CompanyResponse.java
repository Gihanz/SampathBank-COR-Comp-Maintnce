package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import biz.nable.sb.cor.comp.bean.CompanyBean;
import biz.nable.sb.cor.comp.bean.LinkedCompantBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse extends CompanyBean {
	private List<LinkedCompantBean> listOfLinkedCustIds = new ArrayList<>();
	private String companyId;
	private String status;
	protected String createdBy;
	protected Date createdDate;
	protected String lastUpdatedBy;
	protected Date lastUpdatedDate;
	private Date lastVerifiedDate;
	private String lastVerifiedBy;
	private String userGroup;
	private String deviceLocation;
}
