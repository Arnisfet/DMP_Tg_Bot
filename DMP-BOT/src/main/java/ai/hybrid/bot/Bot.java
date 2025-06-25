package ai.hybrid.bot;


import ai.hybrid.bot.service.NavigationBarService;
import ai.hybrid.bot.service.handler.BotCommandHandler;
import ai.hybrid.bot.service.handler.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private BotConfig config;
    @Autowired
    private NavigationBarService navBar;
    @Autowired
    private CommandDispatcher commandDispatcher;

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            BotCommandHandler command = commandDispatcher.getHandlerMap().get(text);
            if (command == null) {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText("Hello! Choose an option: ");

                message.setReplyMarkup(navBar.getMainMenu());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    command.handle(update, this);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
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
