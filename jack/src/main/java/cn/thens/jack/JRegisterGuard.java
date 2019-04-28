package cn.thens.jack;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 原子注册和注销。
 *
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class JRegisterGuard {
    private static final JAction.P0 NO_ACTION = System.out::println;

    /**
     * 注册。
     *
     * @return 是否已注册。
     */
    public abstract boolean register();

    /**
     * 注销。
     *
     * @return 是否已注销。
     */
    public abstract boolean unregister();

    /**
     * 查看是否已注册。
     */
    public abstract boolean isRegistered();

    public JRegisterGuard doOnRegister(JAction.P0 onRegister) {
        return doOnEach(onRegister, NO_ACTION);
    }

    public JRegisterGuard doOnUnregister(JAction.P0 onUnregister) {
        return doOnEach(NO_ACTION, onUnregister);
    }

    public JRegisterGuard doOnEvent(JAction.P1<Boolean> onEvent) {
        return doOnEach(() -> onEvent.invoke(true), () -> onEvent.invoke(false));
    }

    public JRegisterGuard doOnEach(JAction.P0 onRegister, JAction.P0 onUnregister) {
        return new Wrapper(this) {
            @Override
            public boolean register() {
                if (super.register()) return true;
                onRegister.invoke();
                return false;
            }

            @Override
            public boolean unregister() {
                if (super.unregister()) return true;
                onUnregister.invoke();
                return false;
            }
        };
    }

    public static class Atomic extends JRegisterGuard {
        private AtomicBoolean isRegistered = new AtomicBoolean(false);

        @Override
        public boolean register() {
            return !isRegistered.compareAndSet(false, true);
        }

        @Override
        public boolean unregister() {
            return !isRegistered.compareAndSet(true, false);
        }

        @Override
        public boolean isRegistered() {
            return isRegistered.get();
        }
    }

    public static class Wrapper extends JRegisterGuard {
        private final JRegisterGuard base;

        public Wrapper(JRegisterGuard base) {
            this.base = base;
        }

        @Override
        public boolean register() {
            return base.register();
        }

        @Override
        public boolean unregister() {
            return base.unregister();
        }

        @Override
        public boolean isRegistered() {
            return base.isRegistered();
        }
    }
}