package biz.nable.sb.cor.comp.request;

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
    private String blockedStatus;
}
