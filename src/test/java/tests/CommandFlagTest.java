package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Flag;
import pw.stamina.mandate.api.annotations.Syntax;
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
    public void setup() {
        commandManager.register(this);
    }

    @Test
    public void testFlagSet() {
        Execution result = commandManager.execute("greet foo -caps");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("FOO", commandOutput.poll());
    }

    @Test
    public void testFlagUnset() {
        Execution result = commandManager.execute("greet foo");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("foo", commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "greet")
    public ExitCode doGreeting(IODescriptor io, String greeting, @Flag(value = {"caps"}, def = "false") boolean useCaps) {
        io.out().write(useCaps ? greeting.toUpperCase() : greeting);
        return ExitCode.SUCCESS;
    }
}
