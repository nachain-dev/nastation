package org.nastation.components.global;

import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;
import lombok.extern.slf4j.Slf4j;
import org.nastation.module.pub.data.PageState;

@Slf4j
public class DefaultMessageProvider implements SystemMessagesProvider {

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
        CustomizedSystemMessages systemMessages = new CustomizedSystemMessages();

        systemMessages.setSessionExpiredURL("/PageStateView?type="+ PageState.SESSION_TIMEOUT);
        systemMessages.setSessionExpiredNotificationEnabled(true);

        log.error("DefaultMessageProvider - systemMessagesInfo: "+ systemMessagesInfo);

        return systemMessages;
    }
}