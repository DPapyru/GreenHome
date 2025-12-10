package comdpapyru.greenhome.service.user;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import org.springframework.stereotype.Service;

@Service // 标记为Spring的bean(可以自动创建实例)
public interface IUserService {
    /**
     * 添加用户
     * @param user 用户的信息
     * @return 添加的用户
     */
    User add(UserDto user);

    /**
     * 获取用户
     * @param userID 用户ID
     * @return 找到的用户
     */
    User getUser(Integer userID);
    /**
     * 修改用户
     * @param user 用户的信息
     * @return 修改的用户
     */

    User editUser(UserDto user);
}
