package ai.hybrid.bot.service.handler;

import ai.hybrid.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotCommandHandler {
    public String getCommand();
    public void handle(Update update, Bot bot) throws TelegramApiException;
}
