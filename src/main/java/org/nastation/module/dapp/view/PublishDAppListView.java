package org.nastation.module.dapp.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.extern.slf4j.Slf4j;
import org.nastation.module.dapp.data.DApp;
import org.nastation.module.dapp.service.DAppService;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.Optional;

@Route(value = PublishDAppListView.Route_Value+ "/:dAppID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(PublishDAppListView.Page_Title) @Slf4j
public class PublishDAppListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "PublishDAppListView";
    public static final String Page_Title = "DApp List";

    private final String DAPP_ID = "dAppID";
    private final String DAPP_EDIT_ROUTE_TEMPLATE = "PublishDAppListView/%d/edit";

    private Grid<DApp> grid = new Grid<>(DApp.class, false);

    private TextField icon;
    private TextField name;
    private TextField domain;
    private TextField fileSize;
    private TextField type;
    private TextField status;
    private DateTimePicker addTime;

    private Button cancel = new Button("Cancel");
    private Button visit = new Button("Visit");

    //private CollaborationBinder<DApp> binder;

    private DApp dApp;

    private DAppService dAppService;

    public PublishDAppListView(@Autowired DAppService dAppService) {
        this.dAppService = dAppService;
        addClassNames("publish-d-app-list-view-view", "flex", "flex-col", "h-full");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        //UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("icon").setAutoWidth(true);
        grid.addColumn("domain").setAutoWidth(true);
        grid.addColumn("fileSize").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.addColumn("addTime").setAutoWidth(true);

        grid.setItems(query -> dAppService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DAPP_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PublishDAppListView.class);
            }
        });

        // Configure Form
        //binder = new CollaborationBinder<>(DApp.class, userInfo);

        // Bind fields. This where you'd define e.g. validation rules

        //binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        visit.addClickListener(e -> {
            try {
                /*
                if (this.dApp == null) {
                    this.dApp = new DApp();
                }
                binder.writeBean(this.dApp);
                dAppService.update(this.dApp);
                clearForm();
                refreshGrid();
                UI.getCurrent().navigate(PublishDAppListView.class);
                */
            } catch (Exception ex) {
                Notification.show("An exception happened while trying to store the dApp details.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> dAppId = event.getRouteParameters().getInteger(DAPP_ID);
        if (dAppId.isPresent()) {
            Optional<DApp> dAppFromBackend = dAppService.get(dAppId.get());
            if (dAppFromBackend.isPresent()) {
                populateForm(dAppFromBackend.get());
            } else {
                Notification.show(String.format("The requested dApp was not found, ID = %d", dAppId.get()), 3000,Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PublishDAppListView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        icon = new TextField("Icon");
        domain = new TextField("Domain");
        fileSize = new TextField("File Size");
        type = new TextField("Type");
        status = new TextField("Status");
        addTime = new DateTimePicker("Time");

        addTime.setStep(Duration.ofSeconds(1));
        Component[] fields = new Component[]{icon, name, domain,fileSize,type, status, addTime};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add( formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        visit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(visit, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(DApp value) {
        this.dApp = value;
        String topic = null;
        if (this.dApp != null && this.dApp.getId() != null) {
            topic = "dApp/" + this.dApp.getId();
        } else {
        }
        //binder.setTopic(topic, () -> this.dApp);
    }
}