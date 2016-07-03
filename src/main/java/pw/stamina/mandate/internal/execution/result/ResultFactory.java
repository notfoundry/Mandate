/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Foundry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pw.stamina.mandate.internal.execution.result;

import pw.stamina.mandate.api.execution.result.CommandResult;

/**
 * @author Foundry
 */
public final class ResultFactory {
    private ResultFactory() {}

    public static CommandResult immediate(String resultText, CommandResult.Status resultStatus) {
        return new ImmediateCommandResult(resultText, resultStatus);
    }

    private static class ImmediateCommandResult implements CommandResult {
        private final String result;
        private final Status status;

        ImmediateCommandResult(String result, Status status) {
            this.result = result;
            this.status = status;
        }

        @Override
        public String getResult() {
            return result;
        }

        @Override
        public Status getStatus() {
            return status;
        }
    }
}
