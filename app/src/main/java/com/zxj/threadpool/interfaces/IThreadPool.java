package com.zxj.threadpool.interfaces;

public interface IThreadPool {

    void executor(Runnable runnable);

    void destroy();
}
