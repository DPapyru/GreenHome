package comdpapyru.greenhome.exception;

import comdpapyru.greenhome.pojo.utils.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 拦截所有异常
public class GlobalExceptionHandlerAdvice {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("请求地址：{} 发生异常：", request.getRequestURL(), e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误：" + e.getMessage());
    }

    /**
     * 处理 IllegalArgumentException 异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseMessage<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("请求地址：{} 发生参数异常：", request.getRequestURL(), e);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), "参数错误：" + e.getMessage());
    }
}
