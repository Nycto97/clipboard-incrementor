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

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatchFolder {

    public void watchFolder() {

        try {
            System.out.println("Watching directory for changes" + "\n");

            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path directory = Path.of("C:\\users\\myName\\Desktop\\Test");

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey watchKey;

            try {
                // Wait for and retrieve events
                while ((watchKey = watchService.take()) != null) {
                    for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) watchEvent;

                        String filename = pathEvent.context().toString();

                        if (filename.endsWith(".crdownload")) break;

                        WatchEvent.Kind<?> kind = watchEvent.kind();

                        // The code up to the break runs twice.
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            Toolkit toolkit = Toolkit.getDefaultToolkit();

                            Clipboard clipboard = toolkit.getSystemClipboard();

                            String newFilename = createNewFilename(filename);

                            Transferable clipboardContents = clipboard.getContents(null);

                            boolean clipboardHasString = (clipboardContents != null) &&
                                    clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor);

                            if (clipboardHasString) {
                                try {
                                    String clipboardString =
                                            (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);

                                    if (clipboardString.equals(newFilename)) break;
                                } catch (UnsupportedFlavorException | IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            String newFileCreatedText = "New file is created: ";

                            int dividerLength = (newFileCreatedText.length() + filename.length()) / 2;

                            String divider = "- ".repeat(dividerLength);

                            System.out.println(divider + "\n");

                            System.out.println(newFileCreatedText + filename);

                            StringSelection newFilenameStringSelection = new StringSelection(newFilename);

                            clipboard.setContents(newFilenameStringSelection, null);

                            System.out.println("Clipboard is set to: " + newFilename + "\n");
                        }
                    }

                    // Reset the watch key everytime for continuing to use it for further event retrieval
                    boolean valid = watchKey.reset();

                    if (!valid) break;
                }
            } catch (InterruptedException e) {
                // Restore interrupted status
                Thread.currentThread().interrupt();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static String createNewFilename(String filename) {
        String filenameWithoutExtensions = removeFileExtensions(filename);

        return incrementLastNumber(filenameWithoutExtensions);
    }

    public static String incrementLastNumber(String value) {
        Pattern lastNumberPattern = Pattern.compile("(\\d+)(?!.*\\d)");

        Matcher lastNumberMatcher = lastNumberPattern.matcher(value);

        String valueNew = value;

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
                    // Remove the negative sign caused by overflow
                    // This only occurs when lastNumber is exactly 9,223,372,036,854,775,807 (Long.MAX_VALUE)
                    // lastNumber + 1 will result in -9,223,372,036,854,775,808 if this is the case
                    lastNumberNew = lastNumberNew.substring(1);
                }

                if (leadingZerosCount > 0) {
                    if (lastNumberNew.length() > String.valueOf(lastNumber).length() ||
                            lastNumberString.length() == leadingZerosCount) {
                        leadingZerosCount--;
                    }

                    lastNumberNew = "0".repeat(leadingZerosCount) + lastNumberNew;
                }

                valueNew = value.substring(0, lastNumberMatcher.start(1)) + lastNumberNew +
                        value.substring(lastNumberMatcher.end(1));

            } catch (NumberFormatException exception) {
                System.out.println("The (last) number in the filename is too large to increment." + "\n" +
                        "Added \" (1)\" to the filename instead.");

                exception.printStackTrace();

                valueNew += " (1)";
            }
        } else {
            System.out.println("No number was found in the filename." + "\n" +
                    "Added \" (1)\" to the filename.");

            valueNew += " (1)";
        }

        return valueNew;
    }

    public static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        String extensionPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");

        return filename.replaceAll(extensionPattern, "");
    }

    public static String removeFileExtensions(String filename) {
        return removeFileExtension(filename, true);
    }

    public static class WatchCallable implements Callable<Void> {
        @Override
        public Void call() {
            WatchFolder wf = new WatchFolder();

            wf.watchFolder();

            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("\n" + "Starting a background thread for watching folder");

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new WatchCallable());

        System.out.println("After submitting Callable for watching folder" + "\n");
    }
}
