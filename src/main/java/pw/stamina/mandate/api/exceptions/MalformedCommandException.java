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

package pw.stamina.mandate.api.exceptions;

/**
 * @author Foundry
 */
public class MalformedCommandException extends RuntimeException {

    private static final long serialVersionUID = 7123028407640145435L;

    public MalformedCommandException() {}

    public MalformedCommandException(String message) {
        super(message);
    }

    public MalformedCommandException(Throwable cause) {
        super(cause);
    }

    public MalformedCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
