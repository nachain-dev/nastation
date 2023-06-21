package org.nastation.common.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import org.nachain.core.chain.structure.instance.Instance;


public abstract class InstanceEvent extends ComponentEvent<Component> {

    private Instance instance;

    protected InstanceEvent(Component source, Instance instance) {
        super(source, false);
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }
}
