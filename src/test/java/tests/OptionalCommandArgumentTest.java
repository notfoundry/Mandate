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
import pw.stamina.mandate.api.annotations.Syntax;
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
    public void testPrecedingOptionalArguments() {
        Execution result = commandManager.execute("execute firstOptional firstRequired secondRequired");
        result = commandManager.execute("execute firstOptional firstRequired secondRequired");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("firstOptional, firstRequired, DEFAULT, DEFAULT, secondRequired, DEFAULT",
                commandOutput.poll());
    }

    @Test
    public void testIntermediaryOptionalArguments() {
        Execution result = commandManager.execute("execute firstOptional firstRequired secondRequired");
        result = commandManager.execute("execute firstOptional firstRequired secondOptional secondRequired");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("firstOptional, firstRequired, secondOptional, DEFAULT, secondRequired, DEFAULT",
                commandOutput.poll());
    }

    @Test
    public void testTrailingOptionalArguments() {
        Execution result = commandManager.execute("execute firstOptional firstRequired secondRequired");
        result = commandManager.execute("execute firstOptional firstRequired secondOptional thirdOptional secondRequired fourthOptional");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("firstOptional, firstRequired, secondOptional, thirdOptional, secondRequired, fourthOptional",
                commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "execute")
    public ExitCode doThing(IODescriptor io, Optional<String> arg1, String arg2, Optional<String> arg3, Optional<String> arg4, String arg5, Optional<String> arg6) {
        io.out().write(String.format("%s, %s, %s, %s, %s, %s",
                arg1.orElse("DEFAULT"),
                arg2, arg3.orElse("DEFAULT"),
                arg4.orElse("DEFAULT"),
                arg5, arg6.orElse("DEFAULT")));
        return ExitCode.SUCCESS;
    }
}
