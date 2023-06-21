package org.nastation.module.pub.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.util.JsonUtil;
import org.nastation.data.config.AppConfig;
import org.nastation.module.pub.config.ChangelogConfig;
import org.nastation.module.pub.data.ChangelogJson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = ChangelogView.Route_Value, layout = MainLayout.class)
@PageTitle(ChangelogView.Page_Title)
@Slf4j
public class ChangelogView extends VerticalLayout {

    public static final String Route_Value = "ChangelogView";
    public static final String Page_Title = "Changelog";

    public ChangelogView(@Autowired AppConfig appConfig, @Autowired ChangelogConfig changelogConfig) {
        addClassName("app-about-view");

        this.getStyle()
                .set("display", "block")
                .set("padding-top", "2em")
                .set("text-align", "center");

        H3 title = new H3(Page_Title);
        add(title);

        Accordion accordion = new Accordion();

        List<String> list = changelogConfig.getList();

        List<String> copyList = new ArrayList<String>(list);
        Collections.reverse(copyList);

        for (String json : copyList) {

            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(true);

            String date = "";
            String version = "";

            try {
                ChangelogJson changelogJson = JsonUtil.parseObjectByGson(json, ChangelogJson.class);
                String content = changelogJson.getContent();

                date = changelogJson.getDate();
                version = changelogJson.getVersion();

                String[] split = content.split(";");
                for (String line : split) {
                    if (StringUtils.isEmpty(line)) {
                        continue;
                    }

                    Paragraph text = new Paragraph(line);
                    layout.add(text);
                }

            } catch (Exception e) {
                log.error("parse change log error", e);
            }

            accordion.add(version + " / " + date, layout);
        }

        add(accordion);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

}
