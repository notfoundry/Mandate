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
    public void setupTests() {
        commandManager.register(this);
    }

    @Test
    public void testPrecedingOptionalArguments() {
        ResultCode result = commandManager.execute("execute firstOptional firstRequired secondRequired");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='Default', 4='Default', 5='secondRequired', 6='Default'",
                commandOutput.poll());
    }

    @Test
    public void testIntermediaryOptionalArguments() {
        ResultCode result = commandManager.execute("execute firstOptional firstRequired secondOptional secondRequired");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='secondOptional', 4='Default', 5='secondRequired', 6='Default'",
                commandOutput.poll());
    }

    @Test
    public void testTrailingOptionalArguments() {
        ResultCode result = commandManager.execute("execute firstOptional firstRequired secondOptional thirdOptional secondRequired fourthOptional");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals("1='firstOptional', 2='firstRequired', 3='secondOptional', 4='thirdOptional', 5='secondRequired', 6='fourthOptional'",
                commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "execute")
    public ResultCode doThing(IODescriptor io, Optional<String> arg1, String arg2, Optional<String> arg3, Optional<String> arg4, String arg5, Optional<String> arg6) {
        io.out().write(String.format("1='%s', 2='%s', 3='%s', 4='%s', 5='%s', 6='%s",
                arg1.orElse("Default"),
                arg2, arg3.orElse("Default"),
                arg4.orElse("Default"),
                arg5, arg6.orElse("Default")));
        return ResultCode.COMPLETED;
    }
}
