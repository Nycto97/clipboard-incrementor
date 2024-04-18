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

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipboardManager {
    private final Clipboard clipboard;

    public ClipboardManager() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        this.clipboard = toolkit.getSystemClipboard();
    }

    public String getClipboardText() {
        Transferable clipboardContents = clipboard.getContents(null);

        boolean clipboardHasString = (clipboardContents != null) &&
                clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor);

        if (clipboardHasString) {
            try {
                return (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);
            } catch (IOException | UnsupportedFlavorException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    public void setClipboardText(String clipboardText) {
        StringSelection clipboardTextStringSelection = new StringSelection(clipboardText);

        clipboard.setContents(clipboardTextStringSelection, null);

        System.out.println("Clipboard is set to: " + clipboardText + "\n");
    }
}
