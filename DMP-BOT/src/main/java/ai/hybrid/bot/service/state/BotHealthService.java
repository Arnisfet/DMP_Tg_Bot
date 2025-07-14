package ai.hybrid.bot.service.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class BotHealthService {
    public SendMessage actionHandler() {
        return new SendMessage();
    }
}
