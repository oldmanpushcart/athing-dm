package io.github.athingx.athing.dm.qatest;

import io.github.athingx.athing.dm.common.ThingDmCodes;
import io.github.athingx.athing.dm.platform.domain.SortOrder;
import io.github.athingx.athing.dm.platform.helper.OpReturn;
import io.github.athingx.athing.dm.platform.helper.OpReturnHelper;
import io.github.athingx.athing.dm.platform.message.ThingDmReplyPropertySetMessage;
import io.github.athingx.athing.dm.platform.message.ThingDmReplyServiceReturnMessage;
import io.github.athingx.athing.dm.qatest.puppet.EchoComp;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static io.github.athingx.athing.dm.api.Identifier.toIdentifier;

public class ThingDmTemplateTestCase extends PuppetSupport {

    @Test
    public void test$template$service$invoke$success() throws Exception {

        final var echoComp = thingDmTemplate.getThingDmComp("echo", EchoComp.class);
        final var req = new EchoComp.Echo("ECHO-ECHO:" + System.currentTimeMillis());

        final var syncResp = echoComp.syncEcho(req);
        Assert.assertEquals(req.words(), syncResp.words());

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() -> {
            final var future = echoComp.asyncEcho(req);
            Assert.assertFalse(future.isCancelled());
            Assert.assertFalse(future.isCompletedExceptionally());
        });
        final ThingDmReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.token());
        Assert.assertEquals(opReturn.token(), message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertEquals(ThingDmCodes.OK, message.getCode());
        Assert.assertTrue(message.getData() instanceof EchoComp.Echo);
        if (message.getData() instanceof EchoComp.Echo asyncResp) {
            Assert.assertEquals(req.words(), asyncResp.words());
        }

    }

    @Test
    public void test$template$property$set$success() throws Exception {

        final var lightComp = thingDmTemplate.getThingDmComp("light", LightComp.class);
        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() -> lightComp.setColor(LightComp.Color.YELLOW));
        final ThingDmReplyPropertySetMessage message = waitingForReplyMessageByToken(opReturn.token());
        Assert.assertEquals(opReturn.token(), message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertEquals(ThingDmCodes.OK, message.getCode());

    }

    @Test
    public void test$template$property$batch_set$success() throws Exception {

        final var colorId = toIdentifier("light", "color");
        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                thingDmTemplate.batchSetProperties(new HashMap<>() {{
                    put(colorId, LightComp.Color.PINK);
                }}));

        final ThingDmReplyPropertySetMessage message = waitingForReplyMessageByToken(opReturn.token());
        Assert.assertEquals(opReturn.token(), message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertEquals(ThingDmCodes.OK, message.getCode());

    }

    @Test
    public void test$template$property$get$success() throws Exception {

        // ?????????????????????
        final var lightComp = thingDmTemplate.getThingDmComp("light", LightComp.class);
        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() -> lightComp.setColor(LightComp.Color.BLUE));
        Assert.assertNotNull(waitingForReplyMessageByToken(opReturn.token()));

        // ???????????????????????????
        final var colorId = toIdentifier("light", "color");
        Assert.assertNotNull(waitingForPostMessageByToken(thingDm.properties(colorId).get().token()));

        // ??????????????????
        Thread.sleep(500);

        // ???????????????????????????
        Assert.assertEquals(LightComp.Color.BLUE, lightComp.getColor());

    }

    @Test
    public void test$template$property$batch_get$success() throws Exception {

        // ?????????????????????
        final var lightComp = thingDmTemplate.getThingDmComp("light", LightComp.class);
        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() -> lightComp.setColor(LightComp.Color.PINK));
        Assert.assertNotNull(waitingForReplyMessageByToken(opReturn.token()));

        // ???????????????????????????
        final var colorId = toIdentifier("light", "color");
        Assert.assertNotNull(waitingForPostMessageByToken(thingDm.properties(colorId).get().token()));

        // ??????????????????
        Thread.sleep(500);

        final var propertyMap = thingDmTemplate.batchGetProperties(new HashSet<>() {{
            add(colorId);
        }});

        Assert.assertTrue(propertyMap.containsKey(colorId));
        Assert.assertNotNull(propertyMap.get(colorId).getValue());
        if (propertyMap.get(colorId).getValue() instanceof LightComp.Color color) {
            Assert.assertEquals(LightComp.Color.PINK, color);
        }

    }

    @Test
    public void test$template$property$iterator_get$success() throws Exception {

        final var colorId = toIdentifier("light", "color");
        final var iterator = thingDmTemplate.iteratorForPropertySnapshot(colorId, 10, SortOrder.DESCENDING);
        Assert.assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            final var snapshot = iterator.next();
            Assert.assertEquals(colorId, snapshot.getIdentifier());
            Assert.assertTrue(snapshot.getTimestamp() > 0);
            Assert.assertNotNull(snapshot.getValue());
            Assert.assertTrue(snapshot.getValue() instanceof LightComp.Color);
        }

    }

}
