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
import pw.stamina.mandate.internal.annotations.Length;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * @author Foundry
 */
public class ArrayArgumentTest {

    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = new UnixCommandManager(StandardInputStream.get(), commandOutput::add, commandErrors::add);

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
    public void testPassingToStringArray() {
        Execution result = commandManager.execute("run strings [foo, \"Hello World!\", baz]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[foo, Hello World!, baz]", commandOutput.poll());
    }

    @Test
    public void testPassingZeroLengthArrayAsArgument() {
        Execution result = commandManager.execute("run strings []");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[]", commandOutput.poll());
    }

    @Test
    public void testPassingToPrimitiveIntArray() {
        Execution result = commandManager.execute("run ints [100, 5000, 15000]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[100, 5000, 15000]", commandOutput.poll());
    }

    @Test
    public void testPassingToClampedLengthIntArray() {
        Execution result = commandManager.execute("run clampedints [1, 2, 3, 4, 5]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[1, 2, 3, 4, 5]", commandOutput.poll());
    }

    @Test
    public void testFailingClampedLengthArrayCheck() {
        Execution result = commandManager.execute("run clampedints [1, 2, 3]");

        Assert.assertTrue(result.result() == ExitCode.INVALID);
    }

    @Executes(tree = "strings")
    @Syntax(syntax = "run")
    public ExitCode runStrings(IODescriptor io, String[] strings) {
        io.out().write(Arrays.toString(strings));
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "ints")
    @Syntax(syntax = "run")
    public ExitCode runInts(IODescriptor io, int[] ints) {
        io.out().write(Arrays.toString(ints));
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "clampedints")
    @Syntax(syntax = "run")
    public ExitCode runClampedInts(IODescriptor io, @Length(min = 5, max = 5) int[] ints) {
        io.out().write(Arrays.toString(ints));
        return ExitCode.SUCCESS;
    }
}
