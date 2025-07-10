package ai.hybrid.bot;

import ai.hybrid.bot.data.UserContext;
import ai.hybrid.bot.enums.BotState;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=dev")
@RunWith(SpringJUnit4ClassRunner.class)
public class ValidationTest {
    @Autowired
    Validator validator;
    @Test
    public void correctBehaviourTest() {
        UserContext context = new UserContext(BotState.ACTION, "Start", "Test1", "RU");
        Set<ConstraintViolation<UserContext>> violations = validator.validate(context);
        assertEquals(0, violations.size());
    }
    @Test
    public void incorrectActionTest() {
        UserContext context = new UserContext(BotState.ACTION, "qwerty", "gjdjf", "BJ");
        Set<ConstraintViolation<UserContext>> violations = validator.validate(context);
        Assertions.assertEquals(3, violations.size());
        for (ConstraintViolation<UserContext> violation : violations) {
            assertEquals("Invalid option!", violation.getMessage());
        }
    }
}
