package tests;

import org.junit.Test;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.exceptions.MalformedCommandException;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.internal.annotations.Executes;

/**
 * @author Foundry
 */
public class FailedClassRegistrationTest {

    private CommandManager commandManager = new AnnotatedCommandManager();;

    @Test(expected = MalformedCommandException.class)
    public void testFailedRegistration() {
        commandManager.register(this);
    }

    @Executes(tree = "greet|gr")
    public String doGreeting(String greeting) {
        return "Someone greeted you: " + greeting;
    }
}
