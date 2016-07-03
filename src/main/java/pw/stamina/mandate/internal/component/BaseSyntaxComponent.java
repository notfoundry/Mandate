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

package pw.stamina.mandate.internal.component;

import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.execution.CommandExecutable;

import java.util.*;

/**
 * @author Foundry
 */
public class BaseSyntaxComponent implements SyntaxComponent {
    private final Map<String, SyntaxComponent> childMap;
    private final String syntax;
    private Set<CommandExecutable> executables;

    public BaseSyntaxComponent(String syntax) {
        this(syntax, new CommandExecutable[0]);
    }

    public BaseSyntaxComponent(String syntax, CommandExecutable... executables) {
        this.syntax = syntax;
        this.executables = new HashSet<>(Arrays.asList(executables));
        this.childMap = new LinkedHashMap<>();
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

    @Override
    public SyntaxComponent getChild(String syntax) {
        return childMap.get(syntax);
    }

    @Override
    public void addChild(SyntaxComponent component) {
        childMap.put(component.getSyntax(), component);
    }

    @Override
    public Collection<SyntaxComponent> getChildren() {
        return Collections.unmodifiableCollection(childMap.values());
    }

    @Override
    public void addExecutable(CommandExecutable executable) {
        executables.add(executable);
    }

    @Override
    public Optional<Set<CommandExecutable>> findExecutables() {
        return Optional.ofNullable(executables);
    }

    @Override
    public String toString() {
        return String.format("BaseSyntaxComponent{syntax=%s, children=%s, executables=%s}", syntax, childMap.values(), executables);
    }
}
