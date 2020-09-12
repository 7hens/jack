package cn.thens.jack.flow;

import java.util.List;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

@SuppressWarnings("WeakerAccess")
public interface BackPressure<T> {
    void onBackPressure(List<T> buffer) throws Throwable;

    abstract class X<T> implements BackPressure<T>, Action1<List<T>> {
        @Override
        public abstract void onBackPressure(List<T> buffer);

        @Override
        public void run(List<T> buffer) {
            onBackPressure(buffer);
        }

        private X() {
        }

        public Action1.X<List<T>> action() {
            return Action1.X.of(this);
        }

        public final BackPressure.X<T> catchError(Func1<? super Throwable, ? extends BackPressure<T>> catchError) {
            return of(buffer -> {
                try {
                    onBackPressure(buffer);
                } catch (Throwable e) {
                    catchError.call(e).onBackPressure(buffer);
                }
            });
        }

        public final BackPressure.X<T> or(BackPressure<T> backpressure) {
            return catchError(Funcs.always(backpressure));
        }

        public final BackPressure.X<T> and(BackPressure<T> backpressure) {
            return of(buffer -> {
                onBackPressure(buffer);
                backpressure.onBackPressure(buffer);
            });
        }

        public final BackPressure.X<T> dropAll() {
            return of(List::clear);
        }

        public final BackPressure.X<T> dropLatest() {
            return or(buffer -> buffer.remove(buffer.size() - 1));
        }

        public final BackPressure.X<T> dropOldest() {
            return or(buffer -> buffer.remove(0));
        }

        public static <T> BackPressure.X<T> of(BackPressure<T> backpressure) {
            return new BackPressure.X<T>() {
                @Override
                public void onBackPressure(List<T> buffer) {
                    try {
                        backpressure.onBackPressure(buffer);
                    } catch (Throwable e) {
                        throw new BackPressureException(e);
                    }
                }
            };
        }
    }

}
