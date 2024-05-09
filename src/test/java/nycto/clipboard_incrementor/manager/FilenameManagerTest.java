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

import static nycto.clipboard_incrementor.manager.FilenameManager.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilenameManagerTest {

    @Test
    void createNewFilename_test() {
        assertEquals("Cool Car 24", createNewFilename("Cool Car 23.jpg"));
        assertEquals("cute cat (9)", createNewFilename("cute cat (8).png"));
        assertEquals("14538210_1324532_1324", createNewFilename("14538210_1324532_1323.jpg"));
        assertEquals("xo02632-prod6", createNewFilename("xo02632-prod5.test.java"));
    }

    @Test
    void createNewFilename_removeFileExtensionsBeforeIncrementing() {
        assertEquals("some_song_5", createNewFilename("some_song_4.mp3"));
        assertEquals("Funny Meme 837", createNewFilename("Funny Meme 836.temp.mp4"));
    }

    @Test
    void createNewFilename_addNumberIfNoNumber() {
        assertEquals("file (1)", createNewFilename("file.txt"));
        assertEquals("banner-icon (1)", createNewFilename("banner-icon.png"));
    }

    @Test
    void createNewFilename_preserveLeadingZeros() {
        assertEquals("02", createNewFilename("01.json"));
        assertEquals("some_song006", createNewFilename("some_song005.mp3"));
        assertEquals("cool_text-00003", createNewFilename("cool_text-00002.txt"));
    }

    @Test
    void createNewFilename_removeLeadingZeroOnDigitIncrease() {
        assertEquals("Cool Car (10)", createNewFilename("Cool Car (09).jpg"));
        assertEquals("vb~rr~0100", createNewFilename("vb~rr~0099.jpg"));
        assertEquals("some-song-001000", createNewFilename("some-song-000999.mp3"));
    }

    @Test
    void createNewFilename_removeLeadingZeroIfOnlyZeros() {
        assertEquals("01", createNewFilename("00.xml"));
        assertEquals("001", createNewFilename("000.ext.test"));
        assertEquals("00001", createNewFilename("00000.jpg"));
    }

    /* Don't treat '0' as leading zero */
    @Test
    void createNewFilename_removeLeadingZeroIfZeroOnly() {
        assertEquals("1", createNewFilename("0.png"));
        assertEquals("1", createNewFilename("0.min.css"));
    }

    @Test
    void incrementLastNumberInFilename_test() {
        assertEquals("Cool Car 24", incrementLastNumberInFilename("Cool Car 23"));
        assertEquals("cute cat (9)", incrementLastNumberInFilename("cute cat (8)"));
        assertEquals("14538210_1324532_1324", incrementLastNumberInFilename("14538210_1324532_1323"));
        assertEquals("xo02632-prod6", incrementLastNumberInFilename("xo02632-prod5"));
    }

    @Test
    void incrementLastNumberInFilename_addNumberIfNoNumber() {
        assertEquals("file (1)", incrementLastNumberInFilename("file"));
        assertEquals("banner-icon (1)", incrementLastNumberInFilename("banner-icon"));
    }

    @Test
    void incrementLastNumberInFilename_preserveLeadingZeros() {
        assertEquals("02", incrementLastNumberInFilename("01"));
        assertEquals("some_song006", incrementLastNumberInFilename("some_song005"));
        assertEquals("cool_text-00003", incrementLastNumberInFilename("cool_text-00002"));
    }

    @Test
    void incrementLastNumberInFilename_removeLeadingZeroOnDigitIncrease() {
        assertEquals("Cool Car (10)", incrementLastNumberInFilename("Cool Car (09)"));
        assertEquals("vb~rr~0100", incrementLastNumberInFilename("vb~rr~0099"));
        assertEquals("some-song-001000", incrementLastNumberInFilename("some-song-000999"));
    }

    @Test
    void incrementLastNumberInFilename_removeLeadingZeroIfOnlyZeros() {
        assertEquals("01", incrementLastNumberInFilename("00"));
        assertEquals("001", incrementLastNumberInFilename("000"));
        assertEquals("00001", incrementLastNumberInFilename("00000"));
    }

    /* Don't treat '0' as leading zero */
    @Test
    void incrementLastNumberInFilename_removeLeadingZeroIfZeroOnly() {
        assertEquals("1", incrementLastNumberInFilename("0"));
    }

    @Test
    void removeFileExtension_handleNoFileExtension() {
        assertEquals("grghszzertgh", removeFileExtension("grghszzertgh", true));
        assertEquals("grghszzertgh", removeFileExtension("grghszzertgh", false));

        assertEquals("_-_file_-4315", removeFileExtension("_-_file_-4315", true));
        assertEquals("_-_file_-4315", removeFileExtension("_-_file_-4315", false));
    }

    @Test
    void removeFileExtension_handleSingleFileExtension() {
        assertEquals("Cool Car 23", removeFileExtension("Cool Car 23.jpg", true));
        assertEquals("Cool Car 23", removeFileExtension("Cool Car 23.jpg", false));

        assertEquals("some_song05", removeFileExtension("some_song05.mp3", true));
        assertEquals("some_song05", removeFileExtension("some_song05.mp3", false));
    }

    @Test
    void removeFileExtension_handleMultipleFileExtensions() {
        assertEquals("cute cat (8)", removeFileExtension("cute cat (8).test.png", true));
        assertEquals("cute cat (8).test", removeFileExtension("cute cat (8).test.png", false));

        assertEquals("nav-style", removeFileExtension("nav-style.min.css", true));
        assertEquals("nav-style.min", removeFileExtension("nav-style.min.css", false));

        assertEquals("file2", removeFileExtension("file2.with.dots.123.file", true));
        assertEquals("file2.with.dots.123", removeFileExtension("file2.with.dots.123.file", false));
    }

    @Test
    void removeFileExtension_handleDotfiles() {
        assertEquals(".gitignore", removeFileExtension(".gitignore", true));
        assertEquals(".gitignore", removeFileExtension(".gitignore", false));

        assertEquals(".gitignore", removeFileExtension(".gitignore.old", true));
        assertEquals(".gitignore", removeFileExtension(".gitignore.old", false));

        assertEquals(".gitignore", removeFileExtension(".gitignore.temp.old", true));
        assertEquals(".gitignore.temp", removeFileExtension(".gitignore.temp.old", false));
    }

    @Test
    void removeFileExtensions_handleNoFileExtension() {
        assertEquals("grghszzertgh", removeFileExtensions("grghszzertgh"));
        assertEquals("_-_file_-4315", removeFileExtensions("_-_file_-4315"));
    }

    @Test
    void removeFileExtensions_handleSingleFileExtension() {
        assertEquals("Cool Car 23", removeFileExtensions("Cool Car 23.jpg"));
        assertEquals("some_song05", removeFileExtensions("some_song05.mp3"));
    }

    @Test
    void removeFileExtensions_handleMultipleFileExtensions() {
        assertEquals("cute cat (8)", removeFileExtensions("cute cat (8).test.png"));
        assertEquals("nav-style", removeFileExtensions("nav-style.min.css"));
        assertEquals("file2", removeFileExtensions("file2.with.dots.123.file"));
    }

    @Test
    void removeFileExtensions_handleDotfiles() {
        assertEquals(".gitignore", removeFileExtensions(".gitignore"));
        assertEquals(".gitignore", removeFileExtensions(".gitignore.old"));
        assertEquals(".gitignore", removeFileExtensions(".gitignore.temp.old"));
    }
}
