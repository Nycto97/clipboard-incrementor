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

public class FilenameManager {
    public String createNewFilename(String filename) {
        String filenameWithoutExtensions = removeFileExtensions(filename);

        return incrementLastNumber(filenameWithoutExtensions);
    }

    public String incrementLastNumber(String filenameWithoutExtensions) {
        String filenameNew = filenameWithoutExtensions;

        Pattern lastNumberPattern = Pattern.compile("(\\d+)(?!.*\\d)");

        Matcher lastNumberMatcher = lastNumberPattern.matcher(filenameWithoutExtensions);

        if (lastNumberMatcher.find()) {
            String lastNumberString = lastNumberMatcher.group(1);

            int leadingZerosCount = 0;

            if (lastNumberString.startsWith("0") && lastNumberString.length() > 1) {
                Pattern leadingZeroPattern = Pattern.compile("^0+");

                Matcher leadingZeroMatcher = leadingZeroPattern.matcher(lastNumberString);

                if (leadingZeroMatcher.find()) {
                    leadingZerosCount = leadingZeroMatcher.group().length();
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

                if (leadingZerosCount > 0) {
                    if (lastNumberNew.length() > String.valueOf(lastNumber).length() ||
                            lastNumberString.length() == leadingZerosCount) {
                        leadingZerosCount--;
                    }

                    lastNumberNew = "0".repeat(leadingZerosCount) + lastNumberNew;
                }

                filenameNew = filenameWithoutExtensions.substring(0, lastNumberMatcher.start(1)) + lastNumberNew +
                        filenameWithoutExtensions.substring(lastNumberMatcher.end(1));

            } catch (NumberFormatException exception) {
                System.out.println("The (last) number in the filename is too large to increment." + "\n" +
                        "Added \" (1)\" to the filename instead.");

                exception.printStackTrace();

                filenameNew += " (1)";
            }
        } else {
            System.out.println("No number was found in the filename." + "\n" +
                    "Added \" (1)\" to the filename.");

            filenameNew += " (1)";
        }

        return filenameNew;
    }

    public String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (!filename.contains(".")) return filename;

        String extensionPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        return filename.replaceAll(extensionPattern, "");
    }

    public String removeFileExtensions(String filename) {
        if (!filename.contains(".")) return filename;

        return removeFileExtension(filename, true);
    }
}
