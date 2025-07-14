package ai.hybrid.bot.service;

import ai.hybrid.bot.data.UserContext;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MessageBuilder {
    public String validationMessageBuilder(Set<ConstraintViolation<UserContext>> violations) {
        StringBuilder builder = new StringBuilder("Incorrect states found: \n");
        violations.forEach(violation -> {
            builder.append("Property: ");
            builder.append(violation.getPropertyPath().toString());
            builder.append("; Value: ");
            builder.append(violation.getInvalidValue());
            builder.append("; Message: ");
            builder.append(violation.getMessage()).append("\n");
        });
        return builder.toString();
    }
}
