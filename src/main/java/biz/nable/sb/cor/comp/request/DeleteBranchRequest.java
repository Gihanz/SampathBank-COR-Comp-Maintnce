package biz.nable.sb.cor.comp.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DeleteBranchRequest {
	private String companyId;
	private String branchId;
}
