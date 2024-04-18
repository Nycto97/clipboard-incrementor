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

            // Creates a watch service.
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Gets the path of the directory to monitor.
            Path directory = Path.of("C:\\users\\myName\\Desktop\\Test");

            // Registers the directory with the watch service to which this object is to
            // be registered and the events for which this object should be registered.
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            try {
                // Waits for and retrieves events.
                WatchKey key;

                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                        // Gets the filename from the event context.
                        Path filename = pathEvent.context();

                        String filenameString = filename.toString();

                        if (filenameString.endsWith(".crdownload")) break;

                        // Checks the type of the event.
                        WatchEvent.Kind<?> kind = event.kind();

                        // Performs the necessary action with the create event.
                        // The code up to the break runs twice.
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            Toolkit toolkit = Toolkit.getDefaultToolkit();

                            Clipboard clipboard = toolkit.getSystemClipboard();

                            String newFilename = createNewFilename(filenameString);

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

                            int dividerLength = (newFileCreatedText.length() + filenameString.length()) / 2;

                            String divider = "- ".repeat(dividerLength);

                            System.out.println(divider + "\n");

                            System.out.println(newFileCreatedText + filenameString);

                            StringSelection newFilenameStringSelection = new StringSelection(newFilename);

                            clipboard.setContents(newFilenameStringSelection, null);

                            System.out.println("Clipboard is set to: " + newFilename + "\n");
                        }
                    }

                    // Resets the watch key everytime for continuing to use it for further event retrieval.
                    boolean valid = key.reset();

                    if (!valid) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Restores interrupted status.
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createNewFilename(String filename) {
        String filenameWithoutExtensions = removeFileExtensions(filename);

        return incrementLastNumber(filenameWithoutExtensions);
    }

    public static String incrementLastNumber(String value) {
        // Pattern to match the last number in a string
        Pattern pattern = Pattern.compile("(\\d+)(?!.*\\d)");

        Matcher matcher = pattern.matcher(value);

        String valueNew = value;

        if (matcher.find()) {
            String lastNumberString = matcher.group(1);

            int leadingZeros = 0;

            if (lastNumberString.startsWith("0") && lastNumberString.length() > 1) {
                Pattern leadingZeroPattern = Pattern.compile("^0+");

                Matcher zeroMatcher = leadingZeroPattern.matcher(lastNumberString);

                if (zeroMatcher.find()) {
                    leadingZeros = zeroMatcher.group().length();
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

                if (leadingZeros > 0) {
                    if (lastNumberNew.length() > String.valueOf(lastNumber).length()) {
                        leadingZeros--;
                    }

                    lastNumberNew = "0".repeat(leadingZeros) + lastNumberNew;
                }

                valueNew =
                        value.substring(0, matcher.start(1)) + lastNumberNew + value.substring(matcher.end(1));

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
