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
import static nycto.clipboard_incrementor.Main.printStartBanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        /* Redirect stdout to ByteArrayOutputStream to test printed output */
        System.setOut(new PrintStream(stdoutBuffer));
    }

    @AfterEach
    void tearDown() {
        /* Reset stdout to the original stdout PrintStream */
        System.setOut(stdout);
    }

    @Test
    void printStartBanner_test() {
        printStartBanner();

        String expectedOutput =
            "- - - - - - - - - - - - - - - - - - - - - - - - - - -" +
            System.lineSeparator() +
            "- - - - - - - - Clipboard Incrementor - - - - - - - -" +
            System.lineSeparator() +
            "- - - - - - - - - - - - - - - - - - - - - - - - - - -" +
            System.lineSeparator() +
            "- - - - Type 'help' to see available commands - - - -" +
            System.lineSeparator() +
            "- - - - - - - - - - - - - - - - - - - - - - - - - - -" +
            System.lineSeparator() +
            System.lineSeparator();
        String actualOutput = stdoutBuffer.toString();

        assertEquals(expectedOutput, actualOutput);
    }

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
        assertEquals(System.lineSeparator(), createDivider(-6348));
        assertEquals(System.lineSeparator(), createDivider(-25));
        assertEquals(System.lineSeparator(), createDivider(-7));
        assertEquals(System.lineSeparator(), createDivider(-1));
    }
}
