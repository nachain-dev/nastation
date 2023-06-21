package org.nastation.module.address.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.module.address.data.Address;
import org.nastation.module.address.service.AddressService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Slf4j
@Route(value = AddressListView.Route_Value + "/:addressID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(AddressListView.Page_Title)
public class AddressListView extends Div implements BeforeEnterObserver {

    private final String ADDRESS_ID = "addressID";
    private final String ADDRESS_EDIT_ROUTE_TEMPLATE = "AddressList/%d/edit";

    public static final String Route_Value = "AddressList";
    public static final String Page_Title = "Address Book";

    private Grid<Address> grid = new Grid<>(Address.class, false);

    private TextField address;
    private TextField label;
    private TextField description;
    private TextField coinType;

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button newBtn = new Button("New");

    private BeanValidationBinder<Address> binder;

    private Address _address;

    private AddressService addressService;
    private WalletService walletService;
    private EcologyUrlService ecologyUrlService;

    public AddressListView(@Autowired AddressService addressService,@Autowired WalletService walletService,@Autowired EcologyUrlService ecologyUrlService) {
        this.addressService = addressService;
        this.walletService = walletService;
        this.ecologyUrlService = ecologyUrlService;

        addClassNames("address-list-view", "flex", "flex-col", "h-full");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        //UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "NA");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        //avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        //avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("ID");
        grid.addColumn("label").setAutoWidth(true).setHeader("Label");
        grid.addColumn("address").setAutoWidth(true).setHeader("Address");
        grid.setItems(query -> addressService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        /*Action*/
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, rowData) -> {

            Button copyBtn = new Button("Copy");
            copyBtn.addClickListener(e -> {
                CompUtil.setClipboardText(rowData.getAddress());
            });
            Button viewBtn = new Button("View");
            viewBtn.addClickListener(e -> {
                long currentInstanceId = this.walletService.getCurrentInstanceId();
                String url = ecologyUrlService.buildAccountUrlByScan(rowData.getAddress(), currentInstanceId);
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(copyBtn,viewBtn);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ADDRESS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AddressListView.class);
            }
        });

        this.addListener(SaveEvent.class, this::saveActionListener);

        // Configure Form
        binder = new BeanValidationBinder<Address>(Address.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(coinType, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("coinType");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        newBtn.addClickListener(e -> {
            UI.getCurrent().navigate(AddressFormView.class);
        });

        save.addClickListener(e -> {
            try {
                if (this._address == null) {
                    this._address = new Address();
                }

                binder.writeBean(this._address);

                // check label
                if (this._address.getId() == null) {

                    String label = this._address.getLabel();
                    Address addressEntity = addressService.getRepository().findByLabel(label);
                    if (addressEntity != null) {
                        CompUtil.showError("Label already exists");
                        return;
                    }

                }

                // check label
                String address = this._address.getAddress();
                if (StringUtils.isNotBlank(address)) {
                    if (!WalletUtil.isAddressValid(address)) {
                        CompUtil.showError("Incorrect address format");
                        return;
                    }

                }

                fireEvent(new SaveEvent(this, this._address));

                addressService.update(this._address);

                clearForm();
                refreshGrid();
                CompUtil.showSuccess("Address details stored.");
                UI.getCurrent().navigate(AddressListView.class);
            } catch (Exception ex) {
                String msg = String.format("Store the address details error: " + ex.getMessage());
                log.error(msg, ex);
                CompUtil.showError(msg);
            }
        });
    }

    private void saveActionListener(SaveEvent event) {
        Address addr = event.getContact();
        //System.out.println("saveActionListener() address = " + addr);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> addressId = event.getRouteParameters().getInteger(ADDRESS_ID);
        if (addressId.isPresent()) {
            Optional<Address> addressFromBackend = addressService.get(addressId.get());
            if (addressFromBackend.isPresent()) {
                populateForm(addressFromBackend.get());
            } else {
                CompUtil.showError(String.format("The requested address was not found, ID = %d", addressId.get()));
                refreshGrid();
                event.forwardTo(AddressListView.class);
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
        address = new TextField("Address");
        label = new TextField("Label");
        //description = new TextField("Description");
        //coinType = new TextField("Coin Type");
        Component[] fields = new Component[]{label, address/*, description, coinType*/};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        //editorDiv.add(avatarGroup, formLayout);
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
        newBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        buttonLayout.add(save, cancel/*,newBtn*/);
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

    private void populateForm(Address value) {
        this._address = value;
        binder.readBean(value);

        /*
        String topic = null;
        if (this.address != null && this.address.getId() != null) {
            topic = "address/" + this._address.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this._address);
        avatarGroup.setTopic(topic);
        */
    }


    public static abstract class ContactFormEvent extends ComponentEvent<AddressListView> {
        private Address Address;

        protected ContactFormEvent(AddressListView source, Address Address) {
            super(source, false);
            this.Address = Address;
        }

        public Address getContact() {
            return Address;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(AddressListView source, Address Address) {
            super(source, Address);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}