package com.logging;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryLogs {
    private WatchService watchService;
    private Path path;
    private boolean keepWatching;

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public DirectoryLogs(Path directory) {
        try {
            path = directory;
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
        } catch (IOException ioe) {
            System.err.println(DirectoryLogs.class.getName() + ": IOException while creating Watch Service: " + ioe);
            ioe.printStackTrace();
        }
    }

    public void beginLogging(Callable<Void> callable) {
        keepWatching = true;
        while (keepWatching) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
                for (WatchEvent<?> e : watchKey.pollEvents()) {
                    WatchEvent<Path> ev = cast(e);
                    Path filename = ev.context();
                    try {
                        callable.call();
                        if (e.kind() == ENTRY_CREATE) {
                            System.out.println(ENTRY_CREATE + "-" + filename.getFileName());
                        } else if (e.kind() == ENTRY_DELETE) {
                            System.out.println(ENTRY_DELETE + "-" + filename.getFileName());
                        } else if (e.kind() == ENTRY_MODIFY) {
                            System.out.println(ENTRY_MODIFY + "-" + filename.getFileName());
                        }
                    } catch (Exception exception) {
                        System.out.printf("Exception while calling the callback " + exception);
                    }
                }
                watchKey.reset();
            } catch (InterruptedException interruptedException) {
                System.err.println(DirectoryLogs.class.getName() + ": InterruptedException while starting the Watch Service: " + interruptedException);
                interruptedException.printStackTrace();
                return;
            }
        }
    }

    public void endLogging() {
        try {
            keepWatching = false;
            watchService.close();
        } catch (IOException ioe) {
            System.err.println(DirectoryLogs.class.getName() + ": IOException while ending Watch Service: " + ioe.toString());
            ioe.printStackTrace();
        }
    }
}
