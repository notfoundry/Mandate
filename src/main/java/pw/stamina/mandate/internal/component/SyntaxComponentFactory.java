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

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.exceptions.MalformedCommandException;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.internal.execution.MethodExecutable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Foundry
 */
public final class SyntaxComponentFactory {
    private SyntaxComponentFactory() {}

    public static Set<SyntaxComponent> getComponents(Method backingMethod, Object container, CommandManager commandManager) {
        Syntax syntax; String[][] tree; Set<SyntaxComponent> parents;
        if ((syntax = backingMethod.getDeclaredAnnotation(Syntax.class)) == null && (syntax = backingMethod.getDeclaringClass().getDeclaredAnnotation(Syntax.class)) == null) {
            throw new MalformedCommandException("No syntax annotation found for method " + backingMethod.getName() + " annotated as executable.");
        }

        if ((tree = treeifySubSyntax(backingMethod.getDeclaredAnnotation(Executes.class).tree())).length > 0) {
            addSubSyntax((parents = Arrays.stream(syntax.syntax())
                    .map(BaseSyntaxComponent::new)
                    .collect(Collectors.toCollection(LinkedHashSet::new))), tree, 0, new MethodExecutable(backingMethod, container, commandManager));
        } else {
            CommandExecutable executable = new MethodExecutable(backingMethod, container, commandManager);
            return Arrays.stream(syntax.syntax())
                    .map(s -> new BaseSyntaxComponent(s, executable))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return parents;
    }

    private static void addSubSyntax(Collection<SyntaxComponent> syntaxComponents, String[][] syntaxTree, int index, CommandExecutable terminalOp) {
        for (SyntaxComponent component : syntaxComponents) {
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

    private static String[][] treeifySubSyntax(String[] commandSubSyntax) {
        return Arrays.stream(commandSubSyntax)
                .map(tree -> tree.split(Pattern.quote("|")))
                .toArray(String[][]::new);
    }
}
