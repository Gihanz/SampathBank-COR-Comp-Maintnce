package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.UserListResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseList extends CommonResponse {

    public UserResponseList(int returnCode, String returnMessage, String errorCode) {
        super(returnCode, returnMessage, errorCode);
    }

    Set<UserListResponse> userListResponses;
}

