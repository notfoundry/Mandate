/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2017 Mark Johnson
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

import pw.stamina.mandate.syntax.SyntaxTree;
import pw.stamina.mandate.execution.executable.CommandExecutable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public class ImmutableSyntaxTreeDecorator implements SyntaxTree {

    private final SyntaxTree backingComponent;

    public ImmutableSyntaxTreeDecorator(final SyntaxTree backingComponent) {
        this.backingComponent = backingComponent;
    }

    @Override
    public String getSyntax() {
        return backingComponent.getSyntax();
    }

    @Override
    public Optional<SyntaxTree> findChild(final String syntax) {
        return backingComponent.findChild(syntax).map(ImmutableSyntaxTreeDecorator::new);
    }

    @Override
    public void addChild(final SyntaxTree component) {
        throw new UnsupportedOperationException("Syntax components cannot be manually added to an existing syntax root");
    }

    @Override
    public Optional<Set<SyntaxTree>> findChildren() {
        return backingComponent.findChildren().map(children -> children.stream().map(ImmutableSyntaxTreeDecorator::new).collect(Collectors.toSet()));
    }

    @Override
    public void addExecutable(final CommandExecutable executable) {
        throw new UnsupportedOperationException("Executable components cannot be manually added to an existing syntax root");
    }

    @Override
    public Optional<Set<CommandExecutable>> findExecutables() {
        return backingComponent.findExecutables();
    }
}
