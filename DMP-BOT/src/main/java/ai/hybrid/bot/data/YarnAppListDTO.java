package ai.hybrid.bot.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnAppListDTO {
    private AppContainer apps;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppContainer {
        private List<YarnApp> app;
    }
}
