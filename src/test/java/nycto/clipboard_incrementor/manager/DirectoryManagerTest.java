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

import static nycto.clipboard_incrementor.manager.DirectoryManager.getDirectoryPath;
import static nycto.clipboard_incrementor.manager.DirectoryManager.setDirectoryPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DirectoryManagerTest {

    @Test
    void setAndGetDirectoryPath_test() {
        Path expectedPath = Path.of("C:\\testUser\\testName\\testDir");

        setDirectoryPath(expectedPath);

        Path actualPath = getDirectoryPath();

        assertEquals(expectedPath, actualPath);
    }
}
