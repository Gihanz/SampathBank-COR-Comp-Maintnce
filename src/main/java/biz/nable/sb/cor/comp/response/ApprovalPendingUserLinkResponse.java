package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.UserLinkListResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPendingUserLinkResponse extends CommonResponse {

    Set<UserLinkListResponse> userLinkListResponseHashSet = new HashSet<>();
}
