package ai.hybrid.bot.service.handler;

import ai.hybrid.bot.Bot;
import ai.hybrid.bot.service.NavigationBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class StartCommand implements BotCommandHandler{
    @Autowired
    NavigationBarService navigationBarService;

    @Override
    public String getCommand() {
        return "Start";
    }

    @Override
    public void handle(Update update, Bot bot) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        SendMessage msg = SendMessage.builder()
                .text("Choose next option")
                .chatId(chatId)
                .build();
        msg.setReplyMarkup(navigationBarService.getJobsMenu());
        bot.execute(msg);
    }
}
