package ai.hybrid.bot.annotations;

import ai.hybrid.bot.config.AppButtonsConfig;
import ai.hybrid.bot.enums.BotState;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ActionValidator implements ConstraintValidator<ActionValidatorInterface, String> {
    @Autowired
    private final AppButtonsConfig appButtonsConfig;
    private BotState state;
    public ActionValidator(AppButtonsConfig appButtonsConfig) {
        this.appButtonsConfig = appButtonsConfig;
    }
    @Override
    public void initialize(ActionValidatorInterface annotation) {
        this.state = annotation.state();
    }

    /**
     * @param value
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return switch(state) {
            case INIT -> false;
            case MAIN_MENU -> appButtonsConfig.getMenu().contains(value);
            case ACTION -> appButtonsConfig.getActions().contains(value);
            case JOB -> appButtonsConfig.getJobs().contains(value);
            case CLUSTER -> appButtonsConfig.getClusters().contains(value);
            case HEALTH_INIT -> false;
            case HEALTH_OPTION -> false;
            case HEALTH_RESULT -> false;
        };
    }
}
