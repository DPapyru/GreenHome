package comdpapyru.greenhome.pojo.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

@Table(name="greenhome_user") // 映射到数据表
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
    @Column(name = "password")
    @NotEmpty(message = "密码不能为空") // 密码不能为空
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
    /**
     * 用户邮箱
     */
    @Column(name = "email")
    @Email(message = "邮箱格式不正确") // 邮箱格式
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
