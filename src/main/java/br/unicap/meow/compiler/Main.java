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

import br.unicap.meow.compiler.controller.Compiler;
import br.unicap.meow.compiler.controller.SyntaticAnalyzer;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        final String packagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator + "br" + File.separator +
            "unicap" + File.separator + "meow" + File.separator + "compiler" + File.separator;

        String fileName = "meow-code.txt";

        
        Compiler scanner = new Compiler(packagePath + fileName);
        SyntaticAnalyzer parser = new SyntaticAnalyzer(scanner);
        
        parser.startingPoint_nonTerminal();
    }
}
