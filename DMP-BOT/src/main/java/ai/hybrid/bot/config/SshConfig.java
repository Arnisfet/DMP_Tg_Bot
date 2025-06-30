package ai.hybrid.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "ssh")
public class SshConfig {
    private Map<String, ClusterConfig> clusters;
    @Data
    public static class ClusterConfig {
        private String host;
        private String user;
        private String privateKey;
        private String passphrase;
        private String logfile;
        private Map<String, String> scripts;
    }
}
