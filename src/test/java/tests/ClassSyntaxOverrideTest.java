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
@Syntax(syntax = "foo")
public class ClassSyntaxOverrideTest {

    private CommandManager commandManager = new AnnotatedCommandManager();;

    @Before
    public void setupTests() {
        commandManager.register(this);
    }

    @Test
    public void testSyntaxOverride() {
        Optional<CommandResult> result = commandManager.execute("sum 125 125");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("250", result.get().getResult());
    }

    @Test
    public void testFailedClassSyntaxUse() {
        Optional<CommandResult> result = commandManager.execute("foo 200 175");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals(CommandResult.Status.FAILED, result.get().getStatus());
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    public int sum(int augend, int addend) {
        return augend + addend;
    }
}
