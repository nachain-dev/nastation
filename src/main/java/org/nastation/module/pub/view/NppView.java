package org.nastation.module.pub.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;

@Route(value = NppView.Route_Value, layout = MainLayout.class)
@PageTitle(NppView.Page_Title)
public class NppView extends VerticalLayout {

    public static final String Route_Value = "NppView";
    public static final String Page_Title = "N++ Editor";

    public NppView() {
        setPadding(true);
        setWidthFull();

        AceEditor ace = new AceEditor();
        ace.setTheme(AceTheme.terminal);
        ace.setMode(AceMode.sql);
        ace.setValue("SELECT * FROM test");

        ace.setWidth("100%");
        ace.setHeight("800px");

        add(ace);

    }

}
