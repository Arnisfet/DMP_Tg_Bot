package ai.hybrid.bot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private BotConfig config;

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
            message.setText("Hello! Choose an option: ");

            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline  = new ArrayList<>();

            InlineKeyboardButton startButton = new InlineKeyboardButton();
            startButton.setText("Start");
            startButton.setCallbackData("startOpt");

            InlineKeyboardButton reStartButton = new InlineKeyboardButton();
            reStartButton.setText("Restart");
            reStartButton.setCallbackData("restartOpt");

            rowInline.add(startButton);
            rowInline.add(reStartButton);

            rowsInline.add(rowInline);

            inlineKeyboard.setKeyboard(rowsInline);
            message.setReplyMarkup(inlineKeyboard);
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
