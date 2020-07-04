package cn.thens.jack.sample;

import cn.thens.jack.property.Getter;
import cn.thens.jack.property.Property;
import cn.thens.jack.property.Setter;

public class PropertyDemo {
    private String contentInternal;

    // 只读属性
    private Getter<String> title = new Property<String>()
            .get(() -> "hello world");


    // 可读写属性
    private Setter<String> content = new Property<String>()
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
