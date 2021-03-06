package io.github.athingx.athing.dm.thing.impl.util;

import io.github.athingx.athing.dm.common.util.MapData;
import io.github.athingx.athing.thing.api.op.OpData;

public class MapOpData extends MapData implements OpData {

    private transient final String token;

    public MapOpData(String token, MapData data) {
        this.token = token;
        putAll(data);
    }

    public MapOpData(String token) {
        this(token, new MapData());
    }

    @Override
    public String token() {
        return token;
    }

}
