package io.github.athingx.athing.dm.thing.impl.bind;

import com.google.gson.reflect.TypeToken;
import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFnMap.identity;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonFromBytes;
import static io.github.athingx.athing.thing.api.function.ThingFnMapOpReply.mappingOpReplyFromJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ThingThDmBindForPropertyCaller implements ThingThDmBind<OpCaller<OpData, OpReply<Void>>> {

    private final Thing thing;
    private final ThingDmOption option;

    public ThingThDmBindForPropertyCaller(Thing thing, ThingDmOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCaller<OpData, OpReply<Void>>> bind(OpGroupBind group) {
        return group
                .bind("/sys/%s/thing/event/property/post_reply".formatted(thing.path().toURN()))
                .map(mappingJsonFromBytes(UTF_8))
                .map(mappingOpReplyFromJson(new TypeToken<OpReply<Void>>() {

                }))
                .call(new OpBind.Option().setTimeoutMs(option.getPropertyTimeoutMs()), identity());
    }

}