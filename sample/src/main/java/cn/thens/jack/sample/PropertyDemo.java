package cn.thens.jack.sample;

import cn.thens.jack.ref.MutRef;
import cn.thens.jack.ref.Ref;

public class PropertyDemo {
    private String contentInternal;

    // 只读属性
    private Ref<String> title = Ref.get(() -> "hello world");


    // 可读写属性
    private MutRef<String> content = Ref
            .get(() -> contentInternal)
            .set(it -> contentInternal = it);

    public String getTitle() {
        return title.get();
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String value) {
        this.content.set(value);
    }
}
