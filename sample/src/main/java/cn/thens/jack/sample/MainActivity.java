package cn.thens.jack.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import cn.thens.jack.JGetter;
import cn.thens.jack.JLazyView;

public class MainActivity extends AppCompatActivity {
    // 创建注入工具，支持 Activity、Fragment、View、Dialog 等
    private JLazyView lazyView = JLazyView.create(this);

    // 绑定 id
    private JGetter<TextView> vText = lazyView.bind(R.id.vText);
    private JGetter<Button> vButton = lazyView.bind(R.id.vButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vText.get().setText("hello");
        vButton.get().setEnabled(false);
    }

}
