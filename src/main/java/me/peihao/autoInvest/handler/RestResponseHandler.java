package me.peihao.autoInvest.handler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.alibaba.fastjson.JSONObject;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import me.peihao.autoInvest.common.ResultInfo;
import me.peihao.autoInvest.common.ResultUtil;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.exception.AutoInvestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "error_message";

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        JSONObject object = new JSONObject();

        if (ex.getCause() != null) {
            // catch the case when json format not match the DTO like string -> int
            if (ex.getCause() instanceof InvalidFormatException) {
                InvalidFormatException invalidFormatException = (InvalidFormatException) ex
                        .getCause();
                String errMsg = invalidFormatException.getPath().stream()
                        .map(Reference::getFieldName).collect(
                                Collectors.joining(", "));
                object.put(ERROR_MESSAGE, "Invalid Format on " + errMsg);
            } else {
                System.out.println(ex.getCause());
            }
        } else {
            object.put(ERROR_MESSAGE, ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultUtil.buildResult(ResultInfoConstants.BAD_REQUEST, object));
    }

    // handle:
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorCode = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        JSONObject object = new JSONObject();
        String errorMessage;
        ResultInfo resultInfo = ResultInfoConstants.getApiErrorResultInfo(errorCode);

        if (resultInfo.equals(ResultInfoConstants.BAD_REQUEST)) {
            errorMessage = ex.getBindingResult().getFieldError().getField() + ": " + errorCode;
        } else {
            errorMessage = resultInfo.getMessage();
        }
        object.put(ERROR_MESSAGE, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                .body(ResultUtil.buildResult(resultInfo, object));
    }

    // handler: binding error from javax.validation
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        JSONObject object = new JSONObject();
        if (ex.getCause() != null && ex.getCause() instanceof AutoInvestException) {
            AutoInvestException autoInvestException = (AutoInvestException) ex.getCause();
            object.put(ERROR_MESSAGE, autoInvestException.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResultUtil.buildResult(autoInvestException.getResultInfo(), object));
        }
        object.put(ERROR_MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                .body(ResultUtil.buildResult(ResultInfoConstants.BAD_REQUEST, object));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        JSONObject object = new JSONObject();
        object.put(ERROR_MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
            .body(ResultUtil.buildResult(ResultInfoConstants.BAD_REQUEST, object));
    }
}
