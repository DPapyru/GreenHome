package comdpapyru.greenhome.repository.user;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUserName(String userName);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUserName(String userName);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查是否存在指定角色的用户
     */
    boolean existsByRole(UserRole role);

}
