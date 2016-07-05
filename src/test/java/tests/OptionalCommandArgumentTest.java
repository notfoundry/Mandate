package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.CommandResult;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.internal.annotations.Executes;
import pw.stamina.mandate.internal.annotations.Syntax;

import java.util.Optional;

/**
 * @author Foundry
 */
public class OptionalCommandArgumentTest {

    private CommandManager commandManager = new AnnotatedCommandManager();;

    @Before
    public void setupTests() {
        commandManager.register(this);
    }

    @Test
    public void testPrecedingOptionalArguments() {
        Optional<CommandResult> result = commandManager.execute("execute firstOptional firstRequired secondRequired");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='Default', 4='Default', 5='secondRequired', 6='Default'",
                result.get().getResult());
    }

    @Test
    public void testIntermediaryOptionalArguments() {
        Optional<CommandResult> result = commandManager.execute("execute firstOptional firstRequired secondOptional secondRequired");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='secondOptional', 4='Default', 5='secondRequired', 6='Default'",
                result.get().getResult());
    }

    @Test
    public void testTrailingOptionalArguments() {
        Optional<CommandResult> result = commandManager.execute("execute firstOptional firstRequired secondOptional thirdOptional secondRequired fourthOptional");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='secondOptional', 4='thirdOptional', 5='secondRequired', 6='fourthOptional'",
                result.get().getResult());
    }

    @Executes
    @Syntax(syntax = "execute")
    public String doThing(Optional<String> arg1, String arg2, Optional<String> arg3, Optional<String> arg4, String arg5, Optional<String> arg6) {
        return String.format("1='%s', 2='%s', 3='%s', 4='%s', 5='%s', 6='%s",
                arg1.orElse("Default"),
                arg2, arg3.orElse("Default"),
                arg4.orElse("Default"),
                arg5, arg6.orElse("Default"));
    }
}
