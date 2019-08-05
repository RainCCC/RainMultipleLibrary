package com.rain.liba_customviewutil.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Rain
 * @Date on 2019/08/05
 * @Description 线程池管理
 */
public class ThreadPoolManage {

    public static ThreadPoolManage mInstance;

    public static ThreadPoolManage getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolManage.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolManage();
                }
            }
        }
        return mInstance;
    }

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;
    private TimeUnit unit = TimeUnit.HOURS;
    private ThreadPoolExecutor executor;

    private ThreadPoolManage() {

        /**
         * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
         */
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
        maximumPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                maximumPoolSize,
                //表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                unit,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<Runnable>(),
                //创建线程的工厂
                Executors.defaultThreadFactory(),
                //用来对超出maximumPoolSize的任务的处理策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        if (executor == null) {
            //线程池执行者。
            //参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂;参7:线程异常处理策略
            executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    new LinkedBlockingQueue<Runnable>(),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
        if (runnable != null) {
            executor.execute(runnable);
        }
    }

    /**
     * 移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            executor.remove(runnable);
        }
    }

}
