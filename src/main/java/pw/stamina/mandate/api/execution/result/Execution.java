package pw.stamina.mandate.api.execution.result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Foundry
 */
public interface Execution {
    ExitCode result();

    ExitCode result(long timeout, TimeUnit unit) throws TimeoutException;

    boolean kill();

    boolean completed();

    static Execution complete(ExitCode exitCode) {
        return new Complete(exitCode);
    }

    class Complete implements Execution {
        private final ExitCode exitCode;

        Complete(ExitCode exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        public ExitCode result() {
            return exitCode;
        }

        @Override
        public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
            return exitCode;
        }

        @Override
        public boolean kill() {
            return false;
        }

        @Override
        public boolean completed() {
            return true;
        }
    }
}
