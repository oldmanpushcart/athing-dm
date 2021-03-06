package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightStateChangedEvent extends ThingDmEvent<LightStateChangedEvent.Data> {

    public static final String ID = "light_state_changed_event";

    public LightStateChangedEvent(LightComp.State state) {
        super(Identifier.toIdentifier(LightComp.ID, ID), new Data(state));
    }

    public record Data(LightComp.State state) implements ThingDmData {
    }

}
