package org.nastation.common.event.cross;

import com.google.common.eventbus.Subscribe;

public interface BlockHeightChangeEventListener<T> {

    @Subscribe
    void handle(T event);

}