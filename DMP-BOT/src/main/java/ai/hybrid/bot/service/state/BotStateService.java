package ai.hybrid.bot.service.state;

import ai.hybrid.bot.data.UserContext;
import ai.hybrid.bot.enums.BotState;
import ai.hybrid.bot.service.NavigationBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class BotStateService {
    @Autowired
    private NavigationBarService navBar;
    @Autowired
    BotLaunchService botLaunchService;
    @Autowired
    BotHealthService botHealthService;

    public SendMessage launchStateHandler(BotState currentState, SendMessage message, UserContext context, String text) {
        return switch (currentState) {
            case INIT -> mainMenuHandler(message);
            case MAIN_MENU -> routeByMainMenuChoice(context, message, text);

            case CLUSTER, JOB, ACTION -> botLaunchService.launchStateHandler(currentState, message, context, text);

            case HEALTH_INIT -> null;
            case HEALTH_OPTION -> null;
            case HEALTH_RESULT -> null;
        };
    }

    private SendMessage routeByMainMenuChoice(UserContext context, SendMessage message, String text) {
        if (text.equalsIgnoreCase("Launch")) {
            context.setState(BotState.MAIN_MENU);
            return botLaunchService.launchMenuHandler(context, message, text);

        } else if (text.equalsIgnoreCase("Health Check")) {
            context.setState(BotState.HEALTH_INIT);
            return botHealthService.actionHandler();

        } else {
            message.setText("❓ Unknown command. Please choose an option.");
            message.setReplyMarkup(navBar.getMainMenu());
            return message;
        }
    }
    public SendMessage mainMenuHandler(SendMessage message) {
        message.setReplyMarkup(navBar.getMainMenu());
        message.setText("Choose what you want to do: ");
        return message;
    }

    public BotState backLogicHandler(BotState currentState, SendMessage message, UserContext context) {
        return switch (currentState) {
            case INIT -> null;
            case MAIN_MENU -> {
                message.setText("You're already at the main menu.");
                yield BotState.MAIN_MENU;
            }
            case ACTION -> {
                message.setText("Choose the option:");
                message.setReplyMarkup(navBar.getMainMenu());
                yield BotState.MAIN_MENU;
            }
            case JOB -> {
                context.setAction("");
                message.setText("🔙 Back to Action");
                message.setReplyMarkup(navBar.getActions());
                yield BotState.ACTION;
            }
            case CLUSTER -> {
                context.setJob("");
                message.setText("🔙 Back to Job");
                message.setReplyMarkup(navBar.getJobsMenu());
                yield BotState.JOB;
            }
            case HEALTH_INIT -> null;
            case HEALTH_OPTION -> null;
            case HEALTH_RESULT -> null;
        };
    }
}
