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
import pw.stamina.mandate.internal.annotations.Flag;
import pw.stamina.mandate.internal.annotations.Syntax;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
public class CommandFlagTest {
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
    public void testFlagSet() {
        ResultCode result = commandManager.execute("greet foo -caps");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals("FOO", commandOutput.poll());
    }

    @Test
    public void testFlagUnset() {
        ResultCode result = commandManager.execute("greet foo");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals("foo", commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "greet")
    public ResultCode doGreeting(IODescriptor io, String greeting, @Flag(value = {"caps"}, def = "false") boolean useCaps) {
        io.out().write(useCaps ? greeting.toUpperCase() : greeting);
        return ResultCode.COMPLETED;
    }
}
