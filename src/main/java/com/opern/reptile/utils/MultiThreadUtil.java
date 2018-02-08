package com.opern.reptile.utils;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 多线程工具类
 *
 * @param <T>
 * @param <K>
 */
public class MultiThreadUtil<T, K> {
    private CountDownLatch countDownLatch;
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private Worker<T, K> worker;
    private List<T> list;
    private List<K> resultList = Collections.synchronizedList(new ArrayList<K>());
    private FinishListener<K> finishListener;

    /**
     * 处理器
     *
     * @param <T>
     * @param <K>
     */
    public interface Worker<T, K> {
        List<K> work(List<T> list);
    }

    /**
     * 完成回调
     *
     * @param <T>
     */
    public interface FinishListener<T> {
        void onFinish(List<T> resultList);
    }

    public MultiThreadUtil(@NotNull Worker<T, K> worker, FinishListener<K> finishListener, List<T> list) {
        this.worker = worker;
        this.finishListener = finishListener;
        this.list = list;
    }

    public MultiThreadUtil(@NotNull Worker<T, K> worker, FinishListener<K> finishListener, List<T> list, int threadCount) {
        this.worker = worker;
        this.threadCount = threadCount;
        this.finishListener = finishListener;
        this.list = list;
    }

    /**
     * 启动多线程
     */
    public void start() {
        LogUtil.i("MultiThreadUtil", "MultiThread启动 线程数: " + threadCount);
        countDownLatch = new CountDownLatch(threadCount);
        for (int index = 0; index < threadCount; index++) {
            // TODO: 2018/2/8 0008 此处计算会丢失一定数据 
            WorkThread workThread = new WorkThread(list.subList(list.size() / threadCount * index, list.size() / threadCount * (index + 1)));
            workThread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.i("MultiThreadUtil", "MultiThread完毕");
        if (finishListener != null) {
            finishListener.onFinish(resultList);
        }
    }

    /**
     * 工作线程
     */
    public class WorkThread extends Thread {
        private List<T> list;

        public WorkThread(List<T> list) {
            this.list = list;
        }

        @Override
        public void run() {
            super.run();
            try {
                List<K> workerList = worker.work(list);
                resultList.addAll(workerList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }
    }
}
