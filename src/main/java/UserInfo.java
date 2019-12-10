import lombok.Data;

@Data
public class UserInfo {

    private String id;
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
    private String createdAt;
    private String lastLogin;
}
