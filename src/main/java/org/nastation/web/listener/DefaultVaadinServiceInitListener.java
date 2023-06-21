package org.nastation.web.listener;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.extern.slf4j.Slf4j;
import org.nastation.module.pub.view.HomeView;
import org.slf4j.LoggerFactory;

@Slf4j
public class DefaultVaadinServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {

        //event.addBootstrapListener(this);

        event.getSource().addUIInitListener(initEvent -> {
            LoggerFactory.getLogger(getClass()).debug("A new UI has been initialized!");
        });

        event.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!HomeView.class.equals(enterEvent.getNavigationTarget())){
                    //enterEvent.rerouteTo(HomeView.class);
                }
            });
        });

        /*
        event.addBootstrapListener(response -> {
            // BoostrapListener to change the bootstrap page
        });
        */

        event.addDependencyFilter((dependencies, filterContext) -> {
            // DependencyFilter to add/remove/change dependencies sent to
            // the client

            //log.info("addDependencyFilter() - dependencies:" +dependencies);

            return dependencies;
        });

        event.addRequestHandler((session, request, response) -> {
            // RequestHandler to change how responses are handled

            //log.info("addRequestHandler() - session:" +session);

            return false;
        });
    }


}