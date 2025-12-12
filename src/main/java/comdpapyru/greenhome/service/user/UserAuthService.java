package comdpapyru.greenhome.service.user;

import comdpapyru.greenhome.exception.NewUserException;
import comdpapyru.greenhome.exception.SpawnInvCodeException;
import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.UserRole;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import comdpapyru.greenhome.pojo.user.dto.UserLoginDto;
import comdpapyru.greenhome.repository.user.UserRepository;
import comdpapyru.greenhome.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    public User register(UserDto userDto) throws NewUserException, SpawnInvCodeException {
        // 检查用户名是否已存在
        if (userRepository.existsByUserName(userDto.getUserName())) {
            throw new NewUserException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new NewUserException("邮箱已被注册");
        }

        // 生成密码盐值并加密密码
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(userDto.getPassword(), salt);

        // 创建用户对象
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setPassword(encryptedPassword);
        user.setPasswordSalt(salt);
        user.setEmail(userDto.getEmail());
        user.setInv_code(userDto.getInv_code());
        user.setRole(UserRole.USER); // 默认为普通用户

        // 调用现有的用户服务处理邀请码逻辑
        return userService.add_getUser(user);
    }

    /**
     * 用户登录
     */
    public User login(UserLoginDto loginDto) {
        User user = userRepository.findByUserName(loginDto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        // 验证密码
        if (!PasswordUtil.verifyPassword(loginDto.getPassword(), user.getPassword(), user.getPasswordSalt())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        logger.info("用户 {} 登录成功", user.getUserName());
        return user;
    }

    /**
     * 根据用户名查找用户
     */
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName).orElse(null);
    }

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin(User user) {
        return user != null && UserRole.ADMIN.equals(user.getRole());
    }

    /**
     * 检查是否有管理员用户存在
     */
    public boolean isAdminExists() {
        return userRepository.existsByRole(UserRole.ADMIN);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}