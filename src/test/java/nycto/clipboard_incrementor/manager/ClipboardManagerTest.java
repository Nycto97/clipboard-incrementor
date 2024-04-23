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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.datatransfer.Clipboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClipboardManagerTest {
    private ClipboardManager clipboardManager;

    @BeforeEach
    void setup() {
        Clipboard testClipboard = new Clipboard("testClipboard");
        
        clipboardManager = new ClipboardManager(testClipboard);
    }

    private String getClipboardText() {
        return clipboardManager.getClipboardText();
    }

    private void setClipboardText(String clipboardText) {
        clipboardManager.setClipboardText(clipboardText);

    }

    @Test
    void setAndGetClipboardText() {
        String expectedText = "Hello, World! (2024)";

        setClipboardText(expectedText);

        String actualText = getClipboardText();

        assertEquals(expectedText, actualText);
    }
}
