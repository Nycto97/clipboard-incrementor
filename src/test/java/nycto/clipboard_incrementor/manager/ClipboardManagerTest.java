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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClipboardManagerTest {
    private static ClipboardManager clipboardManager;

    @BeforeAll
    static void setup() {
        clipboardManager = new ClipboardManager();
    }

    @Test
    void setAndGetClipboardText() {
        String expectedText = "Hello, World! (2024)";

        clipboardManager.setClipboardText(expectedText);

        String actualText = clipboardManager.getClipboardText();

        assertEquals(expectedText, actualText);
    }
}
