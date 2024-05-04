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

import java.nio.file.Path;
import java.util.concurrent.*;

import static nycto.clipboard_incrementor.manager.ConsoleManager.closeStdinScanner;
import static nycto.clipboard_incrementor.manager.ConsoleManager.processConsoleInput;
import static nycto.clipboard_incrementor.manager.DirectoryManager.*;
import static nycto.clipboard_incrementor.watcher.DirectoryWatcher.closeWatchService;

public class Main {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static Future<?> future = null;

    public static void main(String[] args) {
        startApplication();
    }

    private static void cancelFuture() {
        if (future != null && !future.isDone()) {
            future.cancel(true);

            if (future.isCancelled()) {
                System.out.println("Successfully cancelled future");
            } else {
                System.err.println("Could not cancel future");
            }
        }
    }

    public static String createDivider(int length) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Length can't be a negative number");
        }

        return "- ".repeat(length) + System.lineSeparator();
    }
    
    private static void printStartBanner() {
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - -");
        System.out.println("- - - - - - - - Clipboard Incrementor - - - - - - - -");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - -");
        System.out.println("- - - - Type 'help' to see available commands - - - -");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - -");
        System.out.println();
    }

    private static void startApplication() {
        printStartBanner();

        String directory = "C:\\users\\myName\\Desktop\\Test";
        Path directoryPath = Path.of(directory);

        if (directoryExists(directoryPath)) {
            setDirectoryPath(directoryPath);
            submitDirectoryWatcher();
        } else {
            handleNonExistingDirectory(directoryPath, "Please update the directory path to an existing" +
                    " directory using the 'change' command or stop the application with the 'stop' command.");
        }

        processConsoleInput();
    }

    /**
     * Stops the application by closing the standard input scanner, closing the watch service,
     * canceling the future and shutting down the ExecutorService.
     *
     * @see 
     * <a href="https://www.baeldung.com/java-executor-service-tutorial#shutting">Shutting Down an ExecutorService</a>
     */
    public static void stopApplication() {
        System.out.println("Stopping application...");

        closeStdinScanner();
        closeWatchService();
        cancelFuture();

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException interruptedException) {
            executorService.shutdownNow();
        }

        if (executorService.isShutdown()) {
            System.out.println("Successfully shut down ExecutorService");
        } else {
            System.err.println("Could not shut down ExecutorService");
        }
    }

    public static void submitDirectoryWatcher() throws IllegalStateException {
        /* Cancel the future, if one exists and is not completed/done, before submitting a new Callable */
        cancelFuture();
        /* Close the watch service, if one exists, before submitting a new Callable */
        closeWatchService();

        try {
            future = executorService.submit(new DirectoryWatcher());
        } catch (NullPointerException | RejectedExecutionException exception) {
            throw new IllegalStateException("Could not submit Callable for watching the directory", exception);
        }
    }
}
