package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.impl.parsers.standards.EnumParser;

import java.util.Arrays;

/**
 * @author Foundry
 */
public final class EnumArgumentHandler implements ArgumentHandler<Enum> {

    @Override
    public Enum parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        @SuppressWarnings("unchecked")
        EnumParser<? extends Enum> parser = new EnumParser(parameter.getType());
        return parser.parse(input.getRaw());
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        return parameter.getLabel() + " - " + "one of " + Arrays.toString(parameter.getType().getEnumConstants());
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Enum.class};
    }

}

