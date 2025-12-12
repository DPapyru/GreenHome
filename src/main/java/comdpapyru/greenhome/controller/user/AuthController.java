package comdpapyru.greenhome.controller.user;

import comdpapyru.greenhome.exception.NewUserException;
import comdpapyru.greenhome.exception.SpawnInvCodeException;
import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.UserRole;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import comdpapyru.greenhome.pojo.user.dto.UserLoginDto;
import comdpapyru.greenhome.pojo.utils.ResponseMessage;
import comdpapyru.greenhome.repository.user.UserRepository;
import comdpapyru.greenhome.service.user.UserAuthService;
import comdpapyru.greenhome.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseMessage<User> register(@Valid @RequestBody UserDto userDto, HttpSession session) {
        try {
            logger.info("用户注册请求: {}", userDto.getUserName());

            // 检查是否已经登录
            if (session.getAttribute("user") != null) {
                return ResponseMessage.error("您已经登录，无需重复注册");
            }

            User user = userAuthService.register(userDto);

            // 注册成功后自动登录
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserID());
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("userRole", user.getRole());

            logger.info("用户 {} 注册并登录成功", user.getUserName());
            return ResponseMessage.success("注册成功", user);

        } catch (NewUserException | SpawnInvCodeException e) {
            logger.warn("用户注册失败: {}", e.getMessage());
            return ResponseMessage.error(e.getMessage());
        } catch (Exception e) {
            logger.error("用户注册异常: ", e);
            return ResponseMessage.error("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseMessage<User> login(@Valid @RequestBody UserLoginDto loginDto, HttpSession session) {
        try {
            logger.info("用户登录请求: {}", loginDto.getUserName());

            // 检查是否已经登录
            if (session.getAttribute("user") != null) {
                User currentUser = (User) session.getAttribute("user");
                return ResponseMessage.success("您已经登录", currentUser);
            }

            User user = userAuthService.login(loginDto);

            // 将用户信息存入session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserID());
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("userRole", user.getRole());

            logger.info("用户 {} 登录成功", user.getUserName());
            return ResponseMessage.success("登录成功", user);

        } catch (IllegalArgumentException e) {
            logger.warn("用户登录失败: {}", e.getMessage());
            return ResponseMessage.error(e.getMessage());
        } catch (Exception e) {
            logger.error("用户登录异常: ", e);
            return ResponseMessage.error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseMessage<String> logout(HttpSession session) {
        try {
            String userName = (String) session.getAttribute("userName");
            session.invalidate();
            logger.info("用户 {} 登出成功", userName);
            return ResponseMessage.success("登出成功");
        } catch (Exception e) {
            logger.error("用户登出异常: ", e);
            return ResponseMessage.error("登出失败");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public ResponseMessage<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseMessage.error("未登录");
        }
        return ResponseMessage.success("获取成功", user);
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/check-status")
    public ResponseMessage<String> checkLoginStatus(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return ResponseMessage.error("未登录");
        }
        return ResponseMessage.success("已登录", userName);
    }

    /**
     * 临时API - 手动创建管理员账号（仅在测试环境使用）
     */
    @PostMapping("/create-admin")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseMessage<User> createAdmin() {
        try {
            // 手动创建管理员账号
            String salt = PasswordUtil.generateSalt();
            String password = "admin123456";
            String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(encryptedPassword);
            admin.setPasswordSalt(salt);
            admin.setEmail("admin@tmodloader.cn");
            admin.setRole(UserRole.ADMIN);
            admin.setInv_code("ADMIN-INIT-CODE-12345");

            User savedAdmin = userRepository.save(admin);
            logger.info("手动创建管理员账号成功: {}", savedAdmin.getUserName());

            return ResponseMessage.success("管理员账号创建成功", savedAdmin);
        } catch (Exception e) {
            logger.error("创建管理员账号失败: ", e);
            return ResponseMessage.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 快速修复API - 创建测试管理员账号（绕过所有验证）
     */
    @GetMapping("/fix-admin")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseMessage<String> fixAdmin(HttpSession session) {
        try {
            // 检查是否已有管理员
            if (userRepository.findByUserName("admin").isPresent()) {
                return ResponseMessage.success("管理员账号已存在");
            }

            // 直接插入管理员账号
            String salt = PasswordUtil.generateSalt();
            String password = "admin123456";
            String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(encryptedPassword);
            admin.setPasswordSalt(salt);
            admin.setEmail("admin@tmodloader.cn");
            admin.setRole(UserRole.ADMIN);
            admin.setInv_code("ADMIN-INIT-CODE-12345");

            userRepository.save(admin);
            logger.info("快速修复：管理员账号创建成功");

            return ResponseMessage.success("管理员账号创建成功，现在可以登录了！\n用户名: admin\n密码: admin123456");
        } catch (Exception e) {
            logger.error("快速修复失败: ", e);
            return ResponseMessage.error("修复失败: " + e.getMessage());
        }
    }

    /**
     * 临时测试端点 - 检查是否有管理员用户
     */
    @GetMapping("/check-admin")
    public ResponseMessage<String> checkAdminExists() {
        try {
            boolean adminExists = userAuthService.isAdminExists();
            if (adminExists) {
                return ResponseMessage.success("管理员用户存在");
            } else {
                return ResponseMessage.error("管理员用户不存在");
            }
        } catch (Exception e) {
            logger.error("检查管理员用户失败: ", e);
            return ResponseMessage.error("检查失败: " + e.getMessage());
        }
    }
}