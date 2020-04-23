package biz.nable.sb.cor.comp.thirdparty;

import lombok.*;

import java.util.Set;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDetailsResponse {

    public Set<GroupsDetails> groups;
}
