[![jitpack](https://jitpack.io/v/7hens/jack.svg)](https://jitpack.io/#7hens/jack)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/7hens/jack/blob/master/LICENSE)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)

# Jack

一个模拟 Kotlin 的轻量级的库。[项目地址](https://github.com/7hens/jack)

> 公司项目不让用 Kotlin，所以我就用 Java 写了个模拟 Kotlin 的库。哈哈~

```groovy
implementation 'cn.thens.jack:jack-android:0.3'
```

## View 注入

使用 Jack 来注入 View 非常简单，虽然比不过 Kotlin，但会比 Butter Knife 好很多，无需手动解绑，无内存泄漏风险。

<!-- tabs:start -->

### ** Jack **

```java
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
        
        // 获取 View
        vText.get().setText("hello");
        vButton.get().setEnabled(false);
    }
}
```

### ** ButterKnife **

```java
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.vText)
    TextView vText;
    @BindView(R.id.vButton)
    Button vButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ButterKnife.bind(this);
        vText.setText("hello");
        vButton.setEnabled(false);
    }
}
```

### ** Kotlin **

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        vText.text = "hello"
        vButton.isEnabled = false
    }
}
```

<!-- tabs:end -->

## lazy

模拟 Kotlin 的 lazy 函数。

<!-- tabs:start -->

### ** Jack **

```java
public class LazyDemo {
    // 类似于 Kotlin 的 private val name by lazy { "Jack" }
    private Ref<String> name = Ref.lazy(() -> "Jack");
    
    public String getName() {
        return name.get();
    }
}
```

### ** Kotlin **

```kotlin
class LazyDemo {
    val name by lazy { "Jack" }
}
```

<!-- tabs:end -->

## property

模拟 Kotlin 的属性。

<!-- tabs:start -->

### ** Jack **

```java
public class PropertyDemo {
    private String contentInternal;

    // 可读写属性
    private MutRef<String> content = Ref
            .get(() -> contentInternal)
            .set(it -> contentInternal = it);
    
    // 只读属性
    private Ref<String> title = Ref.get(() -> "hello world");

    public String getContent() {
        return content.get();
    }

    public void setContent(String value) {
        this.content.set(value);
    }
    
    public String getTitle() {
        return title.get();
    }
}
```

### ** Kotlin **

```kotlin
class PropertyDemo {
    private var contentInternal: String
    
    var content: String
        get() = contentInternal
        set(value) {
            contentInternal = value
        }
        
    val title: String 
        get() = "hello world"
}
```

<!-- tabs:end -->

## 空安全

Jack 中的空安全使用了一个封装类 Ref 来实现。

<!-- tabs:start -->

### ** Jack **

```java
Ref.of(text).elvis("");

Ref.of(text).safeCall(it -> it + "safeCall").get();
```

### ** Kotlin **

```kotlin
text ?: ""

text?.let { it + "safeCall" }
```

<!-- tabs:end -->

## Sequence

<!-- tabs:start -->

### ** Jack **

```java
Chain.of(list)
    .map(it -> it.toString())
    .joinToString();
```

### ** Kotlin **

```kotlin
list.toSequence()
    .map { it.toString() }
    .joinToString()
```

<!-- tabs:end -->

## 最后

看了看，还是 Kotlin 好用啊~

<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>