package ai.hybrid.bot.service.state;

import ai.hybrid.bot.data.UserContext;
import ai.hybrid.bot.enums.BotState;
import ai.hybrid.bot.service.MessageBuilder;
import ai.hybrid.bot.service.NavigationBarService;
import ai.hybrid.bot.service.dispatcher.CommandDispatcher;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class BotLaunchService {
    @Autowired
    private NavigationBarService navBar;
    @Autowired
    private Validator validator;
    @Autowired
    private CommandDispatcher commandDispatcher;
    @Autowired
    MessageBuilder messageBuilder;

    public SendMessage launchStateHandler(BotState currentState, SendMessage message, UserContext context, String text) {
        return switch (currentState) {
            case INIT -> null;
            case MAIN_MENU -> actionHandler(context, message, text);
            case ACTION -> jobHandler(context, message, text);
            case JOB -> clusterHandler(context, message, text);
            case CLUSTER -> execution(context, message, text);
            case HEALTH_INIT -> null;
            case HEALTH_OPTION -> null;
            case HEALTH_RESULT -> null;
        };
    }

    public SendMessage actionHandler(UserContext context, SendMessage message, String text) {
        context.setAction(text);
        message.setReplyMarkup(navBar.getActions());
        message.setText("Choose Action: ");
        return message;
    }
    public SendMessage jobHandler(UserContext context, SendMessage message, String text) {
        context.setJob(text);
        message.setReplyMarkup(navBar.getJobsMenu());
        message.setText("Choose job: ");
        return message;
    }

    public SendMessage clusterHandler(UserContext context, SendMessage message, String text) {
        context.setJob(text);
        message.setReplyMarkup(navBar.getClusterMenu());
        message.setText("Choose cluster: ");
        return message;
    }
    public SendMessage execution(UserContext context, SendMessage message, String text) {
        context.setCluster(text);
        var violations = validator.validate(context);
        if (!violations.isEmpty()) {
            message.setReplyMarkup(navBar.getMainMenu());
            String error = messageBuilder.validationMessageBuilder(violations);
            message.setText(error);
            context.clear();
        } else {
            message.setReplyMarkup(navBar.getMainMenu());
            message.setText("✅ All set! Your choice was: "
                    + context.getAction() + " | "
                    + context.getJob() + " | "
                    + context.getCluster());
            commandDispatcher.commandPull(context);
            context.clear();
        }
        return message;
    }
}
