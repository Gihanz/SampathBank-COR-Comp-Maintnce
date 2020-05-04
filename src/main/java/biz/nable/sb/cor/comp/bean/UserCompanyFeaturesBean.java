package biz.nable.sb.cor.comp.bean;


import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCompanyFeaturesBean {

    private long featureId;
    private String featureName;
}
