package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LinkCompanyRequest implements CommonTempBean {
	private String parentCompanyId;
	private String customerId;
}
