package cn.thens.jack.init;


import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JAction0;
import cn.thens.jack.func.JAction1;

/**
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class JRegisterGuard {
    public abstract boolean register();

    public abstract boolean unregister();

    public abstract boolean isRegistered();

    public JRegisterGuard doOnRegister(JAction0 onRegister) {
        return doOnEach(onRegister, JAction.empty());
    }

    public JRegisterGuard doOnUnregister(JAction0 onUnregister) {
        return doOnEach(JAction.empty(), onUnregister);
    }

    public JRegisterGuard doOnEvent(JAction1<Boolean> onEvent) {
        return doOnEach(() -> onEvent.invoke(true), () -> onEvent.invoke(false));
    }

    public JRegisterGuard doOnEach(JAction0 onRegister, JAction0 onUnregister) {
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