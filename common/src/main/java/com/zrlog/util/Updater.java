package com.zrlog.util;

import java.util.concurrent.CompletableFuture;

public interface Updater {

    CompletableFuture<Void> restartJarAsync();

    String fileName();
}
