package me.peihao.autoInvest.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.common.ResultInfo;

@NoArgsConstructor
public class ResultInfoConstants {
    public static final ResultInfo SUCCESS = new ResultInfo("00000000", "SUCCESS", "Successful Request", "S");
    public static final ResultInfo PARAM_MISSING = new ResultInfo("00000002", "PARAM_MISSING", "One or more mandatory parameters is/are missing", "F");
    public static final ResultInfo PARAM_ILLEGAL = new ResultInfo("00000004", "PARAM_ILLEGAL", "Illegal parameters. For example, non-numeric input, invalid date", "F");
    public static final ResultInfo INVALID_SIGNATURE = new ResultInfo("00000007", "INVALID_SIGNATURE", "Signature is invalid", "F");
    public static final ResultInfo KEY_NO_FOUND = new ResultInfo("00000008", "KEY_NO_FOUND", "Key is not found", "F");
    public static final ResultInfo BAD_REQUEST = new ResultInfo("00000009", "BAD_REQUEST", "Bad request", "F");
    public static final ResultInfo NO_INTERFACE_DEF = new ResultInfo("00000013", "NO_INTERFACE_DEF", "API is not defined", "F");
    public static final ResultInfo API_IS_INVALID = new ResultInfo("00000014", "API_IS_INVALID", "API is invalid (or not active)", "F");
    public static final ResultInfo MSG_PARSE_ERROR = new ResultInfo("00000015", "MSG_PARSE_ERROR", "Message parsed error", "F");
    public static final ResultInfo FUNCTION_NOT_MATCH = new ResultInfo("00000017", "FUNCTION_NOT_MATCH", "Function parameter does not match API", "F");
    public static final ResultInfo SYSTEM_ERROR = new ResultInfo("00000900", "SYSTEM_ERROR", "System error", "F");
    public static final ResultInfo NOT_FOUND = new ResultInfo("00000018", "NOT FOUND", "Not found", "F");

    public static final ResultInfo INVALID_WEEK_DAY = new ResultInfo("01000001", "INVALID_WEEK_DAY", "Invalid Week Day Type", "F");
    public static final ResultInfo BALANCE_INSUFFICIENT = new ResultInfo("01000002", "BALANCE_INSUFFICIENT", "Balance insufficient", "F");
    public static final ResultInfo FAIL_GETTING_ASSET = new ResultInfo("01000003", "FAIL_GETTING_ASSET", "Fail to retrieve asset", "F");
    public static final ResultInfo TRADE_HISTORY_NOT_FOUND = new ResultInfo("01000003", "TRADE_HISTORY_NOT_FOUND", "Not found any records", "F");
    public static final ResultInfo CONFIRM_TOKEN_SET_ERROR = new ResultInfo("01000004", "CONFIRM_TOKEN_SET_ERROR", "Fail to set confirm token", "F");
    public static final ResultInfo CONFIRM_TOKEN_ERROR = new ResultInfo("01000005", "CONFIRM_TOKEN_SET_ERROR", "Fail to confirm token", "F");
    public static final ResultInfo MISSING_REFRESH_TOKEN = new ResultInfo("01000006", "MISSING_REFRESH_TOKEN", "Missing refresh token", "F");
    public static final ResultInfo FAIL_GETTING_INDEX = new ResultInfo("010000007", "FAIL_GETTING_INDEX", "Fail to get fear index", "F");
    public static final ResultInfo NO_HISTORY_FOUND = new ResultInfo("010000008", "NO_HISTORY_FOUND", "No history found", "F");
    private static final Map<String, ResultInfo> codeToResultInfo;
    private static final List<ResultInfo> ResultInfoList = new ArrayList<>();
    static {
        ResultInfoList.add(SUCCESS);
        ResultInfoList.add(PARAM_MISSING);
        ResultInfoList.add(PARAM_ILLEGAL);
        ResultInfoList.add(INVALID_SIGNATURE);
        ResultInfoList.add(KEY_NO_FOUND);
        ResultInfoList.add(BAD_REQUEST);
        ResultInfoList.add(NO_INTERFACE_DEF);
        ResultInfoList.add(API_IS_INVALID);
        ResultInfoList.add(FUNCTION_NOT_MATCH);
        ResultInfoList.add(SYSTEM_ERROR);
        ResultInfoList.add(MSG_PARSE_ERROR);

        codeToResultInfo = ResultInfoList.stream().collect(
                Collectors.toMap(ResultInfo::getCode, Function.identity()));
    }

    public static ResultInfo getApiErrorResultInfo(String errorCode) {
        return Optional.ofNullable(codeToResultInfo.get(errorCode)).orElse(ResultInfoConstants.BAD_REQUEST);
    }
}
