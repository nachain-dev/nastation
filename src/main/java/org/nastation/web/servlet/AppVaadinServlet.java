package org.nastation.web.servlet;

import com.vaadin.flow.server.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;

@Slf4j
public class AppVaadinServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        log.info("sessionInit = " + "sessionInit");
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        log.info("sessionDestroy = " + "sessionDestroy");
    }
}