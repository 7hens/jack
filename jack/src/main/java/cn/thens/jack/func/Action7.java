package cn.thens.jack.func;

import cn.thens.jack.util.ThrowableWrapper;

public interface Action7<P1, P2, P3, P4, P5, P6, P7> {
   void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) throws Throwable;

   abstract class X<P1, P2, P3, P4, P5, P6, P7>
           implements Action7<P1, P2, P3, P4, P5, P6, P7> {
       public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

       X() {
       }

       public Action6.X<P2, P3, P4, P5, P6, P7> curry(P1 p1) {
           return Action6.X.of((p2, p3, p4, p5, p6, p7) ->
               run(p1, p2, p3, p4, p5, p6, p7));
       }

       public X<P1, P2, P3, P4, P5, P6, P7> once() {
           final Once<Void> once = Once.create();
           return of((p1, p2, p3, p4, p5, p6, p7) ->
               once.call(() -> run(p1, p2, p3, p4, p5, p6, p7)));
       }

       public <R> Func7.X<P1, P2, P3, P4, P5, P6, P7, R> func(R result) {
           return Func7.X.of((p1, p2, p3, p4, p5, p6, p7) -> {
               run(p1, p2, p3, p4, p5, p6, p7);
               return result;
           });
       }

       public static <P1, P2, P3, P4, P5, P6, P7>
       X<P1, P2, P3, P4, P5, P6, P7>
       of(Action7<P1, P2, P3, P4, P5, P6, P7> action) {
           return new X<P1, P2, P3, P4, P5, P6, P7>() {
               @Override
               public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
                   try {
                       action.run(p1, p2, p3, p4, p5, p6, p7);
                   } catch (Throwable e) {
                       throw new ThrowableWrapper(e);
                   }
               }
           };
       }
   }
}
