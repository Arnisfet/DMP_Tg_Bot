package ai.hybrid.bot;


import ai.hybrid.bot.config.BotConfig;
import ai.hybrid.bot.data.UserContext;
import ai.hybrid.bot.enums.BotState;
import ai.hybrid.bot.service.NavigationBarService;
import ai.hybrid.bot.service.dispatcher.CommandDispatcher;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Bot extends TelegramLongPollingBot {
    private BotConfig config;
    @Autowired
    private NavigationBarService navBar;
    @Autowired
    private CommandDispatcher commandDispatcher;
    @Autowired
    private Validator validator;
    private Map<Long, UserContext> stateMap = new ConcurrentHashMap<>();

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            if (text.equals("/start") && !stateMap.containsKey(chatId)) {
                message.setText("👋 Hello! I’m your DMP Bot 🤖\nPlease choose an option using the buttons below ⬇️");
                executeSafe(message);
            }

            stateMap.putIfAbsent(chatId, new UserContext(BotState.MAIN_MENU, "", "", ""));
            UserContext context = stateMap.get(chatId);
            BotState currentState = context.getState();

            if (text.equals("⬅️")) {
                BotState newState = backLogicHandler(currentState, message, context);
                context.setState(newState);
                executeSafe(message);
                return;
            }

            switch (currentState) {
                case MAIN_MENU -> {
                    message.setReplyMarkup(navBar.getMainMenu());
                    message.setText("Choose Action: ");
                }
                case ACTION -> {
                    context.setAction(text);
                    message.setReplyMarkup(navBar.getJobsMenu());
                    message.setText("Choose Job: ");
                }
                case JOB -> {
                    context.setJob(text);
                    message.setReplyMarkup(navBar.getClusterMenu());
                    message.setText("Choose Cluster: ");
                }
                case CLUSTER -> {
                    context.setCluster(text);
                    var violations = validator.validate(context);
                    if (!violations.isEmpty()) {
                        message.setReplyMarkup(navBar.getMainMenu());
                        String error = validationMessageBuilder(violations);
                        message.setText(error);
                        executeSafe(message);
                        message.setText("Choose the options again.");
                        executeSafe(message);
                        context.clear();
                        return;
                    } else {
                        message.setReplyMarkup(navBar.getMainMenu());
                        message.setText("✅ All set! Your choice was: "
                                + context.getAction() + " | "
                                + context.getJob() + " | "
                                + context.getCluster());
                        commandDispatcher.commandPull(context);
                        context.clear();
                    }
                }
            }
            context.setState(stateChanger(currentState));
            executeSafe(message);
        }
    }

    public BotState stateChanger(BotState current) {
        return switch (current) {
            case MAIN_MENU -> BotState.ACTION;
            case ACTION -> BotState.JOB;
            case JOB -> BotState.CLUSTER;
            case CLUSTER -> BotState.ACTION;
        };
    }

    private void executeSafe(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private BotState backLogicHandler(BotState currentState, SendMessage message, UserContext context) {
        return switch (currentState) {
            case MAIN_MENU -> {
                message.setText("You're already at the main menu.");
                message.setReplyMarkup(navBar.getMainMenu());
                yield BotState.MAIN_MENU;
            }
            case ACTION -> {
                message.setText("You're already at the main menu.");
                message.setReplyMarkup(navBar.getMainMenu());
                yield BotState.MAIN_MENU;
            }
            case JOB -> {
                context.setAction("");
                message.setText("🔙 Back to Action");
                message.setReplyMarkup(navBar.getMainMenu());
                yield BotState.ACTION;
            }
            case CLUSTER -> {
                context.setJob("");
                message.setText("🔙 Back to Job");
                message.setReplyMarkup(navBar.getJobsMenu());
                yield BotState.JOB;
            }
        };
    }

    private String validationMessageBuilder(Set<ConstraintViolation<UserContext>> violations) {
        StringBuilder builder = new StringBuilder("Incorrect states found: \n");
        violations.forEach(violation -> {
            builder.append("Property: ");
            builder.append(violation.getPropertyPath().toString());
            builder.append("; Value: ");
            builder.append(violation.getInvalidValue());
            builder.append("; Message: ");
            builder.append(violation.getMessage()).append("\n");
        });
        return builder.toString();
    }
    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
