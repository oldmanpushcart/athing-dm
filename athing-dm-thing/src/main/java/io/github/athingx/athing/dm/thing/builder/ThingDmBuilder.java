package io.github.athingx.athing.dm.thing.builder;

import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.bind.*;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCaller;
import io.github.athingx.athing.thing.api.op.OpData;
import io.github.athingx.athing.thing.api.op.OpGroupBind;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static io.github.athingx.athing.thing.api.util.CompletableFutureUtils.tryCatchCompleted;

public class ThingDmBuilder {

    private ThingDmOption option = new ThingDmOption();

    public ThingDmBuilder option(ThingDmOption option) {
        this.option = option;
        return this;
    }

    public CompletableFuture<ThingDm> build(Thing thing) {
        final OpGroupBind group = thing.op().group();
        final ThingDmCompContainer container = new ThingDmCompContainer(thing.path());

        // 批量绑定
        Stream.of(new ThingDmBind[]{
                new ThingDmBindForPropertySet(thing, container),
                new ThingDmBindForServiceAsync(thing, container),
                new ThingDmBindForPropertySet(thing, container),
                new ThingDmBindForServiceSync(thing, container)
        }).forEach(bind -> bind.bind(group));

        // 绑定事件上报呼叫
        final CompletableFuture<OpCaller<OpData, OpReply<Void>>> eCallerFuture
                = new ThingDmBindForEventCaller(thing, option)
                .bind(group);

        // 绑定属性上报呼叫
        final CompletableFuture<OpCaller<OpData, OpReply<Void>>> pCallerFuture
                = new ThingDmBindForPropertyCaller(thing, option)
                .bind(group);

        // 提交绑定
        return group
                .commit()
                .thenCompose(binder -> tryCatchCompleted(() -> new ThingDmImpl(
                        thing,
                        container,
                        eCallerFuture.get(),
                        pCallerFuture.get()
                )));
    }

}