package github.kasuminova.fileutils2.utils;

public interface ProgressFutureListener {
    void onProgress(long completedBytes, long bytesWritten);
}
