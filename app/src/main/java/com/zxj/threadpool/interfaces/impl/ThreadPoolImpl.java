package com.zxj.threadpool.interfaces.impl;


import com.zxj.threadpool.interfaces.IThreadPool;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolImpl implements IThreadPool {

    private WorkThread[] workThreads;
    private PriorityBlockingQueue<Runnable> priorityBlockingQueue = new PriorityBlockingQueue<>();
    private static IThreadPool threadPool;
    private AtomicInteger priorityBlockingQueueSize;

    private ThreadPoolImpl() {
    }

    public static IThreadPool getFixCoreThreadPool(int coreThread) {
        if (threadPool == null) {
            synchronized (ThreadPoolImpl.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolImpl(coreThread);
                }
            }
        }
        return threadPool;
    }

    public ThreadPoolImpl(int coreThread) {
        priorityBlockingQueueSize = new AtomicInteger(0);
        workThreads = new WorkThread[coreThread];
        for (int i = 0; i < coreThread; i++) {
            workThreads[i] = new WorkThread();
            Thread thread = new Thread(workThreads[i], "ThreadPool - worker - " + i);
            thread.start();
        }
    }

    @Override
    public void executor(Runnable runnable) {
        priorityBlockingQueue.add(runnable);
        priorityBlockingQueueSize.incrementAndGet();
    }

    @Override
    public void destroy() {
        for (int i = 0; i < workThreads.length; i++) {
            workThreads[i].setRunning(false);
        }
    }

    class WorkThread extends Thread {

        private boolean isRunning = true;

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                Runnable runnable = null;
                try {
                    runnable = priorityBlockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (runnable != null) {
                    runnable.run();
                }
                priorityBlockingQueueSize.decrementAndGet();
            }
        }
    }
}
