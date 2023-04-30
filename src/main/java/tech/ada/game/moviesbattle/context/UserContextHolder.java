package tech.ada.game.moviesbattle.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class UserContextHolder {

    private UserContextInfo userContextInfo;

    public UserContextInfo getUserContextInfo() {
        return userContextInfo;
    }

    public void setUserContextInfo(UserContextInfo userContextInfo) {
        this.userContextInfo = userContextInfo;
    }

    public void reset() {
        userContextInfo = null;
    }
}
