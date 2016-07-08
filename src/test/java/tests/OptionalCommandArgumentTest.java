package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

/**
 * @author Foundry
 */
public class OptionalCommandArgumentTest {
    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = new UnixCommandManager(StandardInputStream::get, () -> commandOutput::add, () -> commandErrors::add);

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
    public void testUsingFullCommandSignature() {
        Execution result = commandManager.execute("execute first \"second argument\" 100");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("first second argument 100",
                commandOutput.poll());
    }

    @Test
    public void testUsingPartialCommandSignature() {
        Execution result = commandManager.execute("execute first second");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("first second 0",
                commandOutput.poll());
    }

    @Test
    public void testUsingOnlyMandatoryArguments() {
        Execution result = commandManager.execute("execute first");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("first DEFAULT 0",
                commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "execute")
    public ExitCode doThing(IODescriptor io, String arg1, Optional<String> arg2, Optional<Integer> arg3) {
        io.out().write(String.format("%s %s %d", arg1, arg2.orElse("DEFAULT"), arg3.orElse(0)));
        return ExitCode.SUCCESS;
    }
}
