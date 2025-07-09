package ai.hybrid.bot.data;

import ai.hybrid.bot.annotations.ActionValidatorInterface;
import ai.hybrid.bot.enums.BotState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserContext {
    BotState state;
    @ActionValidatorInterface(state = BotState.ACTION)
    String action;
    @ActionValidatorInterface(state = BotState.JOB)
    String job;
    @ActionValidatorInterface(state = BotState.CLUSTER)
    String cluster;
}
