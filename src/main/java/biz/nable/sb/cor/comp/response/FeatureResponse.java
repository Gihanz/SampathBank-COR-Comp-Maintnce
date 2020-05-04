package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.FeatureBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeatureResponse extends CommonResponse {
	List<FeatureBean> featureBeans = new ArrayList<>();
}
