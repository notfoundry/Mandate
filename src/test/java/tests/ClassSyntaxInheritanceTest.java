package tests;

import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
@Syntax(syntax = {"execute", "exec", "do"})
public class ClassSyntaxInheritanceTest {
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
    public void testSyntaxInheritance() {
        Execution result = commandManager.execute("execute sum 500 250");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals(750, commandOutput.poll());
    }

    @Test
    public void testAliasUsage() {
        Execution result = commandManager.execute("do add 1000 500");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals(1500, commandOutput.poll());
    }

    @Executes(tree = "sum|add")
    public ExitCode sum(IODescriptor io, int augend, int addend) {
        io.out().write(augend + addend);
        return ExitCode.SUCCESS;
    }
}
