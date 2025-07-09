package ai.hybrid.bot.service;

import ai.hybrid.bot.config.AppButtonsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationBarService {
    @Autowired
    AppButtonsConfig buttons;
    public ReplyKeyboardMarkup getMainMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(buttons.getActions(), 2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
    public ReplyKeyboardMarkup getJobsMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(buttons.getJobs(), 2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }
    public ReplyKeyboardMarkup getClusterMenu() {
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(keyboardBuilder(buttons.getClusters(), 2))
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
