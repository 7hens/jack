package cn.thens.jack.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.thens.jack.app.ActivityRequest;
import cn.thens.jack.ref.IntentKey;
import cn.thens.jack.ref.LazyView;
import cn.thens.jack.ref.Ref;

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
            ActivityRequest.with(this).startForResult(Ref.of(new Intent())
                    .put(TITLE, "Hello, Jack")
                    .get())
                    .onEach(result -> {

                    })
                    .collect();
        });

        String title = Ref.of(getIntent()).get(TITLE);
        vText.get().setText(title);
    }

    public static IntentKey<String> TITLE = IntentKey.string("title");
}
