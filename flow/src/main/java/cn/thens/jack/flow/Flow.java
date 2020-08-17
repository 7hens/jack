package cn.thens.jack.flow;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.thens.jack.func.Action0;
import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;
import cn.thens.jack.scheduler.Schedulers;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public abstract class Flow<T> implements IFlow<T> {
    @Override
    public Flow<T> asFlow() {
        return this;
    }

    protected abstract void onStartCollect(Emitter<? super T> emitter) throws Throwable;

    Cancellable collectWith(Emitter<?> downEmitter, Collector<? super T> collector) throws Throwable {
        CollectorEmitter<T> upEmitter = CollectorEmitter.create(downEmitter, collector);
        downEmitter.addCancellable(upEmitter);
        onStartCollect(upEmitter);
        return upEmitter;
    }

    public Cancellable collect() {
        IScheduler scheduler = Schedulers.unconfined();
        Collector<T> collector = CollectorHelper.empty();
        CollectorEmitter<T> emitter = CollectorEmitter.create(scheduler, collector);
        emitter.addCancellable(emitter.schedule(() -> {
            try {
                onStartCollect(emitter);
            } catch (Throwable e) {
                emitter.error(e);
            }
        }));
        return emitter;
    }

    public Cancellable collectTo(Cancellable target) {
        Cancellable job = collect();
        target.addCancellable(job);
        return job;
    }

    public Flow<T> flowOn(IScheduler upScheduler) {
        return new FlowFlowOn<>(this, upScheduler);
    }

    @ApiStatus.Experimental
    public Flow<T> publish(Func0<? extends FlowEmitter<T>> emitterFactory) {
        return new FlowPublish<>(this, emitterFactory);
    }

    @ApiStatus.Experimental
    public Flow<T> publish(FlowEmitter<T> emitter) {
        return publish(Funcs.always(emitter));
    }

    public <R> R to(Func1<? super Flow<T>, ? extends R> operator) {
        return Funcs.of(operator).call(this);
    }

    public <R> Flow<R> lift(Func1<? super Emitter<? super R>, ? extends Collector<? super T>> operator) {
        return FlowLift.lift(this, operator);
    }

    public Flow<T> onCollect(Collector<? super T> collector) {
        return FlowOnCollect.onCollect(this, collector);
    }

    public Flow<T> onStart(Action1<? super Cancellable> consumer) {
        return FlowOnCollect.onStart(this, consumer);
    }

    public Flow<T> onEach(Action1<? super T> consumer) {
        return FlowOnCollect.onEach(this, consumer);
    }

    public Flow<T> onTerminate(Action1<? super Throwable> consumer) {
        return FlowOnCollect.onTerminate(this, consumer);
    }

    public Flow<T> onComplete(Action0 action) {
        return FlowOnCollect.onComplete(this, action);
    }

    public Flow<T> onError(Action1<? super Throwable> consumer) {
        return FlowOnCollect.onError(this, consumer);
    }

    public Flow<T> onCancel(Action0 action) {
        return FlowOnCollect.onCancel(this, action);
    }

    @SafeVarargs
    public final PolyFlow<T> polyWith(IFlow<T>... flows) {
        ArrayList<IFlow<T>> flowList = new ArrayList<>();
        flowList.add(this);
        flowList.addAll(Arrays.asList(flows));
        return from(flowList).to(FlowX.poly());
    }

    @SafeVarargs
    public final Flow<T> concatWith(IFlow<T>... flows) {
        return polyWith(flows).flatConcat();
    }

    @SafeVarargs
    public final Flow<T> mergeWith(IFlow<T>... flows) {
        return polyWith(flows).flatMerge();
    }

    @SafeVarargs
    public final Flow<T> switchWith(IFlow<T>... flows) {
        return polyWith(flows).flatSwitch();
    }

    @SafeVarargs
    public final Flow<List<T>> zipWith(IFlow<T>... flows) {
        return polyWith(flows).flatZip();
    }

    @SafeVarargs
    public final Flow<List<T>> joinWith(IFlow<T>... flows) {
        return polyWith(flows).flatJoin();
    }

    public <R> Flow<R> map(Func1<? super T, ? extends R> mapper) {
        return new FlowMap<>(this, mapper);
    }

    @Deprecated
    public <R> PolyFlow<R> polyMap(Func1<? super T, ? extends IFlow<R>> mapper) {
        return mapToFlow(mapper);
    }

    public <R> PolyFlow<R> mapToFlow(Func1<? super T, ? extends IFlow<R>> mapper) {
        return map(mapper).to(FlowX.poly());
    }

    public <R> Flow<R> flatMap(Func1<? super T, ? extends IFlow<R>> mapper) {
        return mapToFlow(mapper).flatMerge();
    }

    public <R> Flow<R> cast(Class<? extends R> cls) {
        return map(cls::cast);
    }

    public <C extends Collection<T>> Flow<C> toCollection(C collection) {
        return new FlowToCollection<>(this, collection);
    }

    public Flow<List<T>> toList() {
        return defer(() -> toCollection(new ArrayList<>()));
    }

    public Flow<Set<T>> toSet() {
        return defer(() -> toCollection(new LinkedHashSet<>()));
    }

    @ApiStatus.Experimental
    public <K> MapFlow<K, T> groupBy(Func1<? super T, ? extends K> keySelector) {
        return new FlowGroupBy<>(this, keySelector);
    }

    public Flow<T> filter(Predicate<? super T> predicate) {
        return FlowFilter.filter(this, predicate);
    }

    public Flow<T> filterNot(Predicate<? super T> predicate) {
        return filter(Predicate.X.of(predicate).not());
    }

    public Flow<T> filterNotNull() {
        return filter(Predicate.X.isNotNull());
    }

    public <R> Flow<R> filterIsInstance(Class<R> cls) {
        return filter(Predicate.X.is(cls)).cast(cls);
    }

    @Deprecated
    public <K> Flow<T> distinct(Func1<? super T, ? extends K> keySelector) {
        return distinctBy(keySelector);
    }

    public <K> Flow<T> distinctBy(Func1<? super T, ? extends K> keySelector) {
        return FlowFilter.distinctBy(this, keySelector);
    }

    public Flow<T> distinct() {
        return FlowFilter.distinct(this);
    }

    @Deprecated
    public <K> Flow<T> distinctUntilChanged(Func1<? super T, ? extends K> keySelector) {
        return distinctUntilChangedBy(keySelector);
    }

    public <K> Flow<T> distinctUntilChangedBy(Func1<? super T, ? extends K> keySelector) {
        return FlowFilter.distinctUntilChangedBy(this, keySelector);
    }

    public Flow<T> distinctUntilChanged() {
        return FlowFilter.distinctUntilChanged(this);
    }

    public Flow<T> throttleFirst(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        return new FlowThrottleFirst<>(this, flowFactory);
    }

    public Flow<T> throttleFirst(IFlow<?> flow) {
        return throttleFirst(Funcs.always(flow));
    }

    public Flow<T> throttleLast(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        return new FlowThrottleLast<>(this, flowFactory);
    }

    public Flow<T> throttleLast(IFlow<?> flow) {
        return throttleLast(Funcs.always(flow));
    }

    public Flow<T> sampleFirst(IFlow<?> windowFlow) {
        return window(windowFlow)
                .mapToFlow(it -> it.asFlow().first())
                .flatMerge();
    }

    public Flow<T> sampleLast(IFlow<?> windowFlow) {
        return window(windowFlow)
                .mapToFlow(it -> it.asFlow().last())
                .flatMerge();
    }

    public Flow<T> take(IFlow<?> timeoutFlow) {
        return autoSwitch(timeoutFlow, Flow.empty());
    }

    public Flow<T> take(int count) {
        return FlowTakeUntil.take(this, count);
    }

    public Flow<T> takeLast(int count) {
        return FlowBuffer.takeLast(this, count);
    }

    public Flow<T> takeWhile(Predicate<? super T> predicate) {
        return FlowTakeWhile.takeWhile(this, predicate);
    }

    public Flow<T> takeUntil(Predicate<? super T> predicate) {
        return FlowTakeUntil.takeUntil(this, predicate);
    }

    public Flow<T> takeUntil(T data) {
        return FlowTakeUntil.takeUntil(this, data);
    }

    public Flow<T> skip(IFlow<?> timeoutFlow) {
        return FlowFilter.skip(this, timeoutFlow);
    }

    public Flow<T> skip(int count) {
        return FlowFilter.skip(this, count);
    }

    public Flow<T> skipLast(int count) {
        return FlowBuffer.skipLast(this, count);
    }

    public Flow<T> skipAll() {
        return FlowFilter.skipAll(this);
    }

    @Deprecated
    public Flow<T> ignoreElements() {
        return skipAll();
    }

    public <R> Flow<R> skipAllTo(IFlow<R> next) {
        return FlowLift.skipAllTo(this, next);
    }

    public Flow<T> first(Predicate<? super T> predicate) {
        return FlowElementAt.first(this, predicate);
    }

    public Flow<T> first() {
        return FlowElementAt.first(this);
    }

    public Flow<T> last(Predicate<? super T> predicate) {
        return FlowFilter.last(this, predicate);
    }

    public Flow<T> last() {
        return last(Predicate.X.alwaysTrue());
    }

    public Flow<T> elementAt(int index) {
        if (index < 0) return FlowBuffer.lastElement(this, -index);
        return FlowElementAt.elementAt(this, index);
    }

    public Flow<T> ifEmpty(IFlow<T> fallback) {
        return FlowFilter.ifEmpty(this, fallback);
    }

    public Flow<T> repeat() {
        return FlowRepeat.repeat(this);
    }

    public Flow<T> repeat(int count) {
        return FlowRepeat.repeat(this, count);
    }

    public Flow<T> repeat(Func0<? extends Boolean> shouldRepeat) {
        return FlowRepeat.repeat(this, shouldRepeat);
    }

    public <R> Flow<R> reduce(R initialValue, Func2<? super R, ? super T, ? extends R> accumulator) {
        return FlowReduce.reduce(this, initialValue, accumulator);
    }

    public Flow<T> reduce(Func2<? super T, ? super T, ? extends T> accumulator) {
        return FlowReduce.reduceSelf(this, accumulator);
    }

    public Flow<T> timeout(IFlow<?> timeoutFlow, IFlow<T> fallback) {
        return new FlowTimeout<>(this, timeoutFlow, fallback);
    }

    public Flow<T> timeout(IFlow<?> timeoutFlow) {
        return timeout(timeoutFlow, Flow.error(new TimeoutException()));
    }

    @Deprecated
    public Flow<T> timeout(long timeout, TimeUnit unit, IFlow<T> fallback) {
        return timeout(Flow.timer(timeout, unit), fallback);
    }

    @Deprecated
    public Flow<T> timeout(long timeout, TimeUnit unit) {
        return timeout(Flow.timer(timeout, unit));
    }

    public Flow<T> delay(Func1<? super Reply<? extends T>, ? extends IFlow<?>> delayFunc) {
        return FlowDelay.delay(this, delayFunc);
    }

    public Flow<T> delay(IFlow<?> delayFlow) {
        return FlowDelay.delay(this, delayFlow);
    }

    public Flow<T> delayError(Func1<? super Throwable, ? extends IFlow<?>> delayFunc) {
        return delay(reply -> reply.isError() ? delayFunc.call(reply.error()) : Flow.empty());
    }

    public Flow<T> delayError(IFlow<?> delayFlow) {
        return delayError(Funcs.always(delayFlow));
    }

    public Flow<T> delayStart(IFlow<?> delayFlow) {
        return FlowDelayStart.delayStart(this, delayFlow);
    }

    public Flow<T> autoCancel(IFlow<?> cancelFlow) {
        return FlowAutoSwitch.autoCancel(this, cancelFlow);
    }

    public Flow<T> autoSwitch(IFlow<?> timeoutFlow, IFlow<T> fallback) {
        return FlowAutoSwitch.autoSwitch(this, timeoutFlow, fallback);
    }

    public Flow<T> catchError(Func1<? super Throwable, ? extends IFlow<T>> resumeFunc) {
        return FlowCatch.catchError(this, resumeFunc);
    }

    @Deprecated
    public Flow<T> catchError(Action2<? super Throwable, ? super Emitter<? super T>> resumeAction) {
        return FlowCatch.catchError(this, resumeAction);
    }

    public Flow<T> catchError(IFlow<T> resumeFlow) {
        return FlowCatch.catchError(this, resumeFlow);
    }

    public Flow<T> catchError() {
        return catchError(empty());
    }

    public Flow<T> retry() {
        return FlowCatch.retry(this);
    }

    public Flow<T> retry(Predicate<? super Throwable> predicate) {
        return FlowCatch.retry(this, predicate);
    }

    public Flow<T> retry(int count) {
        return FlowCatch.retry(this, count);
    }

    public Flow<T> retry(IFlow<?> timeoutFlow) {
        return FlowCatch.retry(this, timeoutFlow);
    }

    public PolyFlow<T> window(IFlow<?> windowFlow) {
        return FlowWindow.window(this, windowFlow);
    }

    public PolyFlow<T> window(Predicate<? super T> shouldClose) {
        return FlowWindowFilter.window(this, shouldClose);
    }

    public PolyFlow<T> window(int count) {
        return FlowWindowFilter.window(this, count);
    }

    public Flow<List<T>> buffer(IFlow<?> windowFlow) {
        return window(windowFlow).flatToList();
    }

    public Flow<List<T>> buffer(int count) {
        return window(count).flatToList();
    }

    public Flow<T> onBackPressure(BackPressure<T> backPressure) {
        return new FlowOnBackPressure<>(this, backPressure);
    }

    public static <T> Flow<T> create(Action1<? super Emitter<? super T>> onStart) {
        return FlowCreate.create(onStart);
    }

    public static <T> Flow<T> defer(IFlow<T> flowFactory) {
        return FlowCreate.defer(flowFactory);
    }

    @SafeVarargs
    public static <T> Flow<T> just(T... items) {
        return FlowCreate.fromArray(items);
    }

    public static <T> Flow<T> from(T[] array) {
        return FlowCreate.fromArray(array);
    }

    public static <T> Flow<T> from(Iterable<T> iterable) {
        return FlowCreate.fromIterable(iterable);
    }

    public static <T> Flow<T> from(Future<? extends T> future) {
        return FlowCreate.fromFuture(future);
    }

    public static Flow<Integer> range(int start, int end, int step) {
        return FlowCreate.range(start, end, step);
    }

    public static Flow<Integer> range(int start, int end) {
        return range(start, end, end > start ? 1 : -1);
    }

    public static <T> Flow<T> empty() {
        return FlowCreate.empty();
    }

    public static <T> Flow<T> single(Func0<? extends T> func) {
        return FlowCreate.fromFunc(func);
    }

    public static <T> Flow<T> complete(Action0 action) {
        return FlowCreate.fromAction(action);
    }

    public static <T> Flow<T> error(Throwable e) {
        return FlowCreate.error(e);
    }

    public static <T> Flow<T> cancel() {
        return error(Reply.cancel().error());
    }

    public static <T> Flow<T> never() {
        return FlowCreate.never();
    }

    public static Flow<Long> timer(long delay, TimeUnit unit) {
        return FlowCreate.timer(delay, unit);
    }

    public static Flow<Long> interval(long initialDelay, long period, TimeUnit unit) {
        return FlowCreate.interval(initialDelay, period, unit);
    }

    public static Flow<Long> interval(long period, TimeUnit unit) {
        return interval(period, period, unit);
    }

    public static Flow<Integer> from(@NotNull InputStream input, byte[] buffer) {
        return FlowCreate.from(input, buffer);
    }

    public static Flow<Integer> copy(@NotNull InputStream input, @NotNull OutputStream output, byte[] buffer) {
        return FlowCreate.copy(input, output, buffer);
    }

    public static Flow<Integer> copy(@NotNull InputStream input, @NotNull OutputStream output) {
        return copy(input, output, new byte[8 * 1024]);
    }
}
