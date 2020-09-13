package cn.thens.jack.loq;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 7hens
 */
public interface Logger<T> {

    void log(int priority, String tag, T message);

    Logger<Object> EMPTY = new Logger<Object>() {
        @Override
        public void log(int priority, String tag, Object message) {
        }
    };

    Logger<String> LOGCAT = new Logger<String>() {
        private int tagPrefix = 0;

        @Override
        public void log(int priority, String tag, String message) {
            tagPrefix = (tagPrefix + 1) % 10;
            Log.println(priority, tagPrefix + "/" + tag, message);
        }
    };

    Logger<String> SYSTEM = new Logger<String>() {
        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        private String[] levels = new String[]{"", "", "V", "D", "I", "W", "E", "A"};

        @Override
        public void log(int priority, String tag, String message) {
            String time = dateFormat.format(new Date());
            String level = levels[priority];
            System.out.println(time + " " + level + "/" + tag + ": " + message);
        }
    };
}
