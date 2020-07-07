package cn.thens.jack.func;

public interface Action8<P1, P2, P3, P4, P5, P6, P7, P8> {
   void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) throws Throwable;

   abstract class X<P1, P2, P3, P4, P5, P6, P7, P8>
           implements Action8<P1, P2, P3, P4, P5, P6, P7, P8> {
       public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

       private X() {
       }

       public Action7.X<P2, P3, P4, P5, P6, P7, P8> curry(P1 p1) {
           return Action7.X.of((p2, p3, p4, p5, p6, p7, p8) ->
               run(p1, p2, p3, p4, p5, p6, p7, p8));
       }

       public X<P1, P2, P3, P4, P5, P6, P7, P8> once() {
           final Once<Void> once = Once.create();
           return of((p1, p2, p3, p4, p5, p6, p7, p8) ->
               once.run(() -> run(p1, p2, p3, p4, p5, p6, p7, p8)));
       }

       public <R> Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R> func(R result) {
           return Func8.X.of((p1, p2, p3, p4, p5, p6, p7, p8) -> {
               run(p1, p2, p3, p4, p5, p6, p7, p8);
               return result;
           });
       }

       public static <P1, P2, P3, P4, P5, P6, P7, P8>
       X<P1, P2, P3, P4, P5, P6, P7, P8>
       of(Action8<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8> action) {
           return new X<P1, P2, P3, P4, P5, P6, P7, P8>() {
               @Override
               public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
                   try {
                       action.run(p1, p2, p3, p4, p5, p6, p7, p8);
                   } catch (Throwable e) {
                       throw new ThrowableWrapper(e);
                   }
               }
           };
       }
   }
}
