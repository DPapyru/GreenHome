package comdpapyru.greenhome.service.user;

import comdpapyru.greenhome.exception.NewUserException;
import comdpapyru.greenhome.exception.SpawnInvCodeException;
import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService implements IUserService {
    private static final StringBuffer STRING_BUFFER = new StringBuffer(50);
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    /**
     * 用户服务
     */
    @Autowired
    UserRepository userRepository;

    /**
     * 添加用户信息
     * @param user 用户的信息
     * @return 添加的用户
     */
    @Override
    public User add(UserDto user) {
        try {
            // 调用方法, 将用户信息保存到数据库
            User userPojo = new User(); // 创建POJO对象
            BeanUtils.copyProperties(user, userPojo); // 将DTO对象转换为POJO对象
            User result = add_getUser(userPojo);
            return result;
        }
        catch (Exception e) {
            LOGGER.error("用户保存失败,报错信息：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 保存用户到数据库并且进行一些处理
     * @param userPojo 用户的POJO对象
     * @return 保存后的用户
     */
    private User add_getUser(User userPojo) throws SpawnInvCodeException, NewUserException {
        // 检查输入的用户的邀请码是否存在的
        var invCode = userPojo.getInv_code();
        var checkUser = checkUserInvCode(invCode);
        if(checkUser == null){
            throw new NewUserException("用户邀请码不存在");
        }

        userPojo.setInv_code(spawnUserInvCode());
        removeUserInvCode(checkUser);

        User savedUser = userRepository.save(userPojo); // 把POJO对象保存到数据库

        if(savedUser == null){ // nm你Null了还画线是吧
            LOGGER.error(STRING_BUFFER.append("\n用户保存失败,").append("用户信息：").append(userPojo.toString()).toString());
        }
        else {
            LOGGER.info(STRING_BUFFER.append("\n用户保存成功,").append("用户信息：").append(savedUser).toString());
        }
        STRING_BUFFER.setLength(0);
        return savedUser;
    }

    /**
     * 检查用户邀请码是否存在
     * @param invCode 用户的邀请码
     * @return 有用户返回则为true
     */
    private User checkUserInvCode(String invCode){
        // 先获取数据库里面所有的用户
        var users = userRepository.findAll();

        // 遍历所有用户列表,邀请码一致则返回true
        for (User user : users) {
            if(user.getInv_code() == null){
                continue;
            }

            if (user.getInv_code().equals(invCode)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 当用户的邀请码被使用后,需要将其删除
     * @param user 用户
     */
    private void removeUserInvCode(User user){
        var userOptional = userRepository.findById(user.getUserID()); // 通过用户ID获取
        if (userOptional.isPresent()) {
            User updateUser = userOptional.get();
            updateUser.setInv_code(null); // 将邀请码设置为null，表示已使用
            userRepository.save(updateUser); // 保存更新后的用户信息
            LOGGER.info("用户 {} 的邀请码已被移除", user.getUserID());
        } else {
            LOGGER.warn("尝试移除不存在的用户邀请码，用户ID: {}", user.getUserID());
        }
    }

    /**
     * 用户邀请码随机生成
     */
    private String spawnUserInvCode() throws SpawnInvCodeException{
        Random random = new Random();
        try {
            for (int i = 0; i < 10; i++) {
                char c = (char) (random.nextInt(52) + 'a'); // 范围 a-zA-Z
                STRING_BUFFER.append(c); // 添加字符
            }
            STRING_BUFFER.append("-"); // 连接符
            for(int i = 0;i<15;i++){
                char c = (char) (random.nextInt(10) + '0'); // 范围 0-9
                STRING_BUFFER.append(c);
            }

            var invCode = STRING_BUFFER.toString();
            STRING_BUFFER.setLength(0); // 重置字符串缓冲区
            return invCode;
        }
        catch (Exception e) {
            throw new SpawnInvCodeException();
        }
    }

    @Override
    public User getUser(Integer userID) {
        return userRepository.findById(userID).orElseThrow(() -> new IllegalArgumentException("用户不存在,参数异常"));
    }

    @Override
    public User editUser(UserDto user) {
        try {
            // 调用方法, 将用户信息保存到数据库
            User userPojo = new User(); // 创建POJO对象
            BeanUtils.copyProperties(user, userPojo); // 将DTO对象转换为POJO对象
            return add_getUser(userPojo);
        }
        catch (Exception e) {
            LOGGER.error("用户编辑失败,报错信息：{}", e.getMessage());
            return null;
        }
    }
}
