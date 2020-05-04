package biz.nable.sb.cor.comp.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkCompanyDeleteRequest {
	private String parentCompanyId;
	private String customerId;
	private String reason;
}
