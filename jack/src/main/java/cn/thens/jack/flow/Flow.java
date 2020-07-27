package cn.thens.jack.flow;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.thens.jack.func.Action0;
import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.func.Things;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Scheduler;
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

    protected abstract Cancellable collect(Scheduler scheduler, Collector<? super T> collector);

    public Cancellable collect() {
        return collect(Schedulers.unconfined(), CollectorHelper.get());
    }

    Cancellable collect(Emitter<?> emitter, Collector<? super T> collector) {
        Cancellable cancellable = collect(emitter.scheduler(), collector);
        emitter.addCancellable(cancellable);
        return cancellable;
    }

    Cancellable collect(Emitter<? super T> emitter) {
        return collect(emitter, CollectorHelper.from(emitter));
    }

    public Flow<T> flowOn(Scheduler upScheduler) {
        return new FlowFlowOn<>(this, upScheduler);
    }

    public <R> R to(Func1<? super Flow<T>, ? extends R> converter) {
        try {
            return converter.call(this);
        } catch (Throwable e) {
            throw Things.wrap(e);
        }
    }

    public <R> Flow<R> transform(FlowOperator<? super T, ? extends R> operator) {
        return new FlowTransform<>(this, operator);
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
    public final PolyFlow<T> polyWith(IFlow<T> flow, IFlow<T>... moreFlows) {
        ArrayList<IFlow<T>> flowList = new ArrayList<>();
        flowList.add(this);
        flowList.add(flow);
        flowList.addAll(Arrays.asList(moreFlows));
        return from(flowList).to(FlowX.poly());
    }

    @SafeVarargs
    public final Flow<T> concatWith(IFlow<T> flow, IFlow<T>... moreFlows) {
        return polyWith(flow, moreFlows).flatConcat();
    }

    @SafeVarargs
    public final Flow<T> mergeWith(IFlow<T> flow, IFlow<T>... moreFlows) {
        return polyWith(flow, moreFlows).flatMerge();
    }

    @SafeVarargs
    public final Flow<T> switchWith(IFlow<T> flow, IFlow<T>... moreFlows) {
        return polyWith(flow, moreFlows).flatSwitch();
    }

    @SafeVarargs
    public final Flow<List<T>> zipWith(IFlow<T> flow, IFlow<T>... moreFlows) {
        return polyWith(flow, moreFlows).flatZip();
    }

    public <R> Flow<R> map(Func1<? super T, ? extends R> mapper) {
        return transform(new FlowMap<>(mapper));
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
        return transform(new FlowToCollection<>(collection));
    }

    public Flow<List<T>> toList() {
        return toCollection(new ArrayList<>());
    }

    public Flow<T> filter(Predicate<? super T> predicate) {
        return FlowFilter.filter(this, predicate);
    }

    public Flow<T> ignoreElements() {
        return FlowFilter.ignoreElements(this);
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

    public Flow<T> skip(int count) {
        return FlowFilter.skip(this, count);
    }

    public Flow<T> skipLast(int count) {
        return transform(FlowBuffer.skipLast(count));
    }

    public Flow<T> throttleFirst(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        return transform(FlowThrottleFirst.throttleFirst(flowFactory));
    }

    public Flow<T> throttleFirst(IFlow<?> flow) {
        return transform(FlowThrottleFirst.throttleFirst(flow));
    }

    public Flow<T> throttleLast(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        return transform(FlowThrottleLast.throttleLast(flowFactory));
    }

    public Flow<T> throttleLast(IFlow<?> flow) {
        return transform(FlowThrottleLast.throttleLast(flow));
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
        return transform(FlowBuffer.takeLast(count));
    }

    public Flow<T> takeWhile(Predicate<? super T> predicate) {
        return transform(FlowTakeWhile.takeWhile(predicate));
    }

    public Flow<T> takeUntil(Predicate<? super T> predicate) {
        return FlowTakeUntil.takeUntil(this, predicate);
    }

    public Flow<T> takeUntil(T data) {
        return FlowTakeUntil.takeUntil(this, data);
    }

    public Flow<T> first(Predicate<? super T> predicate) {
        return FlowElementAt.first(this, predicate);
    }

    public Flow<T> first() {
        return FlowElementAt.first(this);
    }

    public Flow<T> elementAt(int index) {
        if (index < 0) return transform(FlowBuffer.lastElement(-index));
        return FlowElementAt.elementAt(this, index);
    }

    public Flow<T> last(Predicate<? super T> predicate) {
        return FlowFilter.last(this, predicate);
    }

    public Flow<T> last() {
        return last(Predicate.X.alwaysTrue());
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
        return FlowReduce.reduce(this, accumulator);
    }

    public Flow<T> timeout(IFlow<?> timeoutFlow, IFlow<T> fallback) {
        return new FlowTimeout<>(this, timeoutFlow, fallback);
    }

    public Flow<T> timeout(IFlow<?> timeoutFlow) {
        return timeout(timeoutFlow, Flow.error(new TimeoutException()));
    }

    public Flow<T> timeout(long timeout, TimeUnit unit, IFlow<T> fallback) {
        return timeout(Flow.timer(timeout, unit), fallback);
    }

    public Flow<T> timeout(long timeout, TimeUnit unit) {
        return timeout(Flow.timer(timeout, unit));
    }

    public Flow<T> delay(Func1<? super Reply<? extends T>, ? extends IFlow<?>> delayFunc) {
        return FlowDelay.delay(this, delayFunc);
    }

    public Flow<T> delay(IFlow<?> delayFlow) {
        return FlowDelay.delay(this, delayFlow);
    }

    public Flow<T> delayError(IFlow<?> delayFlow) {
        return delay(reply -> reply.isError() ? delayFlow : Flow.empty());
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

    public Flow<T> catchError(Action2<? super Throwable, ? super Emitter<? super T>> resumeAction1) {
        return FlowCatch.catchError(this, resumeAction1);
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

    public Flow<T> onBackpressure(Backpressure<T> backpressure) {
        return new FlowOnBackpressure<>(this, backpressure);
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

    public static <T> Flow<T> single(Func0<? extends T> func) {
        return FlowCreate.fromFunc(func);
    }

    public static <T> Flow<T> complete(Action0 action) {
        return FlowCreate.fromAction(action);
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

    public static <T> Flow<T> error(Throwable e) {
        return FlowCreate.error(e);
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
