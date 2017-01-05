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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.annotations.numeric.IntClamp;
import pw.stamina.mandate.annotations.numeric.PreciseClamp;
import pw.stamina.mandate.annotations.numeric.RealClamp;
import pw.stamina.mandate.internal.utils.Primitives;
import pw.stamina.mandate.parsing.InputParsingException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Mark Johnson
 */
public final class NumberArgumentHandler implements ArgumentHandler<Number> {

    private static final Predicate<String> HEX_VALIDATOR_PREDICATE = Pattern.compile("^(-|\\+)?(0x|0X|#)[a-fA-F0-9]+$").asPredicate();

    private static final Predicate<String> OCTAL_VALIDATOR_PREDICATE = Pattern.compile("^(-|\\+)?0[1-7][0-7]*$").asPredicate();

    private static final Map<Class<? extends Number>, Function<String, ? extends Number>> NUMBER_DECODERS;

    private static final Map<Class<? extends Number>, Function<Number, Function<CommandParameter, Number>>> POST_PROCESSORS;

    @Override
    public final Number parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        @SuppressWarnings("unchecked")
        final Class<? extends Number> numberClass = (Class<? extends Number>) Primitives.wrap(parameter.getType());
        final Number resultNumber = parseNumber(input.getRaw(), numberClass);
        return POST_PROCESSORS.get(numberClass).apply(resultNumber).apply(parameter);
    }

    @Override
    public final String getSyntax(final CommandParameter parameter) {
        @SuppressWarnings("unchecked")
        final Class<? extends Number> numberClass = Primitives.wrap((Class<? extends Number>) parameter.getType());
        final String prefix = numberClass.getSimpleName();
        final String suffix;

        final IntClamp intClamp = parameter.getAnnotation(IntClamp.class);
        if (intClamp == null) {
            final RealClamp realClamp = parameter.getAnnotation(RealClamp.class);
            if (realClamp == null) {
                return parameter.getLabel() + " - " + prefix;
            } else {
                final double min = Math.min(realClamp.min(), realClamp.max());
                final double max = Math.max(realClamp.min(), realClamp.max());
                suffix = !Double.isNaN(min) && !Double.isNaN(max) ? String.format("%s-%s", min, max) : (!Double.isNaN(min) ? ">" + min : (!Double.isNaN(max) ? "<" + max : "?"));
            }
        } else {
            final long min = Math.min(intClamp.min(), intClamp.max());
            final long max = Math.max(intClamp.min(), intClamp.max());
            suffix = Long.MIN_VALUE != min && Long.MAX_VALUE != max ? String.format("%s-%s", min, max) : (Long.MIN_VALUE != min ? ">" + min : (Long.MAX_VALUE != max ? "<" + max : "?"));
        }

        return parameter.getLabel() + " - " + String.format("%s[%s]", prefix, suffix);
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Number.class};
    }

    private static <R extends Number> R parseNumber(final String input, final Class<R> numberClass) throws InputParsingException {
        try {
            final Function<String, ? extends Number> decoder = NUMBER_DECODERS.get(numberClass);
            return numberClass.cast(decoder.apply(input));
        } catch (final NumberFormatException e) {
            throw new InputParsingException(String.format("'%s' cannot be parsed to a(n) %s", input, numberClass.getCanonicalName()), e);
        }
    }

    private static Double decodeDouble(final String input) throws NumberFormatException {
        return (HEX_VALIDATOR_PREDICATE.test(input) || OCTAL_VALIDATOR_PREDICATE.test(input))
                ? Long.decode(input).doubleValue()
                : Double.valueOf(input);
    }

    private static Float decodeFloat(final String input) throws NumberFormatException {
        return (HEX_VALIDATOR_PREDICATE.test(input) || OCTAL_VALIDATOR_PREDICATE.test(input))
                ? Integer.decode(input).floatValue()
                : Float.valueOf(input);
    }

    private static BigInteger decodeBigInteger(final String input) throws NumberFormatException {
        return HEX_VALIDATOR_PREDICATE.test(input)
                ? new BigInteger((input.charAt(0) == '0' ? input.substring(2) : input.substring(1)), 16)
                : new BigInteger(input, (OCTAL_VALIDATOR_PREDICATE.test(input) ? 8 : 10));
    }

    private static BigDecimal decodeBigDecimal(final String input) throws NumberFormatException {
        return HEX_VALIDATOR_PREDICATE.test(input)
                ? new BigDecimal(new BigInteger((input.charAt(0) == '0' ? input.substring(2) : input.substring(1)), 16))
                : (OCTAL_VALIDATOR_PREDICATE.test(input)
                ? new BigDecimal(new BigInteger(input, 8))
                : new BigDecimal(input));
    }

    private static Number clampInteger(final IntClamp intClamp, final Number num, final Function<Number, Number> castingFunction) {
        final long min, max;
        if (num.longValue() < (min = Math.min(intClamp.min(), intClamp.max()))) {
            return castingFunction.apply(min);
        } else if (num.longValue() > (max = (Math.max(intClamp.min(), intClamp.max())))) {
            return castingFunction.apply(max);
        } else {
            return num;
        }
    }

    private static Number clampReal(final RealClamp realClamp, final Number num, final Function<Number, Number> castingFunction) {
        final double min, max;
        if (!Double.isNaN((min = Math.min(realClamp.min(), realClamp.max()))) && num.doubleValue() < min) {
            return castingFunction.apply(min);
        } else if (!Double.isNaN((max = Math.max(realClamp.min(), realClamp.max()))) && num.doubleValue() > max) {
            return castingFunction.apply(max);
        } else {
            return num;
        }
    }

    private static BigInteger clampBigInteger(final PreciseClamp preciseClamp, final BigInteger num) {
        final BigInteger min = !preciseClamp.min().isEmpty() ? decodeBigInteger(preciseClamp.min()) : null;
        final BigInteger max = !preciseClamp.max().isEmpty() ? decodeBigInteger(preciseClamp.max()) : null;
        if (min != null) {
            final BigInteger absMin = (max != null) ? min.min(max) : min;
            if (num.compareTo(absMin) < 0) {
                return absMin;
            }
        }
        if (max != null) {
            final BigInteger absMax = (min != null) ? max.max(min) : max;
            if (num.compareTo(absMax) > 0) {
                return absMax;
            }
        }
        return num;
    }

    private static BigDecimal clampBigDecimal(final PreciseClamp preciseClamp, final BigDecimal num) {
        final BigDecimal min = !preciseClamp.min().isEmpty() ? decodeBigDecimal(preciseClamp.min()) : null;
        final BigDecimal max = !preciseClamp.max().isEmpty() ? decodeBigDecimal(preciseClamp.max()) : null;
        if (min != null) {
            final BigDecimal absMin = (max != null) ? min.min(max) : min;
            if (num.compareTo(absMin) < 0) {
                return absMin;
            }
        }
        if (max != null) {
            final BigDecimal absMax = (min != null) ? max.max(min) : max;
            if (num.compareTo(absMax) > 0) {
                return absMax;
            }
        }
        return num;
    }

    static {
        final Map<Class<? extends Number>, Function<String, ? extends Number>> decoders = new HashMap<>();
        decoders.put(Byte.class, Byte::decode);
        decoders.put(Short.class, Short::decode);
        decoders.put(Integer.class, Integer::decode);
        decoders.put(Long.class, Long::decode);
        decoders.put(Double.class, NumberArgumentHandler::decodeDouble);
        decoders.put(Float.class, NumberArgumentHandler::decodeFloat);
        decoders.put(BigInteger.class, NumberArgumentHandler::decodeBigInteger);
        decoders.put(BigDecimal.class, NumberArgumentHandler::decodeBigDecimal);
        NUMBER_DECODERS = Collections.unmodifiableMap(decoders);

        final Function<Function<Number, Number>, Function<Number, Function<CommandParameter, Number>>> intProcessor = cast -> num -> param -> {
            final IntClamp intClamp = param.getAnnotation(IntClamp.class);
            if (intClamp != null) {
                return clampInteger(intClamp, num, cast);
            } else {
                final RealClamp realClamp = param.getAnnotation(RealClamp.class);
                if (realClamp != null) {
                    return clampReal(realClamp, num, cast);
                } else {
                    return num;
                }
            }
        };

        final Function<Function<Number, Number>, Function<Number, Function<CommandParameter, Number>>> realProcessor = cast -> num -> param -> {
            final RealClamp realClamp = param.getAnnotation(RealClamp.class);
            if (realClamp != null) {
                return clampReal(realClamp, num, cast);
            } else {
                final IntClamp intClamp = param.getAnnotation(IntClamp.class);
                if (intClamp != null) {
                    return clampInteger(intClamp, num, cast);
                } else {
                    return num;
                }
            }
        };

        final Map<Class<? extends Number>, Function<Number, Function<CommandParameter, Number>>> processingStages = new HashMap<>();
        processingStages.put(Byte.class, intProcessor.apply(Number::byteValue));
        processingStages.put(Short.class, intProcessor.apply(Number::shortValue));
        processingStages.put(Integer.class, intProcessor.apply(Number::intValue));
        processingStages.put(Long.class, intProcessor.apply(Number::longValue));
        processingStages.put(Double.class, realProcessor.apply(Number::doubleValue));
        processingStages.put(Float.class, realProcessor.apply(Number::floatValue));
        processingStages.put(BigInteger.class, num -> param -> {
            final PreciseClamp preciseClamp = param.getAnnotation(PreciseClamp.class);
            if (preciseClamp != null) {
                return clampBigInteger(preciseClamp, (BigInteger) num);
            } else {
                return intProcessor.apply(Function.identity()).apply(num).apply(param);
            }
        });
        processingStages.put(BigDecimal.class, num -> param -> {
            final PreciseClamp preciseClamp = param.getAnnotation(PreciseClamp.class);
            if (preciseClamp != null) {
                return clampBigDecimal(preciseClamp, (BigDecimal) num);
            } else {
                return realProcessor.apply(Function.identity()).apply(num).apply(param);
            }
        });
        POST_PROCESSORS = Collections.unmodifiableMap(processingStages);
    }
}
