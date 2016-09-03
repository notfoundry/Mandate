/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Mark Johnson
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

package pw.stamina.mandate.parsing;

/**
 * @author Mark Johnson
 */
public class InputTokenizationException extends InputParsingException {

    private static final long serialVersionUID = -5169565456531864998L;

    public InputTokenizationException() {
        super();
    }

    public InputTokenizationException(final String message) {
        super(message);
    }

    public InputTokenizationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InputTokenizationException(final Throwable cause) {
        super(cause);
    }

    protected InputTokenizationException(final String message, final Throwable cause,
                                         final boolean enableSuppression,
                                         final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
