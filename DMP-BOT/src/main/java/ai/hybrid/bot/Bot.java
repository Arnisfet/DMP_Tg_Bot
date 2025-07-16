package ai.hybrid.bot;


import ai.hybrid.bot.authentication.Authenticator;
import ai.hybrid.bot.config.BotConfig;
import ai.hybrid.bot.data.UserContext;
import ai.hybrid.bot.enums.BotState;
import ai.hybrid.bot.service.state.BotStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Bot extends TelegramLongPollingBot {
    private BotConfig config;
    @Autowired
    private Authenticator authenticator;
    @Autowired
    private BotStateService botStateService;

    private Map<Long, UserContext> stateMap = new ConcurrentHashMap<>();

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            // Auth
            if (!authenticator.authentication(update.getMessage().getFrom().getId())) {
                SendMessage deny = new SendMessage();
                deny.setChatId(String.valueOf(chatId));
                deny.setText("🚫 You are not authorized to use this bot.");
                executeSafe(deny);
                return;
            }
            stateMap.putIfAbsent(chatId, new UserContext(BotState.INIT, "", "", ""));
            UserContext context = stateMap.get(chatId);
            BotState currentState = context.getState();

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            if (currentState == BotState.INIT){
                message.setText("👋 Hello! I’m your DMP Bot 🤖\nPlease choose an option using the buttons below ⬇️");
                executeSafe(message);
            }

            //In case of back symbol
            if (text.equals("⬅️")) {
                BotState newState = botStateService.backLogicHandler(currentState, message, context);
                context.setState(newState);
                executeSafe(message);
                return;
            }
            // The main service of statement logic
            message = botStateService.launchStateHandler(currentState, message, context, text);
            // In case the handler finished exec sequence
            if (context.getState() == null) {
                currentState = BotState.INIT;
            }
            context.setState(currentState.next());
            executeSafe(message);
        }
    }

    private void executeSafe(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
