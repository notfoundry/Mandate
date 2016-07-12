package pw.stamina.mandate.api.execution.result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An Execution represents a possibly asynchronous command execution taking place
 * Methods are provided to check if the execution is
 * complete, to wait for its completion, and to retrieve the result of
 * the execution.  The result can only be retrieved using method
 * {@link #result result} when the execution has completed, blocking if
 * necessary until it is ready. Cancellation is performed by the
 * {@link #kill kill} method.
 *
 * @author Foundry
 */
public interface Execution {
    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the terminal exit code delivered by the executed command
     */
    ExitCode result();

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @return the terminal exit code delivered by the executed command
     */
    ExitCode result(long timeout, TimeUnit unit) throws TimeoutException;

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.
     *
     * @return {@code false} if the execution could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    boolean kill();

    /**
     * Returns {@code true} if this execution completed.
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this execution completed
     */
    boolean completed();

    /**
     * Returns a new Execution that is already completed with the specified exit code
     * @param exitCode the exit code that this execution should be defined as having finished with
     * @return a new Execution that is already completed with the specified exit code
     */
    static Execution complete(ExitCode exitCode) {
        return new Complete(exitCode);
    }

    /**
     * An Execution that is already completed with a specified exit code
     */
    class Complete implements Execution {
        private final ExitCode exitCode;

        Complete(ExitCode exitCode) {
            this.exitCode = exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ExitCode result() {
            return exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
            return exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean kill() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean completed() {
            return true;
        }
    }
}
