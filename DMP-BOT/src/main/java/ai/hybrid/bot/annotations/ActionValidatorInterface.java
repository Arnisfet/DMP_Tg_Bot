package ai.hybrid.bot.annotations;

import ai.hybrid.bot.enums.BotState;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ActionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionValidatorInterface {
    String message() default "Invalid option!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    BotState state();

}
