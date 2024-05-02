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

import java.util.Scanner;

import static nycto.clipboard_incrementor.Main.stopApplication;

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
            String inputCleaned = readConsoleInput().toLowerCase();

            switch (inputCleaned) {
                case "exit", "stop", "quit" -> {
                    stopApplication();

                    break scanLineLoop;
                }
                default -> System.err.println("Unknown command: " + inputCleaned);
            }
        }
    }

    private static String readConsoleInput() {
        return stdinScanner.nextLine().trim();
    }
}
