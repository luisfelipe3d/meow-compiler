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

package br.unicap.meow.compiler.model;

public class Token {
    private String type;
    private String lexeme;

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String getType() {
        return this.type;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public String toString() {
        return "Lexeme: " + getLexeme() + "\n" + "Token Type: " + getType() + "\n";
    }

}