package ai.hybrid.bot.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Service
public class NavigationBarService {
    public ReplyKeyboardMarkup getMainMenu() {
        KeyboardButton startButton = KeyboardButton.builder().text("Start").build();
        KeyboardButton stopButton = KeyboardButton.builder().text("Stop").build();
        KeyboardButton deleteButton = KeyboardButton.builder().text("Delete Checkpoint").build();
        KeyboardButton restartButton = KeyboardButton.builder().text("Restart").build();

        List<KeyboardRow> keyboard = List.of(
                new KeyboardRow(List.of(startButton, stopButton)),
                new KeyboardRow(List.of(restartButton, deleteButton))
        );

        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
    public ReplyKeyboardMarkup getJobsMenu() {
        KeyboardButton startButton = KeyboardButton.builder().text("Places").build();
        KeyboardButton stopButton = KeyboardButton.builder().text("Segments").build();
        KeyboardButton deleteButton = KeyboardButton.builder().text("Segments Mobile").build();
        KeyboardButton restartButton = KeyboardButton.builder().text("MMP Realtime").build();

        List<KeyboardRow> keyboard = List.of(
                new KeyboardRow(List.of(startButton, stopButton)),
                new KeyboardRow(List.of(restartButton, deleteButton))
        );

        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
}
