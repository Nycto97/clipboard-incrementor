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

import static nycto.clipboard_incrementor.manager.ConsoleManager.processConsoleInput;
import static nycto.clipboard_incrementor.manager.DirectoryManager.*;

public class Main {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        startApplication();
    }

    private static void startApplication() {
    	System.out.println("Clipboard Incrementor - Press CTRL+C or type 'stop' in this console window to exit" +
                "..." + "\n");
        String directory = "C:\\users\\myName\\Desktop\\Test";
        Path directoryPath = Path.of(directory);

        if (directoryExists(directoryPath)) {
            setDirectoryPath(directoryPath);
            submitDirectoryWatcher();
        } else {
            handleNonExistingDirectory(directoryPath, "Please update the directory path to an existing" +
                    " directory using the 'changedir' command or stop the application with the 'stop' command.");
        }

        processConsoleInput();
    }

    /**
     * Stops the application by shutting down the ExecutorService.
     *
     * @see 
     * <a href="https://www.baeldung.com/java-executor-service-tutorial#shutting">Shutting Down an ExecutorService</a>
     */
    public static void stopApplication() {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException interruptedException) {
            executorService.shutdownNow();
        }

        System.out.println("Stopping the application...");
    }

    private static void submitDirectoryWatcher() throws IllegalStateException {
        try {
            executorService.submit(new DirectoryWatcher());
        } catch (NullPointerException | RejectedExecutionException exception) {
            throw new IllegalStateException("Could not submit Callable for watching the directory", exception);
        }
    }
}
