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
@Syntax(syntax = {"execute", "exec", "do"})
public class ClassSyntaxInheritanceTest {

    private CommandManager commandManager = new AnnotatedCommandManager();

    @Before
    public void setupTests() {
        commandManager.register(this);
    }

    @Test
    public void testSyntaxInheritance() {
        Optional<CommandResult> result = commandManager.execute("execute sum 500 250");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("750", result.get().getResult());
    }

    @Test
    public void testAliasUsage() {
        Optional<CommandResult> result = commandManager.execute("do add 1000 500");

        Assert.assertTrue(result.isPresent());

        Assert.assertEquals("1500", result.get().getResult());
    }

    @Executes(tree = "sum|add")
    public int sum(int augend, int addend) {
        return augend + addend;
    }
}
