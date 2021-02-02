package com.zxj.threadpool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.zxj.threadpool.interfaces.IThreadPool;
import com.zxj.threadpool.interfaces.impl.ThreadPoolImpl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IThreadPool threadPool = ThreadPoolImpl.getFixCoreThreadPool(5);
        for (int i = 0; i < 10000; i++) {
            Task task = new Task(i, "任务 " + i + " 被执行");
            threadPool.executor(task);
        }

    }

    class Task implements Runnable, Comparable<Task> {

        private int priority;
        private String taskName;

        public Task(int priority, String taskName) {
            this.priority = priority;
            this.taskName = taskName;
        }

        @Override
        public void run() {
            Log.d("======>", taskName + " " + Thread.currentThread().getName());
        }

        @Override
        public int compareTo(Task task) {
            return priority - task.priority;
        }
    }
}