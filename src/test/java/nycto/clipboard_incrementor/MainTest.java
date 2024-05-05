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

import static nycto.clipboard_incrementor.Main.createDivider;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void createDivider_handlePositiveLength() {
        assertEquals(System.lineSeparator(), createDivider(0));
        assertEquals("- " + System.lineSeparator(), createDivider(1));
        assertEquals("- - - " + System.lineSeparator(), createDivider(3));
        assertEquals("- - - - - " + System.lineSeparator(), createDivider(5));
        assertEquals("- - - - - - - - - - " + System.lineSeparator(), createDivider(10));
        assertEquals("- - - - - - - - - - - - - - - - - " + System.lineSeparator(), createDivider(17));
        assertEquals(
            "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - " + System.lineSeparator(),
            createDivider(31)
        );
    }

    @Test
    void createDivider_handleNegativeLength() {
        assertThrows(IllegalArgumentException.class, () -> createDivider(-6348));
        assertThrows(IllegalArgumentException.class, () -> createDivider(-25));
        assertThrows(IllegalArgumentException.class, () -> createDivider(-7));
        assertThrows(IllegalArgumentException.class, () -> createDivider(-1));
    }
}
