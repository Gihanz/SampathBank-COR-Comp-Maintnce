package biz.nable.sb.cor.comp.bean;

import lombok.Setter;
import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindCompanyBean {
	private String createdFromDate;
	private String createdToDate;
	private String lastUpdatedFromDate;
	private String lastUpdatedToDate;
	private String createdBy;
	private String lastUpdatedBy;
	private String createdUserGroup;
	private String lastUpdatedUserGroup;
	private String companyName;
	private StatusEnum status;
}
