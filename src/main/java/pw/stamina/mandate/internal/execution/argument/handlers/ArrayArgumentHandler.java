package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.internal.annotations.Length;
import pw.stamina.mandate.internal.exceptions.ArgumentParseException;
import pw.stamina.mandate.internal.execution.argument.BaseCommandArgument;
import pw.stamina.parsor.exceptions.ParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Foundry
 */
public final class ArrayArgumentHandler implements ArgumentHandler<Object> {

    @Override
    public Object parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        @SuppressWarnings("unchecked")
        Optional<ArgumentHandler> handlerLookup = commandManager.findArgumentHandler((Class) parameter.getType().getComponentType());
        if (!handlerLookup.isPresent()) throw new ArgumentParseException("", parameter.getType().getComponentType(), "");

        List<String> rawComponents = new ArrayList<>();
        StringBuilder rawComponent = new StringBuilder();

        char[] inputChars; boolean escaped = false, quoted = false;
        for (int idx = 1; idx < (inputChars = input.getRaw().toCharArray()).length - 1; idx++) {
            if (escaped) {
                rawComponent.append(inputChars[idx]);
                escaped = false;
            } else {
                switch (inputChars[idx]) {
                    case '\\': {
                        escaped = true;
                        break;
                    }
                    case '"': {
                        quoted = !quoted;
                        break;
                    }
                    case ',': {
                        if (!quoted) {
                            while (inputChars[idx+1] == ' ') {
                                idx++;
                            }
                            if (rawComponent.length() > 0) {
                                rawComponents.add(rawComponent.toString());
                                rawComponent.setLength(0);
                            }
                            break;
                        }
                    }
                    case ' ': {
                        if (!quoted) {
                            if (inputChars[idx - 1] != ' ' && inputChars[idx - 1] != ',') {
                                throw new ArgumentParseException("", parameter.getType().getComponentType(), "");
                            } else {
                                idx++;
                                break;
                            }
                        }
                    }
                    default: {
                        rawComponent.append(inputChars[idx]);
                        escaped = false;
                    }
                }
            }

        }
        if (rawComponent.length() > 0) {
            rawComponents.add(rawComponent.toString());
            rawComponent.setLength(0);
        }

        Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            int min = Math.min(length.min(), length.max());
            int max = Math.max(length.min(), length.max());
            if (rawComponents.size() < min) {
                throw new ArgumentParseException(input.getRaw(), parameter.getType(), String.format("'%s' is too short: length can be between %s-%s elements", input.getRaw(), min, max));
            } else if (rawComponents.size() > max) {
                throw new ArgumentParseException(input.getRaw(), parameter.getType(), String.format("'%s' is too long: length can be between %s-%s elements", input.getRaw(), min, max));
            }
        }

        Object resultArray = Array.newInstance(parameter.getType().getComponentType(), rawComponents.size());
        for (int i = 0; i < rawComponents.size(); i++) {
            Array.set(resultArray, i, handlerLookup.get().parse(new BaseCommandArgument(rawComponents.get(i)), new ArrayProxyCommandParameter(parameter), commandManager));
        }
        return resultArray;
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        return parameter.getLabel() + " - " + " array of " + parameter.getType().getSimpleName();
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Object[].class};
    }

    private static class ArrayProxyCommandParameter implements CommandParameter {
        private final CommandParameter backingParameter;

        ArrayProxyCommandParameter(CommandParameter backingParameter) {
            this.backingParameter = backingParameter;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
            return backingParameter.getAnnotation(annotationClass);
        }

        @Override
        public Annotation[] getAnnotations() {
            return backingParameter.getAnnotations();
        }

        @Override
        public Class<?> getType() {
            return backingParameter.getType().getComponentType();
        }

        @Override
        public boolean isOptional() {
            return backingParameter.isOptional();
        }

        @Override
        public String getDescription() {
            return "An element in an array described as \"" + backingParameter.getDescription() + "\"";
        }

        @Override
        public String getLabel() {
            return backingParameter.getLabel();
        }
    }

}

