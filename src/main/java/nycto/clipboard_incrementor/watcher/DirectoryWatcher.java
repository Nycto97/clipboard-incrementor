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

package nycto.clipboard_incrementor.watcher;

import static nycto.clipboard_incrementor.Main.createDivider;
import static nycto.clipboard_incrementor.manager.ClipboardManager.getClipboardText;
import static nycto.clipboard_incrementor.manager.ClipboardManager.setClipboardText;
import static nycto.clipboard_incrementor.manager.DirectoryManager.getDirectoryPath;
import static nycto.clipboard_incrementor.manager.FilenameManager.createNewFilename;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;

public class DirectoryWatcher implements Callable<Void> {

    private static WatchService watchService;

    public DirectoryWatcher() {}

    public static void closeWatchService() {
        if (watchService != null) {
            try {
                watchService.close();
                System.out.println("Successfully closed watch service");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static WatchService createWatchService() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }

    @Override
    public Void call() {
        try {
            WatchKey watchKey;
            Path directoryPath = getDirectoryPath();
            watchService = createWatchService();

            directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            System.out.println("Watching " + directoryPath + " for changes..." + System.lineSeparator());

            /* Wait for and retrieve watch events */
            while ((watchKey = watchService.take()) != null) {
                if (Thread.currentThread().isInterrupted()) {
                    return null;
                }

                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                    String filename = pathWatchEvent.context().toString();

                    if (filename.endsWith(".crdownload")) break;

                    WatchEvent.Kind<?> watchEventKind = watchEvent.kind();

                    /* The code up to the break runs twice */
                    if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE) {
                        String clipboardText = getClipboardText();
                        String newFilename = createNewFilename(filename);

                        if (clipboardText != null && clipboardText.equals(newFilename)) break;

                        String newFileCreatedText = "New file is created: ";
                        System.out.println(createDivider((newFileCreatedText.length() + filename.length()) / 2));
                        System.out.println(newFileCreatedText + filename);

                        setClipboardText(newFilename);
                    }
                }

                /* Reset the watch key everytime for continuing to use it for further event retrieval */
                boolean isWatchKeyValid = watchKey.reset();

                if (!isWatchKeyValid) break;
            }
        } catch (InterruptedException interruptedException) {
            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException securityException) {
                securityException.printStackTrace();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return null;
    }
}
