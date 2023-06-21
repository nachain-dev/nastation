package org.nastation.common.event;

import com.vaadin.flow.component.Component;
import org.nachain.core.chain.structure.instance.Instance;

public class InstanceChangeEvent extends InstanceEvent {

    public InstanceChangeEvent(Component source, Instance instance) {
        super(source, instance);
    }
}