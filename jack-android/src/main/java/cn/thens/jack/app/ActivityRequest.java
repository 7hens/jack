package cn.thens.jack.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

import cn.thens.jack.flow.Emitter;
import cn.thens.jack.flow.Flow;
import cn.thens.jack.ref.Ref;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ActivityRequest {
    private final Context context;

    private ActivityRequest(Context context) {
        this.context = context;
    }

    public static ActivityRequest with(Context context) {
        return new ActivityRequest(context);
    }

    private <T> Flow<T> request(Request request) {
        return Flow.create(emitter -> {
            new Contract(emitter, request).launch(context);
        });
    }

    public Flow<Result> startForResult(Intent intent, Bundle options) {
        return request((fragment, requestCode) ->
                fragment.startActivityForResult(intent, requestCode, options));
    }

    public Flow<Result> startForResult(Intent intent) {
        return startForResult(intent, null);
    }

    public Flow<Result> startForResult(
            IntentSender intent, Intent fillInIntent, int flagsMask,
            int flagsValues, int extraFlags, Bundle options) {

        return request((fragment, requestCode) ->
                fragment.startIntentSenderForResult(
                        intent, requestCode, fillInIntent, flagsMask,
                        flagsValues, extraFlags, options));
    }

    public Flow<Result> startForResult(
            IntentSender intent, Intent fillInIntent, int flagsMask,
            int flagsValues, int extraFlags) {

        return startForResult(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    public Flow<Map<String, Boolean>> requestPermissions(String... permissions) {
        return request((fragment, requestCode) -> {
            fragment.requestPermissions(permissions, requestCode);
        });
    }

    public static class Result {
        private final int code;
        private final Intent data;

        public Result(int code, Intent data) {
            this.code = code;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public Intent getData() {
            return data;
        }
    }

    private interface Request {
        void run(Fragment fragment, int requestCode) throws Throwable;
    }

    private static SparseArray<Contract> contracts = new SparseArray<>();
    private static final String FRAGMENT_TAG = "jack.ActivityRequest";
    private static final String REQUEST_CODE = "jack.REQUEST_CODE";

    private static class Contract {
        private final Emitter emitter;
        private final Request request;
        private final int requestCode;
        private boolean isRequested = false;

        private Contract(Emitter emitter, Request request) {
            this.emitter = emitter;
            this.request = request;
            this.requestCode = ViewCompat.generateViewId();

            contracts.put(requestCode, this);
            emitter.addCancellable(() -> contracts.remove(requestCode));
        }

        public void launch(Context context) {
            if (isRequested) return;
            try {
                if (context instanceof FragmentActivity) {
                    isRequested = true;
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
                    if (fragment == null) {
                        fragment = new ShadowFragment();
                        fm.beginTransaction().add(fragment, FRAGMENT_TAG).commitNow();
                    }
                    request.run(fragment, requestCode);
                } else {
                    context.startActivity(new Intent(context, ShadowActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(REQUEST_CODE, requestCode)
                    );
                }
            } catch (Throwable e) {
                emitter.error(e);
            }
        }
    }

    public static class ShadowActivity extends FragmentActivity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            overridePendingTransition(0, 0);
            Window window = getWindow();
            window.getAttributes().flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            super.onCreate(savedInstanceState);
            launch(getIntent());
        }

        @Override
        protected void onNewIntent(Intent intent) {
            overridePendingTransition(0, 0);
            super.onNewIntent(intent);
            launch(intent);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            moveTaskToBack(true);
        }

        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(0, 0);
        }

        private void launch(Intent intent) {
            int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
            Contract contract = contracts.get(requestCode);
            if (contract != null) {
                contract.launch(this);
            }
        }
    }

    public static class ShadowFragment extends Fragment {
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            onResult(requestCode, new Result(resultCode, Ref.of(data).elvis(new Intent())));
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Map<String, Boolean> result = new HashMap<>();
            for (int i = 0; i < permissions.length; i++) {
                result.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            onResult(requestCode, result);
        }

        private void onResult(int requestCode, Object result) {
            Contract contract = contracts.get(requestCode);
            if (contract != null) {
                Emitter emitter = contract.emitter;
                emitter.data(result);
                emitter.complete();
            }
        }
    }
}
