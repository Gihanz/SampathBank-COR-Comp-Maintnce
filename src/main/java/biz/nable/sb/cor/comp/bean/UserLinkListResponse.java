package biz.nable.sb.cor.comp.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLinkListResponse {

    private String userId;
    private String approvalId;
    private String signature;
    private String approvalStatus;
    private LinkedCompaniesBean linkedCompaniesBean;
}
