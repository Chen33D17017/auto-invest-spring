package me.peihao.autoInvest.common;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class ResultInfo implements Serializable {
    private String codeId;
    private String code;
    private String message;
    private String status;

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("codeId", this.codeId);
        jsonObject.put("code", this.code);
        jsonObject.put("message", this.message);
        jsonObject.put("status", this.status);
        return jsonObject.toJSONString();
    }

    @ConstructorProperties({"codeId", "code", "message", "status"})
    public ResultInfo(String codeId, String code, String message, String status) {
        this.codeId = codeId;
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
