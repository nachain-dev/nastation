package org.nastation.module.dns.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.dns.data.Domain;
import org.nastation.module.dns.service.DomainService;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@PageTitle(DomainListView.Page_Title)
@Route(value = DomainListView.Route_Value + "/:domainID?/:action?(edit)", layout = MainLayout.class)
public class DomainListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "DomainList";
    public static final String Page_Title = "Domain List";

    private final String DOMAIN_ID = "domainID";
    private final String DOMAIN_EDIT_ROUTE_TEMPLATE = "DomainList/%d/edit";

    private Grid<Domain> grid = new Grid<>(Domain.class, false);

    private TextField name;
    private DateTimePicker regDate;
    private TextField regBlock;
    private TextField expireBlock;
    private TextField paymentAmount;
    private TextField paymentCoinType;
    private TextField nameserver1;
    private TextField nameserver2;
    private TextField nameserver3;
    private TextField txhash;
    private TextField accountAddress;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Domain> binder;

    private Domain domain;

    private DomainService domainService;

    public DomainListView(@Autowired DomainService domainService) {
        addClassNames("domain-list-view", "flex", "flex-col", "h-full");
        this.domainService = domainService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);

        grid.addColumn("paymentAmount").setAutoWidth(true);
        grid.addColumn("paymentCoinType").setAutoWidth(true);
        //grid.addColumn("nameserver1").setAutoWidth(true);
        //grid.addColumn("nameserver2").setAutoWidth(true);
        //grid.addColumn("nameserver3").setAutoWidth(true);
        grid.addColumn("txhash").setAutoWidth(true).setHeader("Tx Hash");
        ;
        grid.addColumn("accountAddress").setAutoWidth(true);
        //grid.addColumn("regDate").setAutoWidth(true);
        grid.addColumn("regBlock").setAutoWidth(true);
        grid.addColumn("expireBlock").setAutoWidth(true).setHeader("Expire Block");
        ;

        //grid.setItems(query -> domainService.list(
        //        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
        //        .stream());        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DOMAIN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DomainListView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Domain.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(regBlock).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
        //        .bind("regBlock");

        binder.forField(paymentAmount).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("paymentAmount");

        //binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
/*                if (this.domain == null) {
                    this.domain = new Domain();
                }
                binder.writeBean(this.domain);

                domainService.update(this.domain);
                clearForm();
                refreshGrid();
                Notification.show("Domain details stored.");*/
                UI.getCurrent().navigate(DomainListView.class);
            } catch (Exception validationException) {
                Notification.show("An exception happened while trying to store the domain details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> domainId = event.getRouteParameters().getInteger(DOMAIN_ID);
        if (domainId.isPresent()) {
            Optional<Domain> domainFromBackend = domainService.get(domainId.get());
            if (domainFromBackend.isPresent()) {
                populateForm(domainFromBackend.get());
            } else {
                Notification.show(String.format("The requested domain was not found, ID = %d", domainId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DomainListView.class);
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

        paymentAmount = new TextField("Payment Amount");
        paymentCoinType = new TextField("Payment Coin Type");
        //nameserver1 = new TextField("Nameserver1");
        //nameserver2 = new TextField("Nameserver2");
        //nameserver3 = new TextField("Nameserver3");
        txhash = new TextField("TxHash");
        accountAddress = new TextField("Account Address");
        //regDate = new DateTimePicker("Reg Date");
        //regDate.setStep(Duration.ofSeconds(1));
        regBlock = new TextField("Reg Block");
        expireBlock = new TextField("Expire Block");
        //expireBlock.setStep(Duration.ofSeconds(1));

        Component[] fields = new Component[]{name, paymentAmount, paymentCoinType, txhash, accountAddress,/*regDate, */regBlock, expireBlock};

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

    private void populateForm(Domain value) {
        this.domain = value;
        binder.readBean(this.domain);

    }
}
