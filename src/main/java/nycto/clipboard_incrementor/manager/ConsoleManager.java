/*
 * Clipboard Incrementor
 * Watches a folder for new files and increments the filename in the clipboard.
 * Copyright (C) 2024 Jelle Van Goethem
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package nycto.clipboard_incrementor.manager;

import nycto.clipboard_incrementor.command.Command;

import java.util.List;
import java.util.Scanner;

import static nycto.clipboard_incrementor.Main.stopApplication;
import static nycto.clipboard_incrementor.manager.DirectoryManager.changeDirectory;
import static nycto.clipboard_incrementor.manager.DirectoryManager.printCurrentDirectoryPath;

public class ConsoleManager {
    private static final List<Command> commands = List.of(
            new Command("change", "Change the directory to watch for new files", List.of("c")),
            new Command("print", "Print the path of the currently watched directory", List.of("p")),
            new Command("help", "Show the available commands", List.of("h", "commands")),
            new Command("stop", "Stop the application", List.of("s", "exit", "quit"))
    );

    /**
     * Scanner object for reading "standard" input from the console.
     */
    private static final Scanner stdinScanner = new Scanner(System.in);

    private ConsoleManager() {
    }

    private static void printCommands() {
        System.out.println("Available commands:");

        for (Command command : commands) {
            String aliases = command.aliases().isEmpty() ? "" : " (" + String.join(", ", command.aliases()) + ")";

            System.out.println(command.name() + aliases + ": " + command.description());
        }
    }

    public static void processConsoleInput() {
        scanLineLoop:
        while (stdinScanner.hasNextLine()) {
            String commandToExecute = null;
            String inputFirstString = splitOnSpaces(readConsoleInput())[0];
            boolean isInputCommandOrAlias = false;

            if (inputFirstString.isEmpty()) continue;

            for (Command command : commands) {
                if (matchesCommandOrAlias(inputFirstString, command)) {
                    commandToExecute = command.name();
                    isInputCommandOrAlias = true;
                }
            }

            if (isInputCommandOrAlias) {
                switch (commandToExecute) {
                    case "change" -> changeDirectory();
                    case "print" -> printCurrentDirectoryPath();
                    case "help" -> printCommands();
                    case "stop" -> {
                        stopApplication();

                        break scanLineLoop;
                    }
                }
            } else {
                System.err.println("Unknown command: " + inputFirstString);
            }
        }
    }

    public static String readConsoleInput() {
        return stdinScanner.nextLine().trim();
    }

    private static boolean matchesCommandOrAlias(String input, Command command) {
        if (input.equalsIgnoreCase(command.name())) {
            return true;
        }

        for (String alias : command.aliases()) {
            if (input.equalsIgnoreCase(alias)) {
                return true;
            }
        }

        return false;
    }

    private static String[] splitOnSpaces(String input) {
        return input.split("\\s+");
    }
}
