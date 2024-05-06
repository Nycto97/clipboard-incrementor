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

import static nycto.clipboard_incrementor.Main.*;
import static nycto.clipboard_incrementor.manager.ConsoleManager.readConsoleInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

public class DirectoryManager {

    @Nullable private static Path directoryPath;

    private DirectoryManager() {}

    static void changeDirectory() {
        System.out.println("Enter the new directory path:");
        @Nullable String newDirectory;
        @Nullable Path newDirectoryPath = null;

        while (newDirectoryPath == null) {
            newDirectory = readConsoleInput();

            while (newDirectory.isEmpty()) {
                System.err.println("Directory path cannot be empty. Please enter a valid directory path:");
                newDirectory = readConsoleInput();
            }

            try {
                newDirectoryPath = Path.of(newDirectory);
            } catch (InvalidPathException invalidPathException) {
                System.err.println(
                    "Invalid directory path format: " +
                    newDirectory +
                    System.lineSeparator() +
                    "Please enter a valid directory path:"
                );
            }
        }

        if (directoryExists(newDirectoryPath)) {
            setDirectoryPath(newDirectoryPath);
            submitDirectoryWatcher();
        } else {
            handleNonExistingDirectory(
                newDirectoryPath,
                "Continuing to watch" + " " + getDirectoryPath() + " for changes..." + System.lineSeparator()
            );
        }
    }

    private static void createDirectory(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
            System.out.println("Successfully created directory: " + directoryPath + System.lineSeparator());
        } catch (IOException ioException) {
            throw new IllegalStateException("Could not create directory: " + directoryPath, ioException);
        } catch (SecurityException securityException) {
            throw new IllegalStateException(
                "Permission denied to create directory: " + directoryPath,
                securityException
            );
        }
    }

    public static boolean directoryExists(Path directoryPath) {
        try {
            return Files.exists(directoryPath);
        } catch (SecurityException securityException) {
            throw new IllegalStateException(
                "Permission denied to access directory: " + directoryPath,
                securityException
            );
        }
    }

    public static void handleNonExistingDirectory(Path directoryPath, String suffix) {
        System.out.println(
            "Directory " +
            directoryPath +
            " does not exist." +
            System.lineSeparator() +
            "Would you like to create it? (yes/no)"
        );

        String consoleInput = readConsoleInput();

        while (
            !consoleInput.equalsIgnoreCase("y") &&
            !consoleInput.equalsIgnoreCase("yes") &&
            !consoleInput.equalsIgnoreCase("n") &&
            !consoleInput.equalsIgnoreCase("no")
        ) {
            System.out.println("Invalid input. Please enter 'yes' or 'no'");

            consoleInput = readConsoleInput();
        }

        if (consoleInput.equalsIgnoreCase("y") || consoleInput.equalsIgnoreCase("yes")) {
            createDirectory(directoryPath);
            setDirectoryPath(directoryPath);
            submitDirectoryWatcher();
        } else {
            System.out.println("Directory not created." + System.lineSeparator() + suffix);
        }
    }

    static void openCurrentDirectory() {
        if (directoryPath == null) {
            System.err.println(
                "Directory path is not configured yet" +
                System.lineSeparator() +
                "Please configure " +
                "the directory path using the 'change' command before trying to open the current directory"
            );
            return;
        }

        if (!IS_DESKTOP_SUPPORTED) {
            System.err.println(
                "Desktop class is not supported on this platform" +
                OS_NAME_SUFFIX +
                System.lineSeparator() +
                "Could not open directory: " +
                directoryPath
            );
            return;
        }

        if (!IS_OPEN_ACTION_SUPPORTED) {
            System.err.println(
                "Desktop class does not support the OPEN action" +
                System.lineSeparator() +
                "Could not open directory: " +
                directoryPath
            );
            return;
        }

        try {
            DESKTOP.open(new File(directoryPath.toString()));
            System.out.println("Successfully opened directory: " + directoryPath);
        } catch (IOException ioException) {
            System.err.println("Could not open directory: " + directoryPath);
        } catch (SecurityException securityException) {
            System.err.println("Permission denied to open directory: " + directoryPath);
        }
    }

    static void printCurrentDirectoryPath() {
        System.out.println("Currently watching directory: " + directoryPath);
    }

    public static @Nullable Path getDirectoryPath() {
        return directoryPath;
    }

    public static void setDirectoryPath(@Nullable Path directoryPath) {
        DirectoryManager.directoryPath = directoryPath;
    }
}
