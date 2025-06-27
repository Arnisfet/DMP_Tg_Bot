package ai.hybrid.bot.data;

import ai.hybrid.bot.enums.BotState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserContext {
    BotState state;
    String action;
    String job;
    String cluster;
}
