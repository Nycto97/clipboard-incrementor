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

package nycto.clipboard_incrementor;

import nycto.clipboard_incrementor.watcher.DirectoryWatcher;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static String directoryPath;
    private static DirectoryWatcher directoryWatcher;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Scanner object for reading "standard" input from the console.
     */
    private static final Scanner stdinScanner = new Scanner(System.in);

    public static void main(String[] args) {
        startApplication();
    }

    private static void processConsoleInput() {
        scanLineLoop:
        while (stdinScanner.hasNext()) {
            String inputCleaned = stdinScanner.nextLine().toLowerCase().trim();

            switch (inputCleaned) {
                case "exit", "stop", "quit" -> {
                    stopApplication();

                    break scanLineLoop;
                }
                default -> System.out.println("Unknown command: " + inputCleaned);
            }
        }
    }

    private static void startApplication() {
        System.out.println("Clipboard Incrementor - Press CTRL+C or type 'stop' in this console window to exit" +
                "..." + "\n");
        directoryPath = "C:\\users\\myName\\Desktop\\Test";
        directoryWatcher = new DirectoryWatcher(directoryPath);

        submitDirectoryWatcher(directoryWatcher);
        processConsoleInput();
    }

    /**
     * Stops the application by shutting down the ExecutorService.
     *
     * @see 
     * <a href="https://www.baeldung.com/java-executor-service-tutorial#shutting">Shutting Down an ExecutorService</a>
     */
    private static void stopApplication() {
        Main.executorService.shutdown();

        try {
            if (!Main.executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                Main.executorService.shutdownNow();
            }
        } catch (InterruptedException interruptedException) {
            Main.executorService.shutdownNow();
        }

        System.out.println("Stopping the application...");
    }

    private static void submitDirectoryWatcher(DirectoryWatcher directoryWatcher) throws IllegalStateException {
        try {
            executorService.submit(directoryWatcher);
        } catch (NullPointerException | RejectedExecutionException exception) {
            throw new IllegalStateException("Could not submit Callable for watching the directory", exception);
        }
    }
}
