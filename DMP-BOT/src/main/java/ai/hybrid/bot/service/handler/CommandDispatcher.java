package ai.hybrid.bot.service.handler;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
public class CommandDispatcher {
    private final Map<String, BotCommandHandler> handlerMap;

    public CommandDispatcher(List<BotCommandHandler> handlers) {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        BotCommandHandler::getCommand,
                        command -> command));
    }

}
