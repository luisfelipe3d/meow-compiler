package br.unicap.meow.compiler.model;

/*
 * Copyright (C) 2022 liraline
 * Copyright (C) 2022 luisfelipe3d
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

public enum TokenTypes {
    IDENTIFIER("1 - Identifier"),
    INTEGER("2 - Integer"),
    REAL("4 - Float"),
    SPECIAL_CHARACTER("5 - Special Character"),
    ARITHMETIC_OPERATOR("6 - Arithmetic Operator"), 
    ASSIGNMENT_OPERATOR("7 - Assignment Operator"),
    RELATIONAL_OPERATOR("8/9 - Relational Operator"),
    RESERVED_WORD("11 - Reserved Word"),
    SPECIAL_OPERATOR("14 - Special Operator"),
    CHAR("17 - Char"),
    CODE_END("99 - End of Code");

    public final String typeCode;

    private TokenTypes (String typeCode) {
        this.typeCode = typeCode;
    }
}
