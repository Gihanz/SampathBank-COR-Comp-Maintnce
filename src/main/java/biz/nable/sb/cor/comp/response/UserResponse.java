package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class UserResponse extends CommonResponse {

    private List<Object> userResponseList;
}
