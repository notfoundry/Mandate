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

import pw.stamina.mandate.annotations.Restricted;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.internal.execution.executable.PermissionValidatingExecutableWrapper;
import pw.stamina.mandate.security.Permission;
import pw.stamina.mandate.syntax.SyntaxTree;
import pw.stamina.mandate.syntax.SyntaxTreeCreationStrategy;

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
public enum DefaultSyntaxTreeFactory implements SyntaxTreeCreationStrategy {
    INSTANCE;

    @Override
    public Set<SyntaxTree> createSyntaxTree(final Method backingMethod, final Object container, final CommandContext commandContext) {
        Syntax syntax; final String[][] tree; final Set<SyntaxTree> parents;
        if ((syntax = backingMethod.getDeclaredAnnotation(Syntax.class)) == null && (syntax = backingMethod.getDeclaringClass().getDeclaredAnnotation(Syntax.class)) == null) {
            throw new MissingSyntaxException("No syntax annotation found for method " + backingMethod.getName() + " annotated as executable.");
        }

        final CommandExecutable executable = createAppropriateCommandExecutable(backingMethod, container, commandContext);

        if ((tree = treeifySubSyntax(backingMethod.getDeclaredAnnotation(Executes.class).tree())).length > 0) {
            addSubSyntax(
                    (parents = Arrays.stream(syntax.root())
                            .map(BaseSyntaxTree::new)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            ), tree, 0, executable);
        } else {
            return Arrays.stream(syntax.root())
                    .map(s -> new BaseSyntaxTree(s, executable))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return parents;
    }

    private static CommandExecutable createAppropriateCommandExecutable(final Method backingMethod, final Object container, final CommandContext commandContext) {
        CommandExecutable executable = commandContext.getCommandConfiguration().getExecutableCreationStrategy().newExecutable(backingMethod, container, commandContext);
        if (backingMethod.isAnnotationPresent(Restricted.class)) {
            executable = new PermissionValidatingExecutableWrapper(executable, Permission.of(backingMethod.getDeclaredAnnotation(Restricted.class).permission()));
        }
        return executable;
    }

    private static void addSubSyntax(final Collection<SyntaxTree> syntaxComponents, final String[][] syntaxTree, final int index, final CommandExecutable terminalOp) {
        for (final SyntaxTree component : syntaxComponents) {
            if (index < syntaxTree.length - 1) {
                Arrays.stream(syntaxTree[index])
                        .map(BaseSyntaxTree::new)
                        .forEach(component::addChild);
                addSubSyntax(component.findChildren().get(), syntaxTree, index+1, terminalOp);
            } else {
                Arrays.stream(syntaxTree[index])
                        .map(s -> new BaseSyntaxTree(s, terminalOp))
                        .forEach(component::addChild);
            }
        }
    }

    private static String[][] treeifySubSyntax(final String[] commandSubSyntax) {
        return Arrays.stream(commandSubSyntax)
                .map(tree -> tree.split(Pattern.quote("|")))
                .toArray(String[][]::new);
    }

    public static DefaultSyntaxTreeFactory getInstance() {
        return INSTANCE;
    }
}
