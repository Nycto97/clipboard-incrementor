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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilenameManager {
    public String createNewFilename(String filename) {
        String filenameWithoutExtensions = removeFileExtensions(filename);

        return incrementLastNumber(filenameWithoutExtensions);
    }

    String incrementLastNumber(String filenameWithoutExtensions) {
        String newFilename = filenameWithoutExtensions;

        try {
            Pattern lastNumberPattern = Pattern.compile("(\\d+)(?!.*\\d)");
            Matcher lastNumberPatternMatcher = lastNumberPattern.matcher(filenameWithoutExtensions);

            if (lastNumberPatternMatcher.find()) {
                String lastNumberString = lastNumberPatternMatcher.group(1);
                int leadingZeroCount = 0;

                if (lastNumberString.startsWith("0") && lastNumberString.length() > 1) {
                    Pattern leadingZeroPattern = Pattern.compile("^0+");
                    Matcher leadingZeroPatternMatcher = leadingZeroPattern.matcher(lastNumberString);

                    if (leadingZeroPatternMatcher.find()) {
                        leadingZeroCount = leadingZeroPatternMatcher.group().length();
                    }
                }

                try {
                    long lastNumber = Long.parseLong(lastNumberString);
                    String lastNumberNew = String.valueOf(lastNumber + 1);

                    if (lastNumber == Long.MAX_VALUE) {
                        /*
                         * Remove the negative sign caused by overflow
                         * This only occurs when lastNumber is exactly 9,223,372,036,854,775,807 (Long.MAX_VALUE)
                         * lastNumber + 1 will result in -9,223,372,036,854,775,808 if this is the case
                         */
                        lastNumberNew = lastNumberNew.substring(1);
                    }

                    if (leadingZeroCount > 0) {
                        if (lastNumberNew.length() > String.valueOf(lastNumber).length() ||
                                lastNumberString.length() == leadingZeroCount) {
                            leadingZeroCount--;
                        }

                        lastNumberNew = "0".repeat(leadingZeroCount) + lastNumberNew;
                    }

                    newFilename =
                            filenameWithoutExtensions.substring(0, lastNumberPatternMatcher.start(1)) + lastNumberNew +
                                    filenameWithoutExtensions.substring(lastNumberPatternMatcher.end(1));

                } catch (NumberFormatException exception) {
                    exception.printStackTrace();

                    newFilename += " (1)";
                    System.out.println("The (last) number in the filename is too large to increment." + "\n" +
                            "Added \" (1)\" to the filename instead.");
                }
            } else {
                newFilename += " (1)";
                System.out.println("No number was found in the filename." + "\n" +
                        "Added \" (1)\" to the filename." + "\n");
            }

            return newFilename;

        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException |
                 NullPointerException | OutOfMemoryError exception) {
            return newFilename + " (1)";
        }
    }

    String removeFileExtension(String filename, boolean removeAllExtensions) throws PatternSyntaxException {
        if (!filename.contains(".")) return filename;

        String extensionPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        try {
            return filename.replaceAll(extensionPattern, "");
        } catch (PatternSyntaxException patternSyntaxException) {
            throw new PatternSyntaxException("Could not remove file extension because the regex pattern is invalid",
                    extensionPattern, -1);
        }
    }

    String removeFileExtensions(String filename) {
        if (!filename.contains(".")) return filename;

        return removeFileExtension(filename, true);
    }
}
