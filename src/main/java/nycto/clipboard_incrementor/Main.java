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
    public static void main(String[] args) throws IllegalStateException {
        /* TODO: Change the directory path to the directory you want to watch */
        String directoryPath = "C:\\users\\myName\\Desktop\\Test";

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(directoryPath);

        ExecutorService executor = Executors.newCachedThreadPool();

        System.out.println("\n" + "Started a background thread for watching the directory");

        try {
            executor.submit(directoryWatcher);

            System.out.println("After submitting Callable for watching directory" + "\n");
        } catch (NullPointerException | RejectedExecutionException exception) {
            exception.printStackTrace();

            throw new IllegalStateException("Could not submit Callable for watching directory", exception);
        }
    }
}
