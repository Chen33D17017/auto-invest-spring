package me.peihao.autoInvest.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.peihao.autoInvest.exception.AutoInvestException;

public enum WhenDuplicateEnum {
  ignore,
  error,
  overwrite;

  private static final Map<String, WhenDuplicateEnum> valueToEnum = new HashMap<>();
  static {
    Arrays.stream(WhenDuplicateEnum.values())
        .forEach(whenDuplicateEnum -> valueToEnum.put(whenDuplicateEnum.name(), whenDuplicateEnum));
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static WhenDuplicateEnum forValue(String value) {
    return Optional.ofNullable(valueToEnum.get(value))
        .orElseThrow(() -> new AutoInvestException(ResultInfoConstants.INVALID_SETTING));
  }
}
