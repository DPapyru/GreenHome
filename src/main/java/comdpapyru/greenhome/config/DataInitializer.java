package comdpapyru.greenhome.config;

import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.UserRole;
import comdpapyru.greenhome.repository.user.UserRepository;
import comdpapyru.greenhome.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已经存在管理员用户
        if (!userRepository.existsByRole(UserRole.ADMIN)) {
            createAdminUser();
        } else {
            logger.info("管理员用户已存在，跳过初始化");
        }
    }

    /**
     * 创建默认管理员用户
     */
    private void createAdminUser() {
        try {
            String salt = PasswordUtil.generateSalt();
            String password = "admin123456"; // 默认密码
            String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(encryptedPassword);
            admin.setPasswordSalt(salt);
            admin.setEmail("admin@tmodloader.cn");
            admin.setRole(UserRole.ADMIN);
            admin.setInv_code("ADMIN-INIT-CODE-12345"); // 管理员的邀请码，可以用来邀请其他用户

            userRepository.save(admin);

            logger.info("===============================================");
            logger.info("默认管理员用户创建成功！");
            logger.info("用户名: admin");
            logger.info("密码: admin123456");
            logger.info("邀请码: ADMIN-INIT-CODE-12345");
            logger.info("请及时修改默认密码以确保安全！");
            logger.info("===============================================");

        } catch (Exception e) {
            logger.error("创建管理员用户失败", e);
        }
    }
}