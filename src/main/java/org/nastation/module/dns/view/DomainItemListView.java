package org.nastation.module.dns.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.nastation.module.dns.data.DomainDnsItem;
import org.nastation.module.dns.service.DomainDnsitemService;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@PageTitle("DomainItemList")
@Route(value = "DomainItemList/:domainDnsitemID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class DomainItemListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "DomainItemList";
    public static final String Page_Title = "Domain Dns List";

    private final String DOMAINDNSITEM_ID = "domainDnsitemID";
    private final String DOMAINDNSITEM_EDIT_ROUTE_TEMPLATE = "DomainItemList/%d/edit";

    private Grid<DomainDnsItem> grid = new Grid<>(DomainDnsItem.class, false);

    private TextField domainId;
    private TextField host;
    private TextField recordType;
    private TextField address;
    private TextField priority;
    private Checkbox enable;
    private TextField accountAddress;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<DomainDnsItem> binder;

    private DomainDnsItem domainDnsitem;

    private DomainDnsitemService domainDnsitemService;

    public DomainItemListView(@Autowired DomainDnsitemService domainDnsitemService) {
        addClassNames("domain-item-list-view", "flex", "flex-col", "h-full");
        this.domainDnsitemService = domainDnsitemService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("domainId").setAutoWidth(true);
        grid.addColumn("host").setAutoWidth(true);
        grid.addColumn("recordType").setAutoWidth(true);
        grid.addColumn("address").setAutoWidth(true);
        grid.addColumn("priority").setAutoWidth(true);
        TemplateRenderer<DomainDnsItem> enableRenderer = TemplateRenderer.<DomainDnsItem>of(
                "<iron-icon hidden='[[!item.enable]]' icon='vaadin:check' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);'></iron-icon><iron-icon hidden='[[item.enable]]' icon='vaadin:minus' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);'></iron-icon>")
                .withProperty("enable", DomainDnsItem::isEnable);
        grid.addColumn(enableRenderer).setHeader("Enable").setAutoWidth(true);

        grid.addColumn("accountAddress").setAutoWidth(true);

        grid.setItems(query -> domainDnsitemService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        //grid.setDataProvider(new CrudServiceDataProvider<>(domainDnsitemService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DOMAINDNSITEM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DomainItemListView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DomainDnsItem.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(domainId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("domainId");
        binder.forField(priority).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("priority");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.domainDnsitem == null) {
                    this.domainDnsitem = new DomainDnsItem();
                }
                binder.writeBean(this.domainDnsitem);

                domainDnsitemService.update(this.domainDnsitem);
                clearForm();
                refreshGrid();
                Notification.show("Domain dns item details stored.");
                UI.getCurrent().navigate(DomainItemListView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the domain dns item details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> domainDnsitemId = event.getRouteParameters().getInteger(DOMAINDNSITEM_ID);
        if (domainDnsitemId.isPresent()) {
            Optional<DomainDnsItem> domainDnsitemFromBackend = domainDnsitemService.get(domainDnsitemId.get());
            if (domainDnsitemFromBackend.isPresent()) {
                populateForm(domainDnsitemFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested domain dns item was not found, ID = %d", domainDnsitemId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DomainItemListView.class);
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
        domainId = new TextField("Domain Id");
        host = new TextField("Host");
        recordType = new TextField("Record Type");
        address = new TextField("Address");
        priority = new TextField("Priority");
        enable = new Checkbox("Enable");
        enable.getStyle().set("padding-top", "var(--lumo-space-m)");
        accountAddress = new TextField("Account Address");
        Component[] fields = new Component[]{domainId, host, recordType, address, priority, enable, accountAddress};

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
        buttonLayout.add(save, cancel);
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

    private void populateForm(DomainDnsItem value) {
        this.domainDnsitem = value;
        binder.readBean(this.domainDnsitem);

    }
}
