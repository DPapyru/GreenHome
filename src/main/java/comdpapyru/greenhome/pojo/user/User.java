package comdpapyru.greenhome.pojo.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import comdpapyru.greenhome.pojo.article.Article;
import java.util.List;

@Table(name = "greenhome_user") // 映射到数据表
@Entity
public class User {
    /**
     * 用户ID
     */
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    @Column(name = "user_id", unique = true) // 用户的ID不会重复
    private Integer userID;
    /**
     * 用户名
     */
    @Column(name = "user_name")
    @NotEmpty(message = "用户名不能为空") // 设置用户名不能为空
    private String userName;
    /**
     * 用户密码
     */
    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    @NotEmpty(message = "密码不能为空") // 密码不能为空
    @Length(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;

    /**
     * 密码盐值
     */
    @Column(name = "password_salt")
    private String passwordSalt;

    /**
     * 用户邮箱
     */
    @Column(name = "email")
    @Email(message = "邮箱格式不正确") // 邮箱格式
    private String email;

    /**
     * 用户的邀请码
     */
    @Column(name = "inv_code")
    private String inv_code;
    
    /**
     * 用户角色
     */
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER; // 默认为普通用户

    /**
     * 用户的文章列表
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInv_code() {
        return inv_code;
    }

    public void setInv_code(String inv_code) {
        this.inv_code = inv_code;
    }
    
    public List<Article> getArticles() {
        return articles;
    }
    
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", userName='" + userName + '\'' + ", password='" + password + '\'' + ", email='" + email + '\'' + ", inv_code='" + inv_code + '\'' + ", role=" + role + '}';
    }
}
