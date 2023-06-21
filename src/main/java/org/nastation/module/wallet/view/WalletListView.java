package org.nastation.module.wallet.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.crypto.bip39.Language;
import org.nachain.core.wallet.WalletUtils;
import org.nachain.core.wallet.keystore.Keystore;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.util.*;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.KeystoreWrapper;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletRow;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.nastation.module.wallet.vo.TokenBalanceGridRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.*;

@Route(value = WalletListView.Route_Value + "/:walletID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(WalletListView.Page_Title)
@Slf4j
public class WalletListView extends Div implements BeforeEnterObserver {

    public static final String Page_Title = "Wallet List";
    public static final String Route_Value = "WalletList";
    private final String WALLET_ID = "walletID";
    private final String WALLET_EDIT_ROUTE_TEMPLATE = Route_Value + "/%d/edit";

    private Grid<WalletRow> grid = new Grid<>(WalletRow.class, false);

    private TextField walletNameSearchItem;
    private TextField walletAddressSearchItem;


    private TextField name;
    private TextField address;
    private TextField pswTip;
    private TextField addTimeText;
    private Checkbox commonNode;
    private Checkbox fullNode;
    private Checkbox defaultWallet;
    private Checkbox hasBackup;

    private Button prevPageBtn;
    private Button nextPageBtn;
    private Button searchBtn;
    private Button clearBtn;
    private Button importBtn;
    private Button backupBtn;
    private Button profileBtn;
    private Button setDefaultBtn;

    private TextField nacBalance;
    private TextField nomcBalance;
    private TextField usdnBalance;

    private Button refresh = new Button("Refresh");
    private Button save = new Button("Save");

    private BeanValidationBinder<WalletRow> binder;

    private WalletRow walletRow;
    private WalletService walletService;
    private WalletRepository walletRepository;
    private NaScanHttpService naScanHttpService;
    private EcologyUrlService ecologyUrlService;

    private Integer selectId = 0;

    public WalletListView(
            @Autowired WalletDataService walletDataService,
            @Autowired EcologyUrlService ecologyUrlService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired WalletRepository walletRepository,
            @Autowired WalletService walletService
    ) {
        addClassNames("wallet-list-view", "flex", "flex-col", "h-full");
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.naScanHttpService = naScanHttpService;
        this.ecologyUrlService = ecologyUrlService;

        long currentInstanceId = walletService.getCurrentInstanceId();

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        //splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("ID");
        //grid.addColumn("name").setAutoWidth(true).setHeader("Name");

/*
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, wallet) -> {
            //DicebearVaadin dice = new DicebearVaadin();
            //dice.setStyle(Constants.Style.avataaars);
            //
            //Options options = new Options();
            //options.setRadius(5);
            //options.setMargin(2);
            //options.setWidth(50);
            //options.setHeight(50);
            //options.setBackground("transparent");
            //options.setValue(wallet.getName());
            //div.add(dice);

            //String walletName = CompUtil.subAbbreviation(wallet.getName());
            //Avatar avatarName = new Avatar(CompUtil.subAbbreviation(walletName));

            //Icon icon = new Icon(VaadinIcon.WALLET);
            //icon.setSize("20px");

            HorizontalLayout layout = new HorizontalLayout();
            //layout.add(icon);
            layout.add(new Span(wallet.getName()));

            div.add(layout);

        })).setAutoWidth(true).setHeader("Name").setResizable(true);
*/

        grid.addComponentColumn(row -> createNameBadge(row)).setAutoWidth(true).setHeader("Name");

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, wallet) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(wallet.getAddress()));
            button.addClickListener(e -> {
                String url = ecologyUrlService.buildAccountUrlByScan(wallet.getAddress(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });
        })).setAutoWidth(true).setHeader("Address").setResizable(true);

        //grid.addColumn("nacBalance").setAutoWidth(true).setHeader("NAC");
        //grid.addColumn("nomcBalance").setAutoWidth(true).setHeader("NOMC");
        //grid.addColumn("usdnBalance").setAutoWidth(true).setHeader("USDN");

        grid.addColumn("addTimeText").setAutoWidth(true).setHeader("Add Time");

