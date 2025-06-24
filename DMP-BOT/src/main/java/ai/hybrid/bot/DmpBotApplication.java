package ai.hybrid.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DmpBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmpBotApplication.class, args);
	}

	@Bean
	public Bot telegramBot(BotConfig config) throws TelegramApiException {
		Bot bot = new Bot(config);
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(bot);
		return bot;
	}
}
