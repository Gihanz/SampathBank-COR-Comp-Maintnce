package biz.nable.sb.cor.comp.response;

import java.util.List;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTempResponse extends CommonResponse implements CommonTempBean {
	private List<CommonTempBean> commonTempBeans;
}
