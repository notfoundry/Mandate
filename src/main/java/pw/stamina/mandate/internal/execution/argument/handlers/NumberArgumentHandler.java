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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.internal.annotations.numeric.IntClamp;
import pw.stamina.mandate.internal.annotations.numeric.RealClamp;
import pw.stamina.mandate.internal.utils.Primitives;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.impl.parsers.standards.NumberParser;

import java.util.regex.Pattern;

/**
 * @author Foundry
 */
public final class NumberArgumentHandler implements ArgumentHandler<Number> {
    private static final Pattern hexValidator = Pattern.compile("^(-|\\+)?(0x|0X|#)[a-fA-F0-9]+$"); //require hex prefix

    @Override
    public final Number parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        Class<? extends Number> numberClass; NumberParser parser; Number resultNumber = (parser = new NumberParser((numberClass = Primitives.wrap(parameter.getType())))).parse(input.getArgument());

        if (numberClass == Float.class || numberClass == Double.class) {
            final RealClamp realClamp = parameter.getAnnotation(RealClamp.class);
            if (realClamp != null) {
                double min = Math.min(realClamp.min(), realClamp.max());
                double max = Math.max(realClamp.min(), realClamp.max());
                if (!Double.isNaN(min) && resultNumber.doubleValue() < min) {
                    resultNumber = (numberClass == Float.class ? (float) min : min);
                } else if (!Double.isNaN(max) && resultNumber.doubleValue() > max) {
                    resultNumber = (numberClass == Float.class ? (float) max : max);
                }
            }
        } else {
            final IntClamp intClamp = parameter.getAnnotation(IntClamp.class);
            if (intClamp != null) {
                Long min, max;
                if (resultNumber.longValue() < (min = Math.min(intClamp.min(), intClamp.max()))) {
                    resultNumber = parser.parse(min.toString());
                } else if (resultNumber.longValue() > (max = (Math.max(intClamp.min(), intClamp.max())))) {
                    resultNumber = parser.parse(max.toString());
                }
            }
        }
        return resultNumber;
    }

    @Override
    public final String getSyntax(CommandParameter parameter) {
        Class<? extends Number> numberClass; String suffix, prefix = (numberClass = Primitives.wrap(parameter.getType())).getSimpleName();
        if (numberClass == Float.class || numberClass == Double.class) {
            RealClamp realClamp = parameter.getAnnotation(RealClamp.class);
            if (realClamp == null) {
                return parameter.getLabel() + " - " + prefix;
            }
            double min = Math.min(realClamp.min(), realClamp.max());
            double max = Math.max(realClamp.min(), realClamp.max());
            suffix = !Double.isNaN(min) && !Double.isNaN(max) ? String.format("%s-%s", min, max) : (!Double.isNaN(min) ? ">" + min : (!Double.isNaN(max) ? "<" + max : "?"));
        } else {
            IntClamp intClamp = parameter.getAnnotation(IntClamp.class);
            if (intClamp == null) {
                return parameter.getLabel() + " - " + prefix;
            }
            long min = Math.min(intClamp.min(), intClamp.max());
            long max = Math.max(intClamp.min(), intClamp.max());
            suffix = Long.MIN_VALUE != min && Long.MAX_VALUE != max ? String.format("%s-%s", min, max) : (Long.MIN_VALUE != min ? ">" + min : (Long.MAX_VALUE != max ? "<" + max : "?"));
        }
        return parameter.getLabel() + " - " + String.format("%s[%s]", prefix, suffix);
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Number.class};
    }
}
