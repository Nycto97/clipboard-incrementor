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

import nycto.clipboard_incrementor.manager.ClipboardManager;
import nycto.clipboard_incrementor.manager.FilenameManager;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;

public class DirectoryWatcher implements Callable<Void> {
    private static final ClipboardManager clipboardManager = new ClipboardManager(null);
    private static final FilenameManager filenameManager = new FilenameManager();

    private final Path directory;

    public DirectoryWatcher(String directoryPath) {
        this.directory = Path.of(directoryPath);
    }

    @Override
    public Void call() {
        try {
            WatchKey watchKey;
            WatchService watchService = FileSystems.getDefault().newWatchService();

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            System.out.println("Watching directory for changes..." + "\n");

            /* Wait for and retrieve watch events */
            while ((watchKey = watchService.take()) != null) {
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                    String filename = pathWatchEvent.context().toString();

                    if (filename.endsWith(".crdownload")) break;

                    WatchEvent.Kind<?> watchEventKind = watchEvent.kind();

                    /* The code up to the break runs twice */
                    if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE) {
                        String clipboardText = clipboardManager.getClipboardText();
                        String newFilename = filenameManager.createNewFilename(filename);

                        if (clipboardText != null && clipboardText.equals(newFilename)) break;

                        String newFileCreatedText = "New file is created: ";
                        System.out.println(createDivider((newFileCreatedText.length() + filename.length()) / 2));
                        System.out.println(newFileCreatedText + filename);

                        clipboardManager.setClipboardText(newFilename);
                    }
                }

                /* Reset the watch key everytime for continuing to use it for further event retrieval */
                boolean isWatchKeyValid = watchKey.reset();

                if (!isWatchKeyValid) break;
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException securityException) {
                securityException.printStackTrace();
            }
        }

        return null;
    }

    String createDivider(int length) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Length can't be a negative number");
        }

        return "- ".repeat(length) + "\n";
    }
}
