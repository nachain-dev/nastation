package org.nastation.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.SystemService;
import org.nastation.data.service.TraceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringClosedListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private TraceDataService traceDataService;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.warn("ContextClosedEvent >> setStopRun(true)");

        SystemService.me().setStopRun(true);

        HttpResult result = traceDataService.postTrace("offline");
        log.info("postTrace in Listener :" + result.toString());
    }

}