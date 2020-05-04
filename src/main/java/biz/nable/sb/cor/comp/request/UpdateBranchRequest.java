package biz.nable.sb.cor.comp.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import biz.nable.sb.cor.comp.bean.BranchBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(value = "companyId, branchId")
public class UpdateBranchRequest extends BranchBean implements CommonTempBean {
}
