package grupo12.practico.dtos.NotificationToken;

import java.io.Serializable;

public class NotificationTokenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userCi;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserCi() {
        return userCi;
    }

    public void setUserCi(String userCi) {
        this.userCi = userCi;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
