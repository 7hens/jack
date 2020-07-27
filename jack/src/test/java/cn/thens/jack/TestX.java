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
import cn.thens.jack.func.Things;
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
        Logger logger = logger();
        return new CollectorHelper<T>() {
            @Override
            protected void onStart(Cancellable cancellable) throws Throwable {
                super.onStart(cancellable);
                log("onStart");
            }

            @Override
            protected void onEach(T data) throws Throwable {
                super.onEach(data);
                log("onEach: " + data);
            }

            @Override
            protected void onComplete() throws Throwable {
                super.onComplete();
                log("onComplete");
            }

            @Override
            protected void onError(Throwable e) throws Throwable {
                super.onError(e);
                log("onError: " + e.getClass().getName());
//                e.printStackTrace();
            }

            @Override
            protected void onCancel() throws Throwable {
                super.onCancel();
                log("onCancel");
            }

            private void log(String message) {
                logger.log("[" + name + "] " + message);
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

    public static void delay(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            throw Things.wrap(e);
        }
    }

    public static <T> Func1<Flow<T>, Void> collect() {
        return new Func1<Flow<T>, Void>() {
            @Override
            public Void call(Flow<T> flow) throws Throwable {
                try {
                    CountDownLatch latch = new CountDownLatch(1);
                    flow.onTerminate(it -> latch.countDown()).collect();
                    latch.await();
                    delay(100);
                    logger().log("==========================");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public static Logger logger() {
        return new Logger();
    }

    public static class Logger {
        private String testName = getTestMethodName();

        public void log(String message) {
            String threadName = Thread.currentThread().getName();
            System.out.println(testName + ": (" + threadName + ") " + message);
        }
    }
}