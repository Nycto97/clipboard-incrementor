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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryManager {
    private static Path directoryPath;

    private DirectoryManager() {
    }

    private static void createDirectory(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
            System.out.println("Successfully created directory: " + directoryPath + "\n");
        } catch (IOException ioException) {
            throw new IllegalStateException("Could not create directory: " + directoryPath, ioException);
        } catch (SecurityException securityException) {
            throw new IllegalStateException("Permission denied to create directory: " + directoryPath,
                    securityException);
        }
    }

    public static boolean directoryExists(Path directoryPath) {
        try {
            return Files.exists(directoryPath);
        } catch (SecurityException securityException) {
            throw new IllegalStateException("Permission denied to access directory: " + directoryPath,
                    securityException);
        }
    }

    public static Path getDirectoryPath() {
        return directoryPath;
    }

    public static void setDirectoryPath(Path directoryPath) {
        DirectoryManager.directoryPath = directoryPath;
    }
}
