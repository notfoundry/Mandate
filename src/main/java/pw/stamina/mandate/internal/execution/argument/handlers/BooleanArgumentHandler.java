package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.impl.parsers.singletons.BooleanParser;

/**
 * @author Foundry
 */
public final class BooleanArgumentHandler implements ArgumentHandler<Boolean> {

    @Override
    public Boolean parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        return BooleanParser.get().parse(input.getRaw());
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        return parameter.getLabel() + " - " + "Boolean";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Boolean.class};
    }

}

