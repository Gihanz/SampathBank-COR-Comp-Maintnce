package biz.nable.sb.cor.comp.thirdparty;

import lombok.*;

import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsertUpdateUserRequest {

    private long userId;
    private String companyId;
    private Set<GroupsRequest> groups;
}
