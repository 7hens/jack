package cn.thens.jack.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.thens.jack.flow.Flow;

public class Permissions {
    public static Flow<Map<String, Boolean>> requestAll(Context context, String... permissions) {
        Map<String, Boolean> result = new HashMap<>();
        List<String> notSurePermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (isGranted(context, permission)) {
                result.put(permission, true);
            } else {
                result.put(permission, false);
                notSurePermissions.add(permission);
            }
        }
        if (notSurePermissions.isEmpty()) {
            return Flow.just(result);
        }
        return ActivityRequest.with(context)
                .requestPermissions(notSurePermissions.toArray(new String[0]))
                .map(it -> {
                    result.putAll(it);
                    return result;
                });
    }

    public static Flow<Boolean> request(Context context, String permission) {
        return isGranted(context, permission) ? Flow.just(true)
                : requestAll(context, permission).map(it -> it.get(permission));
    }


    public static boolean shouldShowRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean isGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            int check = ContextCompat.checkSelfPermission(context, permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
