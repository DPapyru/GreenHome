package comdpapyru.greenhome;

import comdpapyru.greenhome.controller.user.UserController;
import comdpapyru.greenhome.controller.user.UserLogin;
import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.pojo.user.dto.UserDto;
import comdpapyru.greenhome.pojo.utils.ResponseMessage;
import comdpapyru.greenhome.repository.user.UserRepository;
import comdpapyru.greenhome.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * User功能测试类
 * 用于测试用户相关功能，包括用户创建等操作
 */
@SpringBootTest
class GreenHomeTest_User {
    /**
     * 模拟用户仓库对象
     * 使用@Mock注解创建UserRepository的模拟对象，用于替代真实的数据库操作
     */
    @Mock
    private UserRepository userRepository;

    /**
     * 注入模拟对象到UserService中
     * 使用@InjectMocks注解，将模拟的userRepository注入到userService实例中
     */
    @InjectMocks
    private UserService userService;

    /**
     * 用于管理Mockito annotations的生命周期
     * AutoCloseable接口用于确保测试结束后正确关闭所有mock资源
     */
    private AutoCloseable closeable;

    /**
     * 测试前初始化方法
     * 在每个测试方法执行前运行，初始化Mockito的注解
     */
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试后清理方法
     * 在每个测试方法执行后运行，负责释放Mockito资源
     *
     * @throws Exception 当关闭资源出现异常时抛出
     */
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // -------------------测试添加用户功能-------------------

    /**
     * 测试添加用户功能
     * 验证UserService的add方法是否能正确保存用户并返回保存后的用户信息
     */
    @Test
    void add_shouldSaveUserAndReturnSavedUser() {
        // Given (准备测试数据和环境)
        UserDto userDto = new UserDto();
        userDto.setUserName("testUser");
        userDto.setPassword("testPassword");
        userDto.setEmail("test@example.com");
        System.out.println(userDto); // 输出用户信息

        // 设置模拟行为：当调用userRepository.save时，返回一个带有ID的User对象
        // 不去操作数据库，而是直接返回我们预设好的mockUser对象
        User mockUser = new User(); // 创建模拟User对象
        BeanUtils.copyProperties(userDto, mockUser); // 将userDto对象中的属性值复制到mockUser对象中
        mockUser.setUserID(1);

        /*
        when 定义模拟行为
        any(User.class) 表示任何User对象
          any() 表示任何参数
          thenReturn(mockUser) 表示当调用save方法时，返回mockUser对象
         */
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When (执行被测试的方法)
        User result = userService.add(userDto);
        // 添加null检查避免NullPointerException
        if (result != null) {
            System.out.println(result.toString());
        } else {
            System.out.println("User save failed, result is null");
        }

        // Then (验证结果是否符合预期)
        assertNotNull(result);  // 验证返回结果不为空
        assertEquals(1, result.getUserID());  // 验证用户ID是否正确设置
        assertEquals("testUser", result.getUserName());  // 验证用户名是否正确
        assertEquals("testPassword", result.getPassword());  // 验证密码是否正确
        assertEquals("test@example.com", result.getEmail());  // 验证邮箱是否正确

        // 验证userRepository.save()方法有且仅有一次被调用
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * 测试添加多个用户功能
     */
    @Test
    void add_twoUsers_shouldSaveBothUsersAndReturnSavedUsers() {
        // 设置模拟行为：当调用userRepository.save时，返回一个带有ID的User对象
        // 不去操作数据库，而是直接返回我们预设好的mockUser对象
        User mockUser = new User(); // 创建模拟User对象
        for (int i = 0; i < 2; i++) {
            UserDto userDto = new UserDto();
            userDto.setUserName("testUser" + i);
            userDto.setPassword("testPassword" + i);
            userDto.setEmail("test@example.com");
            System.out.println(userDto); // 输出用户信息

            BeanUtils.copyProperties(userDto, mockUser); // 将userDto对象中的属性值复制到mockUser对象中
            mockUser.setUserID(i);

            /*
            when 定义模拟行为
            any(User.class) 表示任何User对象
              any() 表示任何参数
              thenReturn(mockUser) 表示当调用save方法时，返回mockUser对象
             */
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // When (执行被测试的方法)
            User result = userService.add(userDto);
            System.out.println(result.toString());

            // Then (验证结果是否符合预期)
            assertNotNull(result);  // 验证返回结果不为空
            assertEquals(i, result.getUserID());  // 验证用户ID是否正确设置
            assertEquals(userDto.getUserName(), result.getUserName());  // 验证用户名是否正确
            assertEquals(userDto.getPassword(), result.getPassword());  // 验证密码是否正确
            assertEquals(userDto.getEmail(), result.getEmail());  // 验证邮箱是否正确
        }
        // 验证userRepository.save()方法有且仅有两次被调用
        verify(userRepository, times(2)).save(any(User.class));
    }

    // -------------------测试用户登录与注册-------------------

    /**
     * 测试用户注册功能
     */
    @Test
    void registerUser_shouldSaveUserAndReturnSavedUser() {
        // Given (准备测试数据和环境)
        UserDto userDto = new UserDto();
        userDto.setUserName("testUser");
        userDto.setPassword("testPassword");
        userDto.setEmail("test@example.com");
        
        // 创建UserLogin控制器实例
        UserLogin userLogin = new UserLogin();
        
        // 使用反射设置userService（因为它是@Autowired的，但在测试中需要手动注入）
        try {
            java.lang.reflect.Field userServiceField = UserLogin.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(userLogin, userService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("无法注入userService: " + e.getMessage());
        }
        
        // 设置模拟行为：当调用userRepository.save时，返回一个带有ID的User对象
        User mockUser = new User();
        BeanUtils.copyProperties(userDto, mockUser);
        mockUser.setUserID(1); // 设置用户ID
        when(userRepository.save(any(User.class))).thenReturn(mockUser); // 设置返回的User对象
        
        // When (执行被测试的方法)
        var result = userLogin.registerUser(userDto);
        
        // Then (验证结果是否符合预期)
        assertEquals(new ResponseMessage<>(HttpStatus.OK.value(), "连接成功", mockUser).toString(), result.toString());
        System.out.println(result);
        
        // 验证userRepository.save()方法有且仅有一次被调用
        verify(userRepository, times(1)).save(any(User.class));
    }

    // -------------------测试获取用户功能-------------------

    /**
     * 测试根据用户ID获取用户功能
     */
    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Given
        Integer userId = 1;
        User mockUser = new User();
        mockUser.setUserID(userId);
        mockUser.setUserName("testUser");
        mockUser.setPassword("testPassword");
        mockUser.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        // When
        User result = userService.getUser(userId); // getUser会调用 userRepository.findById获取对应的用户信息

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserID());
        assertEquals("testUser", result.getUserName());
        assertEquals("testPassword", result.getPassword());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    /**
     * 测试根据用户ID获取用户功能 - 用户不存在的情况
     */
    @Test
    void getUserById_shouldThrowException_whenUserNotExists() {
        // Given
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(userId);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    // -------------------测试UserController功能-------------------

    /**
     * 测试UserController实例化
     */
    @Test
    void userController_shouldBeInstantiated() {
        // When
        UserController userController = new UserController();

        // Then
        assertNotNull(userController);
    }
}