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

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public class FilenameManager {

    private FilenameManager() {}

    private static int countLeadingZeros(String string) {
        if (!string.startsWith("0") || !(string.length() > 1)) return 0;

        Pattern leadingZeroPattern = Pattern.compile("^0+");
        Matcher leadingZeroPatternMatcher = leadingZeroPattern.matcher(string);

        if (!leadingZeroPatternMatcher.find()) return 0;

        return leadingZeroPatternMatcher.group().length();
    }

    public static String createNewFilename(String filename) {
        return incrementLastNumberInFilename(removeFileExtensions(filename));
    }

    static String incrementLastNumberInFilename(String filenameWithoutExtensions) {
        Pattern lastNumberPattern = Pattern.compile("(\\d+)(?!.*\\d)");
        Matcher lastNumberPatternMatcher = lastNumberPattern.matcher(filenameWithoutExtensions);

        if (!lastNumberPatternMatcher.find()) {
            System.out.println(
                "No number was found in the filename" +
                System.lineSeparator() +
                "Added \" (1)\" to the filename" +
                System.lineSeparator()
            );
            return filenameWithoutExtensions + " (1)";
        }

        String lastNumberString = lastNumberPatternMatcher.group(1);
        @Nullable Number lastNumberParsed = parseNumber(lastNumberString);
        @Nullable Number lastNumberIncremented = incrementNumber(lastNumberParsed);

        if (lastNumberIncremented == null) {
            System.out.println(
                "Could not increment (last) number in filename" +
                System.lineSeparator() +
                "Added \" (1)\" to the filename"
            );
            return filenameWithoutExtensions + " (1)";
        }

        String lastNumberIncrementedString = lastNumberIncremented.toString();
        String lastNumberParsedString = lastNumberParsed.toString();
        int leadingZeroCount = countLeadingZeros(lastNumberString);

        if (leadingZeroCount > 0) {
            if (
                lastNumberIncrementedString.length() > lastNumberParsedString.length() || // Number increased in length
                lastNumberString.length() == leadingZeroCount // Number was all zeros
            ) {
                leadingZeroCount--;
            }

            lastNumberIncrementedString = "0".repeat(leadingZeroCount) + lastNumberIncremented;
        }

        String filenameBeforeLastNumber = filenameWithoutExtensions.substring(0, lastNumberPatternMatcher.start(1));
        String filenameAfterLastNumber = filenameWithoutExtensions.substring(lastNumberPatternMatcher.end(1));

        return filenameBeforeLastNumber + lastNumberIncrementedString + filenameAfterLastNumber;
    }

    @Nullable private static Number incrementNumber(Number number) {
        return switch (number) {
            case Integer i -> (int) number + 1;
            case Long l -> (long) number + 1;
            case BigInteger bigInteger -> bigInteger.add(BigInteger.ONE);
            default -> null;
        };
    }

    @Nullable private static Number parseNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException integerFormatException) {
            try {
                return Long.parseLong(number);
            } catch (NumberFormatException longFormatException) {
                try {
                    return new BigInteger(number);
                } catch (NumberFormatException bigIntegerFormatException) {
                    return null;
                }
            }
        }
    }

    static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (!filename.contains(".")) return filename;

        String extensionPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        return filename.replaceAll(extensionPattern, "");
    }

    static String removeFileExtensions(String filename) {
        if (!filename.contains(".")) return filename;

        return removeFileExtension(filename, true);
    }
}
