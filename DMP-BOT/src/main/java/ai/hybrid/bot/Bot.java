package ai.hybrid.bot;


import ai.hybrid.bot.enums.BotState;
import ai.hybrid.bot.service.NavigationBarService;
import ai.hybrid.bot.service.handler.BotCommandHandler;
import ai.hybrid.bot.service.handler.CommandDispatcher;
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
    private NavigationBarService navBar;
    @Autowired
    private CommandDispatcher commandDispatcher;
    private Map<Long, BotState> stateMap = new ConcurrentHashMap<>();

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            stateMap.putIfAbsent(chatId, BotState.MAIN_MENU);

            BotCommandHandler command = commandDispatcher.getHandlerMap().get(text);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            switch (stateMap.get(chatId)) {
                case MAIN_MENU ->  {
                    message.setReplyMarkup(navBar.getMainMenu());
                    message.setText("Choose Action: ");
                    stateMap.put(chatId, BotState.JOB);
                }
                case JOB -> {
                    message.setReplyMarkup(navBar.getJobsMenu());
                    message.setText("Choose Job: ");
                    stateMap.put(chatId, BotState.CLUSTER);
                }
            }
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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
