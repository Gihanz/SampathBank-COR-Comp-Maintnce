package biz.nable.sb.cor.comp.bean;

import biz.nable.sb.cor.comp.response.CompanyResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyListResponseBean {
	private CompanyResponse companyResponse;
	private CompanyTempBean tempCompanyResponse;
}