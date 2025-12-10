package comdpapyru.greenhome.service.user;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public User add(UserDto user) {
        try {
            // 调用方法, 将用户信息保存到数据库
            User userPojo = new User(); // 创建POJO对象
            BeanUtils.copyProperties(user, userPojo); // 将DTO对象转换为POJO对象
            User savedUser = userRepository.save(userPojo);

            if(savedUser == null){ // nm你Null了还画线是吧
                LOGGER.error(STRING_BUFFER.append("\n用户保存失败,").append("用户信息：").append(userPojo.toString()).toString());
            }
            else {
                LOGGER.info(STRING_BUFFER.append("\n用户保存成功,").append("用户信息：").append(savedUser).toString());
            }
            STRING_BUFFER.setLength(0);
            return savedUser;
        }
        catch (Exception e) {
            LOGGER.error("用户保存失败,报错信息：{}", e.getMessage());
            return null;
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
            User savedUser = userRepository.save(userPojo);

            if(savedUser == null){ // nm你Null了还画线是吧
                LOGGER.error(STRING_BUFFER.append("\n用户保存失败,").append("用户信息：").append(userPojo.toString()).toString());
            }
            else {
                LOGGER.info(STRING_BUFFER.append("\n用户保存成功,").append("用户信息：").append(savedUser).toString());
            }
            STRING_BUFFER.setLength(0);
            return savedUser;
        }
        catch (Exception e) {
            LOGGER.error("用户保存失败,报错信息：{}", e.getMessage());
            return null;
        }
    }
}
