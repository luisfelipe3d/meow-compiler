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

public enum Type {
    INTEGER(0),
    REAL(1),
    CHAR(2),
    IDENTIFIER(3),
    RELATIONAL_OPERATOR(4),
    ARITHMETIC_OPERATOR(5), 
    SPECIAL_CHARACTER(6),
    RESERVED_WORD(7),
    ASSIGNMENT_OPERATOR(8),
    SPECIAL_OPERATOR(9),
    CODE_END(99);

    public final int typeCode;

    private Type (int typeCode) {
        this.typeCode = typeCode;
    }
}
