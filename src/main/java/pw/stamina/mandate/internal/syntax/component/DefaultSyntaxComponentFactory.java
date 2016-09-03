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

package pw.stamina.mandate.internal.syntax.component;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.syntax.SyntaxComponent;
import pw.stamina.mandate.syntax.SyntaxComponentCreationStrategy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public enum DefaultSyntaxComponentFactory implements SyntaxComponentCreationStrategy {
    INSTANCE;

    @Override
    public Set<SyntaxComponent> getComponents(final Method backingMethod, final Object container, final CommandContext commandContext) {
        Syntax syntax; final String[][] tree; final Set<SyntaxComponent> parents;
        if ((syntax = backingMethod.getDeclaredAnnotation(Syntax.class)) == null && (syntax = backingMethod.getDeclaringClass().getDeclaredAnnotation(Syntax.class)) == null) {
            throw new MissingSyntaxException("No syntax annotation found for method " + backingMethod.getName() + " annotated as executable.");
        }

        if ((tree = treeifySubSyntax(backingMethod.getDeclaredAnnotation(Executes.class).tree())).length > 0) {
            addSubSyntax(
                    (parents = Arrays.stream(syntax.root())
                            .map(BaseSyntaxComponent::new)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            ), tree, 0, commandContext.getCommandConfiguration().getExecutableCreationStrategy().newExecutable(backingMethod, container, commandContext));
        } else {
            final CommandExecutable executable = commandContext.getCommandConfiguration().getExecutableCreationStrategy().newExecutable(backingMethod, container, commandContext);
            return Arrays.stream(syntax.root())
                    .map(s -> new BaseSyntaxComponent(s, executable))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return parents;
    }

    private static void addSubSyntax(final Collection<SyntaxComponent> syntaxComponents, final String[][] syntaxTree, final int index, final CommandExecutable terminalOp) {
        for (final SyntaxComponent component : syntaxComponents) {
            if (index < syntaxTree.length - 1) {
                Arrays.stream(syntaxTree[index])
                        .map(BaseSyntaxComponent::new)
                        .forEach(component::addChild);
                addSubSyntax(component.findChildren().get(), syntaxTree, index+1, terminalOp);
            } else {
                Arrays.stream(syntaxTree[index])
                        .map(s -> new BaseSyntaxComponent(s, terminalOp))
                        .forEach(component::addChild);
            }
        }
    }

    private static String[][] treeifySubSyntax(final String[] commandSubSyntax) {
        return Arrays.stream(commandSubSyntax)
                .map(tree -> tree.split(Pattern.quote("|")))
                .toArray(String[][]::new);
    }

    public static DefaultSyntaxComponentFactory getInstance() {
        return INSTANCE;
    }
}
