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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Main {
    private static String directoryPath;
    private static DirectoryWatcher directoryWatcher;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        startApplication();
    }

    private static void startApplication() {
        directoryPath = "C:\\users\\myName\\Desktop\\Test";
        directoryWatcher = new DirectoryWatcher(directoryPath);

        submitDirectoryWatcher(directoryWatcher);
    }

    private static void submitDirectoryWatcher(DirectoryWatcher directoryWatcher) throws IllegalStateException {
        try {
            executorService.submit(directoryWatcher);
        } catch (NullPointerException | RejectedExecutionException exception) {
            throw new IllegalStateException("Could not submit Callable for watching the directory", exception);
        }
    }
}
