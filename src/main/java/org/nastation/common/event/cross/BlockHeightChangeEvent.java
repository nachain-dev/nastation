package org.nastation.common.event.cross;

import lombok.Data;

@Data
public class BlockHeightChangeEvent{

    private long instanceId;

    private long height;

    public BlockHeightChangeEvent() {
    }

    public BlockHeightChangeEvent(long instanceId, long height) {
        this.instanceId = instanceId;
        this.height = height;
    }
}