/*
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, wallet) -> {
            boolean defaultWallet = wallet.isDefaultWallet();
            String theme = String.format("badge %s", defaultWallet ? "success" : "");
            span.getElement().setAttribute("theme", theme);
            span.setText(defaultWallet ? "YES" : "-");
        })).setAutoWidth(true).setHeader("Default").setResizable(true);
*/
        ;

        /*
        grid.setItems(query -> walletService.get(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        */

        /*Action*/
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, rowData) -> {

            Button viewBtn = new Button("Balance");
            viewBtn.addClickListener(e -> {
                viewBalance2(rowData);
            });

            Button visitBtn = new Button("Visit");
            visitBtn.addClickListener(e -> {
                long currentInstanceId2 = this.walletService.getCurrentInstanceId();
                String url = ecologyUrlService.buildAccountUrlByScan(rowData.getAddress(), currentInstanceId2);
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(viewBtn,visitBtn);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {

                selectId = event.getValue().getId();
                UI.getCurrent().navigate(String.format(WALLET_EDIT_ROUTE_TEMPLATE, selectId));

            } else {
                UI.getCurrent().navigate(WalletListView.class);
            }
        });

        GridContextMenu<WalletRow> menu = grid.addContextMenu();
        menu.addItem("Copy address", event -> {
            Optional<WalletRow> row = event.getItem();
            if (row.isPresent()) {
                selectId = row.get().getId();

                String address = row.get().getAddress();
                CompUtil.setClipboardText(address);
            }
        });
        menu.addItem("Set default", event -> {
            Optional<WalletRow> row = event.getItem();
            if (row.isPresent()) {
                selectId = row.get().getId();
                walletService.setDefaultWallet(row.get().getId());
                CompUtil.showSuccess("Default wallet: " + row.get().getName());
                refreshGrid();
            }
        });
        menu.addItem("View profile", event -> {
            Optional<WalletRow> row = event.getItem();
            if (row.isPresent()) {
                selectId = row.get().getId();
                viewBtnClick();
            }
        });
        menu.addItem("Change Password", event -> {
            Optional<WalletRow> row = event.getItem();
            if (row.isPresent()) {
                WalletRow walletRow1 = row.get();
                selectId = walletRow1.getId();
                changePasswordBtnClick(walletRow1);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<WalletRow>(WalletRow.class);
        binder.bindInstanceFields(this);

        refreshGrid();

        refresh.addClickListener(e -> {
            refreshGrid();
        });

        /*
        save.addClickListener(e -> {
            try {
                if (this.wallet == null) {
                    this.wallet = new Wallet();
                }
                binder.writeBean(this.wallet);

                //walletService.update(this.wallet);

                clearForm();
                refreshGrid();

                CompUtil.showSuccess("Wallet details stored");
                UI.getCurrent().navigate(WalletListView.class);
            } catch (ValidationException ex) {
                CompUtil.showError("Error happened while trying to store the wallet details:"+ ex.getMessage());
                log.error("Wallet details stored error", ex);
            }
        });*/

    }

    private Component createNameBadge(Wallet wallet) {
        String theme ="";
        String name = wallet.getName();

        if (wallet.isDefaultWallet()) {
            theme = "badge success";
        }

        Span badge = new Span(name);
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private void viewBalance2(WalletRow rowData) {
        if (rowData == null) {
            return;
        }

        String address = rowData.getAddress();
        long currentInstanceId = walletService.getCurrentInstanceId();

        //prepare token balance
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(address, currentInstanceId);
        Set<Map.Entry<Long, BigInteger>> entries = null;

        String style = " style='padding: 6px;'";

        StringBuilder rowHtml = new StringBuilder("<tr><th" + style + ">Token Name</th><th" + style + ">Token Balance</th></tr>");
        if (usedTokenBalanceDetail != null&&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

            for (Map.Entry<Long, BigInteger> entry : entries) {
                Long key = entry.getKey();
                BigInteger value = entry.getValue();
                rowHtml.append("<tr><td" + style + ">" + TokenUtil.getTokenSymbol(key) + "</td><td" + style + ">" + String.format("%.8f",NumberUtil.bigIntToNacDouble(value)) + "</td></tr>");
            }

        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Wallet token balance");

        String textHtml = "<div><table style='border-collapse:collapse;' border='1'>" + rowHtml.toString() + "</table></div>";
        dialog.setText(new Html(textHtml).getElement());

        Button closeBtn = new Button("Close", VaadinIcon.CLOSE.create());
        closeBtn.addClickListener(e -> {
            dialog.close();
        });
        closeBtn.getElement().setAttribute("theme", "primary");
        dialog.setConfirmButton(closeBtn.getElement());

        dialog.open();
    }

    private void viewBalance(WalletRow rowData) {
        if (rowData == null) {
            return;
        }

        String address = rowData.getAddress();
        long currentInstanceId = walletService.getCurrentInstanceId();

        //prepare token balance
        Wallet defaultWallet1 = walletService.getDefaultWallet();
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWallet1.getAddress(), currentInstanceId);
        Set<Map.Entry<Long, BigInteger>> entries = null;
        Grid<TokenBalanceGridRow> tokenBalanceGrid = new Grid<>(TokenBalanceGridRow.class, false);

        if (usedTokenBalanceDetail != null&&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

            List<TokenBalanceGridRow> rows = Lists.newArrayList();
            for (Map.Entry<Long, BigInteger> entry : entries) {
                Long key = entry.getKey();
                BigInteger value = entry.getValue();
                rows.add(TokenBalanceGridRow.builder().balance(value).token(key).address(walletService.getDefaultWalletAddress()).build());
            }

            tokenBalanceGrid.addColumn("id").setAutoWidth(true).setHeader("Token Name");
            tokenBalanceGrid.addColumn("name").setAutoWidth(true).setHeader("Token Balance");

            grid.addColumn(new ComponentRenderer<>(Button::new, (button, row) -> {

                String address1 = row.getAddress();
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                button.setText(WalletUtil.shortAddress(address1));
                button.addClickListener(e -> {
                    String url = ecologyUrlService.buildAccountUrlByScan(address1, currentInstanceId);
                    //LaunchUtil.launchBrowser(url, "Visit account detail url");
                    getUI().ifPresent(ui -> ui.getPage().open(url));
                });
            })).setAutoWidth(true).setHeader("View").setResizable(true);

            tokenBalanceGrid.setItems(rows);
            tokenBalanceGrid.getDataProvider().refreshAll();
        }

        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Wallet token balance");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.add(tokenBalanceGrid);

        Button button = new Button("Close", e -> dialog.close());
        layout.add(button);

        dialog.add(layout);
        dialog.open();

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> walletId = event.getRouteParameters().getInteger(WALLET_ID);
        if (walletId.isPresent()) {

            long currentInstanceId = walletService.getCurrentInstanceId();

            Optional<WalletRow> walletFromBackend = walletService.getWalletRow(walletId.get(), currentInstanceId);
            if (walletFromBackend.isPresent()) {
                populateForm(walletFromBackend.get());
            } else {
                CompUtil.showError(String.format("The requested wallet was not found, ID = %d", walletId.get()));
                refreshGrid();
                event.forwardTo(WalletListView.class);
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

        /* action buttons */
        VerticalLayout btnLayout = new VerticalLayout();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        btnLayout.setWidthFull();
        btnLayout.setSpacing(false);
        btnLayout.setPadding(false);

        importBtn = new Button("Import");//VaadinIcon.PLUS_CIRCLE_O.create()
        importBtn.setWidthFull();
        backupBtn = new Button("Backup");//VaadinIcon.SHIELD.create()
        backupBtn.setWidthFull();
        profileBtn = new Button("Profile");//VaadinIcon.INFO_CIRCLE_O.create()
        profileBtn.setWidthFull();
        setDefaultBtn = new Button("Set Default");//VaadinIcon.INFO_CIRCLE_O.create()
        setDefaultBtn.setWidthFull();

        btnLayout.add(importBtn, backupBtn, profileBtn, setDefaultBtn);
        btnLayout.setFlexGrow(1, importBtn, backupBtn, profileBtn, setDefaultBtn);
        formLayout.add(btnLayout);
        configFormLayout();

        name = new TextField("Wallet Name");
        name.setReadOnly(true);
        address = new TextField("Wallet Address");
        address.setReadOnly(true);
        nacBalance = new TextField("NAC Balance");
        nacBalance.setVisible(false);
        //nomcBalance = new TextField("NOMC Balance");nomcBalance.setReadOnly(true);
        //usdnBalance = new TextField("USDN Balance");usdnBalance.setReadOnly(true);

        pswTip = new TextField("Password Tip");
        pswTip.setReadOnly(true);
        addTimeText = new TextField("Add Time");
        addTimeText.setReadOnly(true);

        commonNode = new Checkbox("Common Node?");
        commonNode.getStyle().set("padding-top", "var(--lumo-space-m)");
        commonNode.setReadOnly(true);

        fullNode = new Checkbox("Full Node?");
        fullNode.getStyle().set("padding-top", "var(--lumo-space-m)");
        fullNode.setReadOnly(true);

        defaultWallet = new Checkbox("Default Wallet?");
        defaultWallet.getStyle().set("padding-top", "var(--lumo-space-m)");
        defaultWallet.setReadOnly(true);

        hasBackup = new Checkbox("Backup?");
        hasBackup.getStyle().set("padding-top", "var(--lumo-space-m)");
        hasBackup.setReadOnly(true);

        Component[] fields = new Component[]{name, address, nacBalance,/*nomcBalance,usdnBalance,*/ addTimeText, pswTip, /*fullNode,*/ defaultWallet};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void configFormLayout() {
        importBtn.addClickListener(e -> importBtnClick());
        backupBtn.addClickListener(e -> backupBtnClick());
        profileBtn.addClickListener(e -> viewBtnClick());
        setDefaultBtn.addClickListener(e -> setDefaultClick());
    }

    private void viewBtnClick() {
        if (selectId > 0) {
            UI.getCurrent().navigate(String.format(WalletProfileFormView.WALLET_PROFILE_ROUTE_TEMPLATE, selectId));
        }
    }

    private void setDefaultClick() {
        if (selectId > 0) {
            walletService.setDefaultWallet(selectId);
            refreshGrid();
            CompUtil.showSuccess("Set default successfully");

            // fire instance change
            UI mainLayout = CompUtil.getMainLayout();
            if (mainLayout != null) {
                ComponentUtil.fireEvent(mainLayout, new WalletChangeDefaultEvent(this, walletService.getDefaultWallet()));
            }
        }
    }

    private void backupBtnClick() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Please enter wallet password:");
        //String textHtml = "<p>Do you want to <b>import wallet</b> or <b>cancel</b> ?</p>";
        //dialog.setText(new Html(textHtml).getElement());

        FormLayout formLayout = new FormLayout();
        PasswordField psw = new PasswordField("Password");
        Component[] fields = new Component[]{psw};
        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        dialog.add(formLayout);

        Button saveButton = new Button("Confirm", VaadinIcon.CHECK_CIRCLE_O.create());
        saveButton.addClickListener(e -> {

            String pswValue = psw.getValue();
            if (StringUtils.isBlank(pswValue)) {
                CompUtil.showError("Please enter the password");
                return;
            }

            if (selectId <= 0) {
                CompUtil.showError("Please select grid item first");
                return;
            }

            Optional<Wallet> walletOptional = this.walletRepository.findById(selectId);
            if (!walletOptional.isPresent()) {
                CompUtil.showError("The wallet you selected does not exist");
            }

            try {
                Wallet wallet = walletOptional.get();
                String mnemonicEncrypt = wallet.getMnemonicEncrypt();
                String saltEncrypt = wallet.getSaltEncrypt();
                String mnemonic = AESUtil.decrypt(mnemonicEncrypt, pswValue);
                String salt = null;

                boolean isSaltNotEmpty = StringUtils.isNotEmpty(saltEncrypt);
                if (isSaltNotEmpty) {
                    salt = AESUtil.decrypt(saltEncrypt, pswValue);
                }

                //restore
                Keystore restoreKeystore = WalletUtils.generate(Language.ENGLISH, mnemonic, isSaltNotEmpty ? salt : null, 0);
                if (restoreKeystore == null) {
                    throw new RuntimeException("The wallet keystore is empty");
                }

                this.walletService.setCurrentKeystore(new KeystoreWrapper(restoreKeystore, wallet));

                dialog.close();

                UI.getCurrent().navigate(BackupWalletFormView.class);

            } catch (Exception exception) {
                String msg = String.format("Wallet recovery operation failed: " + exception.getMessage());
                log.error(msg, exception);
                CompUtil.showError(msg);
            }

        });
        saveButton.getElement().setAttribute("theme", "primary");
        dialog.setConfirmButton(saveButton.getElement());

        //Button rejectButton = new Button("Discard", VaadinIcon.CLOSE_CIRCLE_O.create());
        //rejectButton.addClickListener(e -> dialog.close());
        //rejectButton.getElement().setAttribute("theme", "error tertiary");
        //dialog.setRejectButton(rejectButton.getElement());

        dialog.setCancelButton("Cancel", e -> dialog.close());
        dialog.open();
    }

    private void changePasswordBtnClick(WalletRow walletRow) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Please enter wallet password info:");
        String textHtml = "<p>Current wallet: "+ walletRow.getName()+ "</p>";
        dialog.setText(new Html(textHtml).getElement());

        FormLayout formLayout = new FormLayout();
        PasswordField oldPassword = new PasswordField("Old Password");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmNewPassword = new PasswordField("Confirm New Password");
        Component[] fields = new Component[]{oldPassword,newPassword,confirmNewPassword};
        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        dialog.add(formLayout);

        Button saveButton = new Button("Confirm", VaadinIcon.CHECK_CIRCLE_O.create());
        saveButton.addClickListener(e -> {

            String oldPasswordValue = StringUtils.trim(oldPassword.getValue());
            if (StringUtils.isBlank(oldPasswordValue)) {
                CompUtil.showError("Please enter the old password");
                return;
            }

            String newPasswordValue = StringUtils.trim(newPassword.getValue());
            if (StringUtils.isBlank(newPasswordValue)) {
                CompUtil.showError("Please enter the new password");
                return;
            }

            String confirmNewPasswordValue = StringUtils.trim(confirmNewPassword.getValue());
            if (!StringUtils.equals(newPasswordValue,confirmNewPasswordValue)) {
                CompUtil.showError("The new password does not match");
                return;
            }

            if (selectId <= 0) {
                CompUtil.showError("Please select grid item first");
                return;
            }

            Optional<Wallet> walletOptional = this.walletRepository.findById(selectId);
            if (!walletOptional.isPresent()) {
                CompUtil.showError("The wallet you selected does not exist");
            }

            try {

                Wallet wallet = walletOptional.get();
                HttpResult result = walletService.changeWalletPassword(wallet, oldPasswordValue, confirmNewPasswordValue);

                if (result.getFlag()) {
                    CompUtil.showSuccess("Change wallet new password successfully");
                    dialog.close();
                }else{
                    CompUtil.showError(result.getMessage());
                }

            } catch (Exception exception) {
                String msg = String.format("Wallet password change failed: " + exception.getMessage());
                log.error(msg, exception);
                CompUtil.showError(msg);
            }

        });
        saveButton.getElement().setAttribute("theme", "primary");
        dialog.setConfirmButton(saveButton.getElement());
        dialog.setCancelButton("Cancel", e -> dialog.close());
        dialog.open();
    }

    private void importBtnClick() {
        UI.getCurrent().navigate(ImportWalletFormView.class);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        refresh.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(refresh);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setPadding(true);
        searchLayout.setWidthFull();

        walletNameSearchItem = new TextField();
        walletNameSearchItem.setPlaceholder("Wallet Name");
        walletAddressSearchItem = new TextField();
        walletAddressSearchItem.setPlaceholder("Wallet Address");
        searchBtn = new Button(new Icon(VaadinIcon.SEARCH));
        //clearBtn = new Button("Clear");
        //clearBtn.getStyle().set("margin-right", "auto");// expands the empty space right of button two
        searchBtn.getStyle().set("margin-right", "auto");// expands the empty space right of button two

        prevPageBtn = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        nextPageBtn = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));

        searchLayout.add(walletNameSearchItem,walletAddressSearchItem,searchBtn,prevPageBtn,nextPageBtn);

        wrapper.add(searchLayout);
        wrapper.add(grid);

        searchBtn.addClickListener(e -> {
            currentPageNumber = 0;
            refreshGrid();
        });

        //clearBtn.addClickListener(e -> {
        //    walletNameSearchItem.setValue("");
        //    walletAddressSearchItem.setValue("");
        //});

        prevPageBtn.addClickListener(e -> {
            if (page != null && currentPageNumber != 0) {
                currentPageNumber = currentPageNumber-1;
                refreshGrid();
            }
        });

        nextPageBtn.addClickListener(e -> {
            if (page != null) {
                int totalPages = page.getTotalPages();
                if (currentPageNumber != totalPages) {
                    currentPageNumber = currentPageNumber+1;
                    refreshGrid();
                }
            }
        });
    }

    private int currentPageNumber = 0;

    private Page<Wallet> page;

    private void refreshGrid() {

        int pageSize = 20;

        String nameValue = walletNameSearchItem == null ? "" : StringUtils.trim(walletNameSearchItem.getValue());
        String addressValue = walletAddressSearchItem == null ? "" : StringUtils.trim(walletAddressSearchItem.getValue());

        long currentInstanceId = walletService.getCurrentInstanceId();

        PageRequest pageRequest = PageRequest.of(currentPageNumber, pageSize, Sort.Direction.DESC, "id");

        Specification<Wallet> specification = new Specification<Wallet>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Wallet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(StringUtils.isNotBlank(nameValue)) {
                    Predicate predicate = cb.like(root.get("name").as(String.class), "%" + nameValue +"%");
                    predicates.add(predicate);
                }
                if (StringUtils.isNotBlank(addressValue)) {
                    Predicate predicate = cb.like(root.get("address").as(String.class), "%" + addressValue +"%");
                    predicates.add(predicate);
                }

                if (predicates.size() == 0) {
                    return null;
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        page = walletRepository.findAll(specification,pageRequest);

        List<Wallet> content = page.getContent();
        List<WalletRow> walletRowList = walletService.getWalletRowList(content,currentInstanceId);

        grid.setItems(walletRowList);

        grid.getDataProvider().refreshAll();

        if (CollUtil.isNotEmpty(walletRowList)) {
            WalletRow walletRow = walletRowList.stream().filter(one -> one.isDefaultWallet()).findAny().orElse(null);
            grid.select(walletRow);
            populateForm(walletRow);
        }
    }

    private void populateForm(WalletRow value) {
        this.walletRow = value;
        binder.readBean(this.walletRow);
    }


    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        //Instance instanceEnum = event.getInstance();
        refreshGrid();
    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        //Wallet wallet = event.getWallet();
        refreshGrid();
    }

    private Registration walletChangeDefaultEventRegistration;
    private Registration instanceChangeEventRegistration;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //UI ui = attachEvent.getUI();

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {

            instanceChangeEventRegistration = ComponentUtil.addListener(
                    mainLayout,
                    InstanceChangeEvent.class,
                    event -> {
                        instanceChangeEventHandler(event);
                    }
            );

            walletChangeDefaultEventRegistration = ComponentUtil.addListener(
                    mainLayout,
                    WalletChangeDefaultEvent.class,
                    event -> {
                        walletChangeDefaultEventHandler(event);
                    }
            );

        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

        if (walletChangeDefaultEventRegistration != null) {
            walletChangeDefaultEventRegistration.remove();
        }

        if (instanceChangeEventRegistration != null) {
            instanceChangeEventRegistration.remove();
        }

        super.onDetach(detachEvent);
    }

}
