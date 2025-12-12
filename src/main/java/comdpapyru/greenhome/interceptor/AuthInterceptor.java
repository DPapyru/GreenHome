package comdpapyru.greenhome.interceptor;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 允许未认证访问的路径
        if (isPublicPath(requestURI)) {
            return true;
        }

        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("未授权访问: {}", requestURI);
            sendErrorResponse(response, 401, "请先登录");
            return false;
        }

        // 检查管理员权限
        if (isAdminPath(requestURI)) {
            User user = (User) session.getAttribute("user");
            if (!UserRole.ADMIN.equals(user.getRole())) {
                logger.warn("非管理员用户尝试访问管理员路径: {}, 用户: {}", requestURI, user.getUserName());
                sendErrorResponse(response, 403, "需要管理员权限");
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否为公开路径
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.equals("/auth/login") ||
               requestURI.equals("/auth/register") ||
               requestURI.equals("/auth/current-user") ||
               requestURI.equals("/auth/check-status") ||
               requestURI.equals("/auth/fix-admin") ||
               requestURI.equals("/auth/create-admin") ||
               requestURI.startsWith("/articles/public") ||
               requestURI.equals("/error") ||
               requestURI.startsWith("/static/") ||
               requestURI.startsWith("/css/") ||
               requestURI.startsWith("/js/") ||
               requestURI.startsWith("/images/");
    }

    /**
     * 判断是否为管理员路径
     */
    private boolean isAdminPath(String requestURI) {
        return requestURI.startsWith("/admin/") ||
               requestURI.equals("/users/list") ||
               requestURI.startsWith("/articles/admin/");
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
            "{\"code\":%d,\"message\":\"%s\",\"data\":null}",
            statusCode, message
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}