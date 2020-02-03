package biz.nable.sb.cor.comp.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeleteBranchRequest {
	private String companyId;
	private String branchId;
}
