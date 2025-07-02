package ai.hybrid.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "apps")
@Data
public class AppButtonsConfig {
    private List<String> menu;
    private List<String> jobs;
    private List<String> clusters;
}