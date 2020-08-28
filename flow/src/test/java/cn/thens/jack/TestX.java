package cn.thens.jack;


import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.flow.Collector;
import cn.thens.jack.flow.CollectorHelper;
import cn.thens.jack.flow.Flow;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Values;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Scheduler;
import cn.thens.jack.scheduler.Schedulers;


/**
 * @author 7hens
 */
@SuppressWarnings("ALL")
public class TestX {
    private static String getTestMethodName(StackTraceElement[] traceElements) {
        for (StackTraceElement traceElement : traceElements) {
            try {
                String className = traceElement.getClassName();
                String methodName = traceElement.getMethodName();
                Class<?> cls = TestX.class.getClassLoader().loadClass(className);
                Method method = cls.getDeclaredMethod(methodName);
                if (method.isAnnotationPresent(Test.class)) {
                    return methodName;
                }
            } catch (Throwable ignore) {
            }
        }
        return "";
    }

    private static String getTestMethodName() {
        return getTestMethodName(Thread.currentThread().getStackTrace());
    }

    public static <T> Collector<T> collector(String name) {
        Logger logger = logger(name);
        return new CollectorHelper<T>() {
            @Override
            protected void onStart(Cancellable cancellable) throws Throwable {
                super.onStart(cancellable);
                logger.log("onStart");
            }

            @Override
            protected void onEach(T data) throws Throwable {
                super.onNext(data);
                logger.log("onEach: " + data);
            }

            @Override
            protected void onComplete() throws Throwable {
                super.onComplete();
                logger.log("onComplete");
            }

            @Override
            protected void onError(Throwable e) throws Throwable {
                super.onError(e);
                logger.err("onError: " + e.getClass().getName());
//                e.printStackTrace();
            }

            @Override
            protected void onCancel() throws Throwable {
                super.onCancel();
                logger.err("onCancel");
            }
        };
    }

    private static String threadName() {
        return Thread.currentThread().getName();
    }

    public static Scheduler scheduler(String name) {
        final AtomicLong threadCount = new AtomicLong();
        return Schedulers.from(Executors.newFixedThreadPool(8, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(name + "-" + threadCount.incrementAndGet());
            return thread;
        }));
    }

    public static void sleep(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            throw Values.wrap(e);
        }
    }

    public static void block(long timeMs) {
        long start = System.currentTimeMillis();
        for (; ; ) {
            if (System.currentTimeMillis() - start > timeMs) {
                break;
            }
        }
    }

    public static <T> Func1<Flow<T>, Void> collect() {
        return flow -> {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                flow.onTerminate(it -> latch.countDown()).collect();
                latch.await();
                sleep(100);
                logger().log("==========================");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public static Logger logger(String tag) {
        return new Logger(tag);
    }

    public static Logger logger() {
        return logger(".");
    }

    public static class Logger {
        private String testName = getTestMethodName();
        private final String tag;

        public Logger(String tag) {
            this.tag = tag;
        }

        private String debugInfo(String message) {
            String threadName = Thread.currentThread().getName();
            return testName + ": (" + threadName + ") [" + tag + "] " + message;
        }

        public void log(String message) {
            System.out.println("  " + debugInfo(message));
        }

        public void err(String message) {
            System.out.println("E " + debugInfo(message));
        }
    }
}