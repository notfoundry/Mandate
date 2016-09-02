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

package pw.stamina.mandate.internal.syntax;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.syntax.CommandRegistry;
import pw.stamina.mandate.syntax.ExecutableLookup;
import pw.stamina.mandate.syntax.SyntaxComponent;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Foundry
 */
public class ForkedCommandRegistry implements CommandRegistry {

    private final CommandRegistry forkedRegistry;

    private final CommandRegistry thisRegistry;

    public ForkedCommandRegistry(final CommandRegistry forkedRegistry, final CommandRegistry thisRegistry) {
        this.forkedRegistry = forkedRegistry;
        this.thisRegistry = thisRegistry;
    }

    @Override
    public Set<SyntaxComponent> getCommands() {
        final Set<SyntaxComponent> componentSet = new HashSet<>();
        componentSet.addAll(thisRegistry.getCommands());
        componentSet.addAll(forkedRegistry.getCommands());
        return componentSet;
    }

    @Override
    public void addCommand(final SyntaxComponent component) {
        thisRegistry.addCommand(component);
    }

    @Override
    public ExecutableLookup findExecutable(final Deque<CommandArgument> syntaxComponents, final ExecutionContext executionContext) {
        final ExecutableLookup lookup = thisRegistry.findExecutable(syntaxComponents, executionContext);
        return lookup.wasSuccessful() ? lookup : forkedRegistry.findExecutable(syntaxComponents, executionContext);
    }

    @Override
    public boolean commandPresent(final String command) {
        return thisRegistry.commandPresent(command) || forkedRegistry.commandPresent(command);
    }
}
