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

import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;

/**
 * @author Foundry
 */
@Syntax(syntax = {"execute", "exec", "do"})
class TestCommandBody {

    @Executes(tree = {"return|ret", "string|str"})
    public String doSomething(String something) {
        return "Someone said: " + something;
    }

    @Executes(tree = {"return|ret", "string|str"})
    public String doSomething(String sender, String something) {
        return sender + " said: " + something;
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    private int sum(int first, int second) {
        return first + second;
    }

}
