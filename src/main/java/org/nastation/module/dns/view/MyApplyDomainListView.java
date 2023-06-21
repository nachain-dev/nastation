package org.nastation.module.dns.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.dns.data.DomainApply;
import org.nastation.module.dns.service.DomainApplyService;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@PageTitle(MyApplyDomainListView.Page_Title)
@Route(value = MyApplyDomainListView.Route_Value + "/:domainID?/:action?(edit)", layout = MainLayout.class)
public class MyApplyDomainListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "MyApplyDomainList";
    public static final String Page_Title = "My Apply Domain List";

    private final String DOMAIN_ID = "ID";
    private final String DOMAIN_EDIT_ROUTE_TEMPLATE = "MyApplyDomainList/%d/edit";

    private Grid<DomainApply> grid = new Grid<>(DomainApply.class, false);

    private TextField id;
    private TextField domainName;
    private TextField contract;
    private TextField blockYear;
    private TextField startBlock;
    private TextField endBlock;
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<DomainApply> binder;
    private DomainApply domainApply;
    private DomainApplyService domainApplyService;

    public MyApplyDomainListView(@Autowired DomainApplyService domainApplyService) {
        addClassNames("domain-list-view", "flex", "flex-col", "h-full");
        this.domainApplyService = domainApplyService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);

        grid.addColumn("domainName").setAutoWidth(true);
        grid.addColumn("contract").setAutoWidth(true).setHeader("DApp Contract");
        grid.addColumn("blockYear").setAutoWidth(true);
        grid.addColumn("startBlock").setAutoWidth(true);
        grid.addColumn("endBlock").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, rowData) -> {

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DOMAIN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MyApplyDomainListView.class);
            }
        });

        // Configure Form
        //binder = new BeanValidationBinder<>(Domain.class);
        //binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                UI.getCurrent().navigate(MyApplyDomainListView.class);
            } catch (Exception validationException) {
                Notification.show("An exception happened while trying to store the domain details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> domainId = event.getRouteParameters().getInteger(DOMAIN_ID);
        if (domainId.isPresent()) {
            Optional<DomainApply> domainFromBackend = domainApplyService.get(domainId.get());
            if (domainFromBackend.isPresent()) {
                populateForm(domainFromBackend.get());
            } else {
                Notification.show(String.format("The requested domain was not found, ID = %d", domainId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MyApplyDomainListView.class);
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

        id = new TextField("ID");
        domainName = new TextField("Domain Name");
        contract = new TextField("DApp Contract");
        blockYear = new TextField("Block Year");
        startBlock = new TextField("Start Block");
        endBlock = new TextField("End Block");

        Component[] fields = new Component[]{id, domainName, blockYear, startBlock, endBlock
        };

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //buttonLayout.add(save, cancel);
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
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(DomainApply value) {
        this.domainApply = value;
        //binder.readBean(this.domainApply);
    }
}
