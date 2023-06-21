package org.nastation.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.nastation.common.service.SystemService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringStoppedListener implements ApplicationListener<ContextStoppedEvent> {

	@Override
	public void onApplicationEvent(ContextStoppedEvent event) {
		log.warn("ContextStoppedEvent >> setStopRun(true)");
		SystemService.me().setStopRun(true);
	}

}