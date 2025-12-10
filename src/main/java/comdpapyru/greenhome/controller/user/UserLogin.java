package comdpapyru.greenhome.controller.user;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import comdpapyru.greenhome.pojo.utils.ResponseMessage;
import comdpapyru.greenhome.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录
 */
@RestController // 返回对象 转换为json文本
@RequestMapping("/login") // 通过路径访问这个类的功能（例如 localhost:8088/login/***）
public class UserLogin {
    @Autowired
    IUserService userService;

    /**
     * 注册用户
     *
     * @param user 用户信息
     *             <ul>
     *             <li><code>@Validated</code>会验证参数 </li>
     *             <li><code>@RequestBody</code>会自动将json对象转为User对象</li>
     *             </ul>
     * @return 注册结果
     */
    @PostMapping("/register") // 注册用户
    public ResponseMessage<User> registerUser(@Validated @RequestBody UserDto user) {
        User result = userService.add(user);
        return ResponseMessage.success(result);
    }
}
