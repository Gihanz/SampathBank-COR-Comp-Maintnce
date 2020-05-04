package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.UserListResponseBean;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalPendingUserResponse extends CommonResponse {

    Set<UserListResponseBean> userListResponseBeanSet = new HashSet<>();
}
