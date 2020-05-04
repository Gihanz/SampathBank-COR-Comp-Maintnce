package biz.nable.sb.cor.comp.request;

import biz.nable.sb.cor.comp.utility.StatusUserEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {

    private String reason;
    private StatusUserEnum blockedStatus;
}
