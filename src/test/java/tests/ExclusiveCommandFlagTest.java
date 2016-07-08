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
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
public class ExclusiveCommandFlagTest {

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
    public void testExclusiveFlagSet() {
        Execution result = commandManager.execute("run --flag1 --flag2");

        Assert.assertTrue(result.result() == ExitCode.INVALID);
    }

    @Test
    public void testIdenticalFlagSet() {
        Execution result = commandManager.execute("run --flag2 --flag2");

        Assert.assertTrue(result.result() == ExitCode.INVALID);
    }

    @Test
    public void testSuccessfulFlagSet() {
        Execution result = commandManager.execute("run -f1");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);
    }

    @Executes
    @Syntax(syntax = "run")
    public ExitCode run(IODescriptor io,
                        @AutoFlag(flag = {"f1", "-flag1"}, xor = {"f2", "-flag2"}) boolean flag1,
                        @AutoFlag(flag = {"f2", "-flag2"}, xor = {"f1", "-flag1"}) boolean flag2) {
        return ExitCode.SUCCESS;
    }
}
