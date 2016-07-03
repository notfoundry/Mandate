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

import pw.stamina.mandate.api.execution.CommandExecutable;

import java.util.*;

/**
 * @author Foundry
 */
public class SyntaxComponent {
    private final Map<String, SyntaxComponent> childMap;
    private final String syntax;
    private Set<CommandExecutable> executables;

    public SyntaxComponent(String syntax) {
        this(syntax, new CommandExecutable[0]);
    }

    public SyntaxComponent(String syntax, CommandExecutable... executables) {
        this.syntax = syntax;
        this.executables = new HashSet<>(Arrays.asList(executables));
        this.childMap = new LinkedHashMap<>();
    }

    public String getSyntax() {
        return syntax;
    }

    public SyntaxComponent getChild(String syntax) {
        return childMap.get(syntax);
    }

    public void addChild(SyntaxComponent component) {
        childMap.put(component.getSyntax(), component);
    }

    public Collection<SyntaxComponent> getChildren() {
        return Collections.unmodifiableCollection(childMap.values());
    }

    public void addExecutable(CommandExecutable executable) {
        executables.add(executable);
    }

    public Optional<Set<CommandExecutable>> findExecutables() {
        return Optional.ofNullable(executables);
    }

    @Override
    public String toString() {
        return String.format("SyntaxComponent{syntax=%s, children=%s, executables=%s}", syntax, childMap.values(), executables);
    }
}
