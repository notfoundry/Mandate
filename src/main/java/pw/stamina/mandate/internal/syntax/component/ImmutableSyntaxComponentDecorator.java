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

package pw.stamina.mandate.internal.syntax.component;

import pw.stamina.mandate.syntax.SyntaxComponent;
import pw.stamina.mandate.execution.executable.CommandExecutable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Foundry
 */
public class ImmutableSyntaxComponentDecorator implements SyntaxComponent {

    private final SyntaxComponent backingComponent;

    public ImmutableSyntaxComponentDecorator(final SyntaxComponent backingComponent) {
        this.backingComponent = backingComponent;
    }

    @Override
    public String getSyntax() {
        return backingComponent.getSyntax();
    }

    @Override
    public Optional<SyntaxComponent> findChild(final String syntax) {
        return backingComponent.findChild(syntax).map(ImmutableSyntaxComponentDecorator::new);
    }

    @Override
    public void addChild(final SyntaxComponent component) {
        throw new UnsupportedOperationException("Syntax components cannot be manually added to an existing syntax tree");
    }

    @Override
    public Optional<Set<SyntaxComponent>> findChildren() {
        return backingComponent.findChildren().map(children -> children.stream().map(ImmutableSyntaxComponentDecorator::new).collect(Collectors.toSet()));
    }

    @Override
    public void addExecutable(final CommandExecutable executable) {
        throw new UnsupportedOperationException("Executable components cannot be manually added to an existing syntax tree");
    }

    @Override
    public Optional<Set<CommandExecutable>> findExecutables() {
        return backingComponent.findExecutables();
    }
}
