package cn.thens.jack.sample;

import cn.thens.jack.property.JGetter;
import cn.thens.jack.property.JProperty;
import cn.thens.jack.property.JSetter;

public class PropertyDemo {
    private String contentInternal;

    // 只读属性
    private JGetter<String> title = new JProperty<String>()
            .get(() -> "hello world");


    // 可读写属性
    private JSetter<String> content = new JProperty<String>()
            .set(it -> contentInternal = it)
            .get(() -> contentInternal);

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
