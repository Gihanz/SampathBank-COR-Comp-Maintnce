package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.comp.bean.BranchResponseBean;
import lombok.Data;

@Data
public class BranchResponse {
	private String companyID;
	private String branchCode;
	private BranchResponseBean current;
	private BranchResponseBean modified;
}
