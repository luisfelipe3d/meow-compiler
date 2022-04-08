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

package br.unicap.meow.compiler;

import br.unicap.meow.compiler.model.Token;
import br.unicap.meow.compiler.controller.Compiler;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world");
        
        if( args.length == 0){
            System.out.println("Sem argumentos");
        } else {
            for (String string : args) {
                System.out.println(string);
            }
        }

        
        String filePath = "src"+File.separator+"main"+File.separator+
                "java"+File.separator+"br"+File.separator+
                "unicap"+File.separator+"meow"+File.separator+
                "compiler"+File.separator+"controller"+File.separator+"code.txt";
        Compiler comp = new Compiler(filePath);
        Token token = null;
        while ((token = comp.getToken()) != null) {
            System.out.println("Lexeme: " + token.getLexeme() + "\nType: " + token.getType());
        }
    }
}
