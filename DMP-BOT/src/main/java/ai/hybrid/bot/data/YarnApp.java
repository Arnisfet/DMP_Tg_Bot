package ai.hybrid.bot.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnApp {
    private String id;
    private String name;
    private String state;
}
