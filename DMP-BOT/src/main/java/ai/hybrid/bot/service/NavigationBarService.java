package ai.hybrid.bot.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationBarService {
    List<String> mainMenuList = List.of("Start", "Stop", "Delete Checkpoint", "Restart");
    List<String> jobsMenuList = List.of("Places", "Segments", "Segments Mobile", "MMP Realtime");
    List<String> clusterMenuList = List.of("NL", "SG", "US", "RU");

    public ReplyKeyboardMarkup getMainMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(mainMenuList, 2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
    public ReplyKeyboardMarkup getJobsMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(jobsMenuList, 2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
    public ReplyKeyboardMarkup getClusterMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(clusterMenuList, 2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }

    private List<KeyboardRow> keyboardBuilder(List<String> labels, int buttonPerRow) {
        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        for (int i = 0; i < labels.size(); i += buttonPerRow) {
            KeyboardRow row = new KeyboardRow();
            for (int j = i; j < i + buttonPerRow && j < labels.size(); j++) {
                row.add(new KeyboardButton(labels.get(j)));
            }
            keyboardRowsList.add(row);
        }
        return keyboardRowsList;
    }
}
