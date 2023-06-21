package org.nastation.common.service;

import com.google.common.eventbus.EventBus;
import org.nastation.common.event.cross.BlockHeightChangeEvent;
import org.nastation.common.event.cross.BlockHeightChangeEventListener;

/**
 * @author John | NaChain
 * @since 01/15/2022 21:02
 */
public class EventBusCenter {

    public static final EventBus eventBus = new EventBus();

    private static EventBusCenter INSTANCE = new EventBusCenter();

    public static EventBusCenter me(){ return INSTANCE; }

    public void post(String text) {
        eventBus.post(text);
    }

    public void unregister(Object obj) {
        eventBus.unregister(obj);
    }

    public void register(Object obj) {
        eventBus.register(obj);
    }
    

    public void post(BlockHeightChangeEvent event) {
        eventBus.post(event);
    }

    public void unregister(BlockHeightChangeEventListener listener) {
        eventBus.unregister(listener);
    }

    public void register(BlockHeightChangeEventListener listener) {
        eventBus.register(listener);
    }
    
    
    
}
