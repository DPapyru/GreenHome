package comdpapyru.greenhome.controller.user;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.dto.UserUpdateDto;
import comdpapyru.greenhome.pojo.utils.ResponseMessage;
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
@RequestMapping("/user")
@Validated
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResponseMessage<User> getCurrentUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseMessage.error("未登录");
        }
        return ResponseMessage.success("获取成功", user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public ResponseMessage<User> updateUser(
            @Valid @RequestBody UserUpdateDto updateDto,
            HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseMessage.error("未登录");
            }

            // 验证当前密码（如果要修改密码）
            if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty()) {
                if (updateDto.getCurrentPassword() == null || updateDto.getCurrentPassword().isEmpty()) {
                    return ResponseMessage.error("请输入当前密码");
                }

                // 验证当前密码
                if (!PasswordUtil.verifyPassword(updateDto.getCurrentPassword(),
                        currentUser.getPassword(), currentUser.getPasswordSalt())) {
                    return ResponseMessage.error("当前密码错误");
                }

                // 更新密码
                String newSalt = PasswordUtil.generateSalt();
                String newEncryptedPassword = PasswordUtil.encryptPassword(updateDto.getNewPassword(), newSalt);

                currentUser.setPassword(newEncryptedPassword);
                currentUser.setPasswordSalt(newSalt);

                logger.info("用户 {} 修改了密码", currentUser.getUserName());
            }

            // 更新用户名（如果修改了）
            if (!currentUser.getUserName().equals(updateDto.getUserName())) {
                currentUser.setUserName(updateDto.getUserName());
                logger.info("用户 {} 修改了用户名为 {}", currentUser.getUserName(), updateDto.getUserName());
            }

            // 更新邮箱（如果修改了）
            if (!currentUser.getEmail().equals(updateDto.getEmail())) {
                currentUser.setEmail(updateDto.getEmail());
                logger.info("用户 {} 修改了邮箱", currentUser.getUserName());
            }

            // 保存更新
            User updatedUser = userAuthService.updateUser(currentUser);

            // 更新session中的用户信息
            session.setAttribute("user", updatedUser);
            session.setAttribute("userName", updatedUser.getUserName());
            session.setAttribute("userRole", updatedUser.getRole());

            logger.info("用户信息更新成功: {}", updatedUser.getUserName());
            return ResponseMessage.success("更新成功", updatedUser);

        } catch (IllegalArgumentException e) {
            logger.warn("用户信息更新失败: {}", e.getMessage());
            return ResponseMessage.error(e.getMessage());
        } catch (Exception e) {
            logger.error("用户信息更新异常: ", e);
            return ResponseMessage.error("更新失败，请稍后重试");
        }
    }
}
