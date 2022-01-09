package me.peihao.autoInvest.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.peihao.autoInvest.common.ResultInfo;

@Data
@EqualsAndHashCode(callSuper = false)
public class AutoInvestException extends RuntimeException{
    private ResultInfo resultInfo;

    public AutoInvestException(ResultInfo resultInfo) {
        super(resultInfo.getMessage());
        this.resultInfo = resultInfo;
    }
}
