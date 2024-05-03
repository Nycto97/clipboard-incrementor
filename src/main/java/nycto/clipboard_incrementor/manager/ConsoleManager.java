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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nycto.clipboard_incrementor.Main.stopApplication;
import static nycto.clipboard_incrementor.manager.DirectoryManager.getDirectoryPath;

public class ConsoleManager {
    /**
     * Scanner object for reading "standard" input from the console.
     */
    private static final Scanner stdinScanner = new Scanner(System.in);

    private ConsoleManager() {
    }

    public static void processConsoleInput() {
        scanLineLoop:
        while (stdinScanner.hasNextLine()) {
            String commandLowerCase;

            String consoleInput = readConsoleInput();
            List<String> consoleInputParts = splitOnSpacesIgnoringQuotes(consoleInput);

            if (consoleInputParts.isEmpty()) continue;

            commandLowerCase = consoleInputParts.getFirst().toLowerCase();

            switch (commandLowerCase) {
                case "dir", "directory" -> System.out.println("Currently watching: " + getDirectoryPath());
                case "exit", "stop", "quit" -> {
                    stopApplication();

                    break scanLineLoop;
                }
                default -> System.err.println("Unknown command: " + commandLowerCase);
            }
        }
    }

    private static String readConsoleInput() {
        return stdinScanner.nextLine().trim();
    }

    /**
     * Splits a string on spaces, ignoring spaces inside quotes, to preserve quoted strings.
     * <p>
     * E.g. changedir "C:/users/my name/Desktop/Test Folder" -> ["changedir", "C:/users/my name/Desktop/Test Folder"]
     *
     * @param input The string to split.
     * @return A list of parts.
     */
    private static List<String> splitOnSpacesIgnoringQuotes(String input) {
        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        List<String> parts = new ArrayList<>();

        while (matcher.find()) parts.add(matcher.group());

        return parts;
    }
}
