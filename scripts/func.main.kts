import java.io.File

val funcLimit = 9
var packageName = "cn.thens.jack.func"
val targetDir = File("jack/src/main/java/" + packageName.replace(".", "/"))

println(targetDir.absolutePath)

class GTypes(private val params: List<Pair<String, String>>) {
    private val types by lazy { params.joinToString(", ") { it.first } }
    val dTypes by lazy { if (types.isNotEmpty()) "<$types>" else "" }
    val args by lazy { params.joinToString(", ") { it.second } }
    val pairs by lazy { params.joinToString(", ") { it.first + " " + it.second } }
    val objectPairs by lazy { params.joinToString(", ") { "Object " + it.second } }
}

val params = mutableListOf<Pair<String, String>>()
for (i in 1..funcLimit) {
    params.add("P$i" to "p$i")
    val gTypes = GTypes(params)
    val types = gTypes.dTypes
    val args = gTypes.args
    val pairs = gTypes.pairs
    val objectPairs = gTypes.objectPairs

    val curryGTypes = GTypes(params.subList(1, params.size))
    val curryTypes = curryGTypes.dTypes
    val curryArgs = curryGTypes.args

    val actionName = "Action$i"
    val curryActionName = "Action${i - 1}"
    val actionFile = File(targetDir, "$actionName.java")
    actionFile.writeText("""
        |package $packageName;
        |
        |import cn.thens.jack.util.ThrowableWrapper;
        |
        |public interface $actionName$types {
        |   void run($pairs) throws Throwable;
        |
        |   abstract class X$types 
        |           implements $actionName$types {
        |       public abstract void run($pairs);
        |       
        |       public $curryActionName.X$curryTypes curry(P1 p1) {
        |           return $curryActionName.X.of(($curryArgs) -> 
        |               run($args));
        |       }
        |       
        |       public X$types once() {
        |           final Once<Void> once = Once.create();
        |           return of(($args) ->
        |               once.call(() -> run($args)));
        |       }
        |       
        |       public static $types 
        |       X$types 
        |       of($actionName$types action) {
        |           return new X$types() {
        |               @Override
        |               public void run($pairs) {
        |                   try {
        |                       action.run($args);
        |                   } catch (Throwable e) {
        |                       throw new ThrowableWrapper(e);
        |                   }
        |               }
        |           };
        |       }
        |       
        |       private static final X EMPTY = new X() {
        |           @Override
        |           public void run($objectPairs) {
        |           }
        |       };
        |       
        |       @SuppressWarnings("unchecked")
        |       public static $types 
        |       X$types 
        |       empty() {
        |           return EMPTY;
        |       }
        |   }
        |}
        |""".trimMargin())
}
