package cn.thens.jack.ref;


import org.junit.Test;

import java.lang.reflect.Method;


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

    public static void delay(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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