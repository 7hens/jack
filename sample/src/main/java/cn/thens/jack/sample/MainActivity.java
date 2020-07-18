package cn.thens.jack.sample;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.thens.jack.app.Permissions;
import cn.thens.jack.ref.IntentKey;
import cn.thens.jack.ref.IntentRef;
import cn.thens.jack.ref.LazyView;
import cn.thens.jack.ref.Ref;
import cn.thens.jack.scheduler.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    // 创建注入工具，支持 Activity、Fragment、View、Dialog 等
    private LazyView lazyView = LazyView.create(this);

    // 绑定 id
    private Ref<TextView> vText = lazyView.bind(R.id.vText);
    private Ref<Button> vButton = lazyView.bind(R.id.vButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vButton.get().setOnClickListener(v -> {
            IntentRef.create(this, MainActivity.class)
                    .put(TITLE, "Hello, Jack")
                    .startActivityForResult(this)
                    .onEach(data -> {

                    })
                    .flowOn(AndroidSchedulers.mainThread())
                    .collect();

            Permissions.request(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onEach(isGranted -> {
                    })
                    .flowOn(AndroidSchedulers.mainThread())
                    .collect();
        });

        String title = IntentRef.of(this).get(TITLE);
        vText.get().setText(title);
    }

    public static IntentKey<String> TITLE = IntentKey.string("title");
}
