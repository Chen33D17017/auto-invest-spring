package me.peihao.autoInvest.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.peihao.autoInvest.exception.AutoInvestException;

public enum WeekDayEnum {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    private static final Map<String, WeekDayEnum> valueToEnum = new HashMap<>();
    static {
        Arrays.stream(WeekDayEnum.values())
                .forEach(balanceTypeEnum -> valueToEnum.put(balanceTypeEnum.name(), balanceTypeEnum));
    }

    @JsonCreator(mode = Mode.DELEGATING)
    public static WeekDayEnum forValue(String value) {
        return Optional.ofNullable(valueToEnum.get(value))
                .orElseThrow(() -> new AutoInvestException(ResultInfoConstants.INVALID_WEEK_DAY));
    }
}
