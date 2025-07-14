package ai.hybrid.bot.authentication;

import ai.hybrid.bot.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Authenticator {
    @Autowired
    AuthConfig config;
    public boolean authentication(Long userId) {
        return config.getUsers().contains(userId);
    }
}
