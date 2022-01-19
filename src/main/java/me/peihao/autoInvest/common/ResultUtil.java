package me.peihao.autoInvest.common;

import com.alibaba.fastjson.JSONObject;;
import com.alibaba.fastjson.serializer.SerializerFeature;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResultUtil {

  public ResultUtil() {
  }

  public static String buildResult(ResultInfo resultInfo, Object data) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("resultInfo", resultInfo);
    jsonObject.put("data", data);
    return convertToJsonString(jsonObject);
  }

  private static String convertToJsonString(Object object) {
    return JSONObject.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss.SSS",
        SerializerFeature.WriteMapNullValue);
  }

  public static ResultInfo getResultInfo(JSONObject result) {
    return (ResultInfo) JSONObject.parseObject(result.getString("resultInfo"), ResultInfo.class);
  }

  public static <T> T getResultData(ResponseEntity<String> responseEntity, Class<T> clazz) {
    String body = (String) responseEntity.getBody();
    return JSONObject.parseObject(body, clazz);
  }

  public static ResultInfo getResultInfo(ResponseEntity<String> responseEntity) {
    String body = (String) responseEntity.getBody();
    return (ResultInfo) getResultData(responseEntity, ResultInfo.class);
  }

  public boolean success(ResponseEntity<String> responseEntity) {
    ResultInfo resultInfo = getResultInfo(responseEntity);
    return "S".equalsIgnoreCase(resultInfo.getStatus());
  }

  public static ResponseEntity<String> generateSuccessResponse(Object response) {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
        body(ResultUtil.buildResult(ResultInfoConstants.SUCCESS, response));
  }
}

