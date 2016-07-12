package tests;

import org.junit.Test;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.internal.execution.executable.UnsupportedParameterException;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;

/**
 * @author Foundry
 */
public class OverlappingCommandFlagTest {

    private CommandManager commandManager = new UnixCommandManager();

    @Test(expected = UnsupportedParameterException.class)
    public void testFailedFlagUse() {
        commandManager.register(this);
    }

    @Executes
    @Syntax(syntax = "run")
    public ExitCode run(IODescriptor io,
                               @AutoFlag(flag = {"f", "flag1"}) boolean flag1,
                               @AutoFlag(flag = {"f", "flag2"}) boolean flag2) {
        return ExitCode.SUCCESS;
    }
}
