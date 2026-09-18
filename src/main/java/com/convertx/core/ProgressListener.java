package com.convertx.core;

@FunctionalInterface
public interface ProgressListener {
    void onProgress(String stage, int completed, int total);
}
