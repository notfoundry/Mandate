package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.ResultCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.internal.annotations.Executes;
import pw.stamina.mandate.internal.annotations.Syntax;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
@Syntax(syntax = "foo")
public class ClassSyntaxOverrideTest {
    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = new AnnotatedCommandManager(StandardInputStream.get(), commandOutput::add, commandErrors::add);

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setupTests() {
        commandManager.register(this);
    }

    @Test
    public void testSyntaxOverride() {
        ResultCode result = commandManager.execute("sum 125 125");

        commandErrors.forEach(System.out::println);

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals(250, commandOutput.poll());
    }

    @Test
    public void testFailedClassSyntaxUse() {
        ResultCode result = commandManager.execute("foo 200 175");

        Assert.assertTrue(result == ResultCode.INVALID);
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    public ResultCode sum(IODescriptor io, int augend, int addend) {
        io.out().write(augend + addend);
        return ResultCode.COMPLETED;
    }
}
