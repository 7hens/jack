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

    public static JRegisterGuard atomic() {
        return new AtomicImpl();
    }

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

    public JRegisterGuard doOnEach(JAction.P0 onRegister, JAction.P0 onUnregister) {
        final JRegisterGuard self = this;
        return new JRegisterGuard() {
            @Override
            public boolean register() {
                if (self.register()) return true;
                onRegister.invoke();
                return false;
            }

            @Override
            public boolean unregister() {
                if (self.unregister()) return true;
                onUnregister.invoke();
                return false;
            }

            @Override
            public boolean isRegistered() {
                return self.isRegistered();
            }
        };
    }

    private static class AtomicImpl extends JRegisterGuard {
        private AtomicBoolean isRegistered = new AtomicBoolean(false);

        public boolean register() {
            return !isRegistered.compareAndSet(false, true);
        }

        public boolean unregister() {
            return !isRegistered.compareAndSet(true, false);
        }

        @Override
        public boolean isRegistered() {
            return isRegistered.get();
        }
    }
}