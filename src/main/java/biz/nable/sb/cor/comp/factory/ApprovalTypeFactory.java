package biz.nable.sb.cor.comp.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import biz.nable.sb.cor.common.template.CommonApprovalTemplate;
import biz.nable.sb.cor.comp.factory.impl.BranchApproval;
import biz.nable.sb.cor.comp.factory.impl.CompanyApproval;
import biz.nable.sb.cor.comp.factory.impl.LinkCompanyApproval;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Component
public class ApprovalTypeFactory {

	@Autowired
	CompanyApproval company;

	@Autowired
	LinkCompanyApproval linkCompanyApproval;
	@Autowired
	BranchApproval branchApproval;

	public CommonApprovalTemplate getApproval(String type) {
		if (null == type) {
			return null;
		} else if (RequestTypeEnum.COMPANY.name().equalsIgnoreCase(type)) {
			return company;
		} else if (RequestTypeEnum.LINK_COMPANY.name().equalsIgnoreCase(type)) {
			return linkCompanyApproval;
		} else if (RequestTypeEnum.BRANCH.name().equalsIgnoreCase(type)) {
			return branchApproval;
		} else {
			return null;
		}
	}
}
