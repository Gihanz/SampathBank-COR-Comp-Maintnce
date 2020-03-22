package biz.nable.sb.cor.comp.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyAccountResponse {
	private String accountNumber;
	private String accountName;
	private String closedFlag;
	private Double balance;
	private String currency;
	private Date accOpenDate;
	private Date lastTransactionDate;
}
