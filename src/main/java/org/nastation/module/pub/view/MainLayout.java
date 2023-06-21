package org.nastation.module.pub.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.structure.instance.Instance;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.event.InstanceRefreshEvent;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.event.WalletCreateEvent;
import org.nastation.common.event.cross.BlockHeightChangeEvent;
import org.nastation.common.event.cross.BlockHeightChangeEventListener;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.MathUtil;
import org.nastation.components.Images;
import org.nastation.data.config.AppConfig;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.service.FluxService;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.nastation.module.wallet.view.ReceiveFormView;
import org.nastation.module.wallet.view.TransferFormView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.addons.apollonav.ApolloNav;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
//@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
//@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
//@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")

//@Push
//https://mekaso.rocks/java-8-concurrency-in-a-vaadin-ui
//https://committed.io/posts/vaadin-and-spring/vaadin-spring/
//https://www.bookstack.cn/read/vaadin-10-en/75c76299690ed804.md
//https://vaadin.com/docs/v14/flow/advanced/tutorial-push-access

@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")

@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
@CssImport("./styles/misc/box-shadow-borders.css")

@CssImport(value = "./styles/styles.css", include = "lumo-badge")

@JsModule("@vaadin/vaadin-lumo-styles/badge")
@Slf4j
public class MainLayout extends AppLayout implements BootstrapListener, LocaleChangeObserver, BlockHeightChangeEventListener<BlockHeightChangeEvent> {

    public static final String NA_STATION = "NaStation";
    private final Tabs menu;
    private H1 viewTitle;

    //private MenuItem websiteMenu;
    //private MenuItem accountMenu;

    private FluxService fluxService;
    private WalletDataService walletDataService;
    private AppConfig appConfig;
    private WalletService walletService;
    private NaScanHttpService naScanHttpService;
    private ProgressBar progressBar;

    private Span percentDataSpan;
    private Span heightDataSpan;

    private SubMenu instanceSubMenu;
    private ContextMenu walletContextMenu;
    private Avatar avatar;

    private static long currentInstanceId;

    private static boolean isHasReg = false;

    public MainLayout(
            @Autowired AppConfig appConfig,
            @Autowired WalletService walletService,
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired FluxService fluxService
    ) {

        this.appConfig = appConfig;
        this.walletService = walletService;
        this.fluxService = fluxService;
        this.walletDataService = walletDataService;
        this.naScanHttpService = naScanHttpService;

        VaadinSession.getCurrent().setErrorHandler((ErrorHandler) errorEvent -> {

            Throwable throwable = errorEvent.getThrowable();

            log.error("ErrorHandler >> ", errorEvent);
            log.error("ErrorHandler Throwable >> ", throwable);

            // do everything you need
            //UI.getCurrent().getSession().close();                   //close Vaadin session
            //UI.getCurrent().getSession().getSession().invalidate(); //close Http session
            //UI.getCurrent().getPage().setLocation("/login");        //redirect..
            //UI.getCurrent().getNavigator().navigateTo("viewName");      //... or using navigator

            //ookie newCookie = new Cookie("userLogin", "");
            //newCookie.setComment("userLogin user");
            //newCookie.setMaxAge(0);
            //newCookie.setPath("/");
            //VaadinService.getCurrentResponse().addCookie(newCookie);

            UI.getCurrent().navigate(PageStateView.class);
        });

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));

        currentInstanceId = walletService.getCurrentInstanceId();

        //log.warn("MainLayout >> setSystemMessagesProvider");
        //VaadinService.getCurrent().setSystemMessagesProvider(new DefaultMessageProvider());

        //if (!isHasReg) {
        //EventBusCenter.me().register(this);
        //isHasReg = true;
        //}

    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        layout.setFlexGrow(1, viewTitle); // this expands the button

        MenuBar menuBar = new MenuBar();
        //menuBar.setOpenOnHover(true);

        createIconItem(menuBar, VaadinIcon.ARROW_CIRCLE_UP_O, "Send", e -> UI.getCurrent().navigate(TransferFormView.class));
        createIconItem(menuBar, VaadinIcon.ARROW_CIRCLE_DOWN_O, "Receive", e -> UI.getCurrent().navigate(ReceiveFormView.class));
        MenuItem apiMenuItem = createIconItem(menuBar, VaadinIcon.CODE, "API", null);
        SubMenu apiSubMenu = apiMenuItem.getSubMenu();
        apiSubMenu.addItem("API doc", e -> {
            getUI().ifPresent(ui -> ui.getPage().open("/swagger-ui.html"));
        });

        //apiSubMenu.addItem("API console", e -> {
        //    UI.getCurrent().navigate(XtermView.class);
        //});

        /* Website */
        /*
        MenuItem websiteMenu = createIconItem(menuBar, VaadinIcon.GLOBE_WIRE, "Website", null);
        SubMenu websiteSubMenu = websiteMenu.getSubMenu();

        Map<String, String> menuMap = Maps.newLinkedHashMap();
        menuMap.put("Website", appConfig.getWebsiteUrl());
        menuMap.put("NaScan", appConfig.getNascanUrl());
        menuMap.put("Download", appConfig.getAppDownloadUrl());
        menuMap.put("Changelog", appConfig.getChangeLogUrl());
        menuMap.put("Telegram", appConfig.getTelegramUrl());
        menuMap.put("Twitter", appConfig.getTwitterUrl());

        for (Map.Entry<String, String> entry : menuMap.entrySet()) {
            String key = entry.getKey();
            MenuItem mi = websiteSubMenu.addItem(key, e -> {
                String value = menuMap.get(e.getSource().getText());
                //LaunchUtil.launchBrowser(value, "Visit url");
                getUI().ifPresent(ui -> ui.getPage().open(value));
            });
        }*/

        /* Network */
        /*
        MenuItem networkMenu = createIconItem(menuBar, VaadinIcon.CONNECT, "Network", null);
        MenuItem mainnet = networkMenu.getSubMenu().addItem("Mainnet", e -> {
        });
        mainnet.setCheckable(true);
        mainnet.setChecked(true);*/

        /* Instance */
        MenuItem instanceMenu = createIconItem(menuBar, VaadinIcon.CUBE, "Instance", null);
        instanceSubMenu = instanceMenu.getSubMenu();

        refresh_instanceSubMenu();

        layout.add(menuBar);

        /* Avatar */
        avatar = new Avatar();
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
        //avatar.setImage(Images.nac_48x48().getSrc());
        //avatar.setMaxWidth("36px");
        avatar.getStyle().set("margin-left", "10px");
        //avatar.setColorIndex(2);

        walletContextMenu = new ContextMenu(avatar);
        walletContextMenu.setOpenOnClick(true);

        /*
        walletContextMenu.addClickListener(e -> {
            List<MenuItem> items = walletContextMenu.getItems();
            if (CollUtil.isEmpty(items)) {
                CompUtil.showError("No wallet yet,please create a wallet first");
                return;
            }

        });
        */

        WalletRepository repository = walletService.getWalletRepository();
        Page<Wallet> page = repository.findByDefaultWallet(false, PageRequest.of(0, 30, Sort.by(Sort.Order.desc("id"))));
        Wallet defaultWallet = repository.findTopByDefaultWalletOrderByIdDesc(true);

        if (defaultWallet != null)
            avatar.setAbbreviation(CompUtil.subAbbreviation(defaultWallet.getName()));

        List<Wallet> list = null;
        if (page.hasContent()) {
            list = page.getContent();
        } else {
            list = Lists.newArrayList();
        }
        List<Wallet> newList = new ArrayList<Wallet>(list);

        if (defaultWallet != null) {
            newList.add(0, defaultWallet);
        }

        for (Wallet wallet : newList) {

            if (wallet == null) {
                continue;
            }

            MenuItem menuItem = walletContextMenu.addItem(buildWalletMenuItemText(wallet), e -> {
                //setWalletMenuItem(contextMenu, w);

                walletContextMenu.getItems().forEach(
                        item -> item.setChecked(false)
                );

                walletService.setDefaultWallet(wallet.getId());
                e.getSource().setChecked(true);
                CompUtil.showSuccess("Default wallet: " + wallet.getName());

                ComponentUtil.fireEvent(UI.getCurrent(), new WalletChangeDefaultEvent(this, wallet));
            });

            menuItem.setCheckable(true);
            menuItem.setChecked(wallet.isDefaultWallet());
        }

        layout.add(avatar);

        return layout;
    }

    private void refresh_instanceSubMenu() {
        List<Instance> instList = InstanceUtil.getEnableInstanceList();

        if (instList == null) {
            return;
        }

        if (instanceSubMenu == null) {
            return;
        }

        //clear
        instanceSubMenu.removeAll();

        for (Instance inst : instList) {
            String symbol = inst.getSymbol();

            MenuItem instItem = instanceSubMenu.addItem(symbol, e -> {
                String symbolText = e.getSource().getText();

                //TODO why id
                Instance targetInst = InstanceUtil.getInstanceBySymbol(symbolText);
                if (targetInst != null) {

                    // cancel
                    List<MenuItem> items = instanceSubMenu.getItems();
                    for (MenuItem item : items) {
                        item.setChecked(false);
                    }

                    // save data
                    walletService.setCurrentInstance(targetInst.getId());
                    log.info("Has changed current instance = " + targetInst.getSymbol());
                    CompUtil.showSuccess("Current instance: " + targetInst.getSymbol());

                    // set target
                    e.getSource().setChecked(true);

                    ComponentUtil.fireEvent(UI.getCurrent(), new InstanceChangeEvent(this, walletService.getCurrentInstance()));
                }
            });

            instItem.setCheckable(true);

            // set checked
            long currentInstanceId = walletService.getCurrentInstanceId();
            if (inst.getId() == currentInstanceId) {
                instItem.setChecked(true);
            } else {
                instItem.setChecked(false);
            }

        }
    }

    private String buildWalletMenuItemText(Wallet w) {

        if (w == null) {
            return "-";
        }

        return w.getName() + " (" + w.getAddress() + " )";
    }

    private void setWalletMenuItem(ContextMenu menu, Wallet wallet) {

        if (wallet == null) {
            return;
        }
        // Update checked state of menu items
        menu.getItems().forEach(
                item -> item.setChecked(wallet.isDefaultWallet())
        );
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        Icon icon = new Icon(iconName);

        boolean isChild = false;

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, clickListener);

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private Component createDrawerContent(Tabs menu) {

        ApolloNav nav = MainMenu.getApolloNav();
        //nav.setLabel("Main menu");
        nav.getStyle().set("padding", "5px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Image logo = Images.nac_48x48();
        logo.setMaxWidth("48px");
        logo.setMaxHeight("48px");

        logoLayout.add(logo);
        logoLayout.add(new H1(NA_STATION));

        VerticalLayout footer = new VerticalLayout();
        footer.setSizeFull();
        footer.setPadding(true);
        //footer.setSpacing(true);

        percentDataSpan = new Span();
        percentDataSpan.setVisible(true);
        heightDataSpan = new Span();
        percentDataSpan.setVisible(true);

        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        footer.add(new Hr());
        footer.add(percentDataSpan);
        footer.add(heightDataSpan);
        footer.add(progressBar);

        layout.add(logoLayout, nav, footer);
        //layout.setFlexGrow(1, logoLayout, nav);

        // first fresh
        refreshSyncInfoUI();

        return layout;
    }

    private void refreshSyncInfoUI() {
        try {
            Long currentHeight = walletDataService.getCurrentSyncBlockHeightFromCache(currentInstanceId);
            Long lastBlockHeight = naScanHttpService.getLastBlockHeight(currentInstanceId);


            if (
                    percentDataSpan != null
                            && currentHeight != null
                            && lastBlockHeight != null
            ) {

                if (lastBlockHeight == 0) {
                    return;
                }
                double percent = MathUtil.round(((double) currentHeight * 1.0 / (double) lastBlockHeight * 100), 2);

                if (percent > 100) {
                    percent = 100;
                }

                percentDataSpan.setText(String.format("Sync block: %s", percent +"%"));
                heightDataSpan.setText(String.format("%s / %s", currentHeight, lastBlockHeight));

                //log.warn("{} -> Sync block: {} , {} / {}",currentInstanceId, percent, currentHeight, lastBlockHeight);

                if (currentHeight >= lastBlockHeight) {
                    progressBar.setVisible(false);
                } else {
                    progressBar.setVisible(true);
                }
            }

        } catch (Exception e) {
            log.error("refreshSyncInfoUI() error:", e);
        }
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        //tabs.add(createMenuItems());
        return tabs;
    }


    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        //Element head = response.getDocument().getElementsByTag("head").get(0);
        //response.getDocument().head().append("<link rel='stylesheet' href='./bootstrap/css/bootstrap.min.css' />");
        //response.getDocument().head().append("<link rel='stylesheet' href='./bootstrap/css/bootstrap-grid.min.css' />");
        //response.getDocument().head().append("<link rel='stylesheet' href='./bootstrap/css/bootstrap-reboot.min.css' />");
        //
        //response.getDocument().head().append("<script src='./assets/jquery.min.js'/>");
        //response.getDocument().head().append("<script src='./assets/popper.min.js'/>");
        //response.getDocument().head().append("<script src='./bootstrap/js/bootstrap.min.js'/>");
        //response.getDocument().head().append("<script src='./bootstrap/js/bootstrap.bundle.min.js'/>");
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        log.debug("localeChange event: {}", event);
    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> addAttachListener listener: {}", listener);
        return null;
    }

    @Override
    public Registration addDetachListener(ComponentEventListener<DetachEvent> listener) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> addDetachListener listener: {}", listener);
        return null;
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> removeRouterLayoutContent oldContent: {}", oldContent);

        UI ui = UI.getCurrent();

        if (true) {

            if (subscription != null) {
                subscription.dispose();
            }

            subscription = this.fluxService.delayBlockHeight("height").subscribe(data -> {
                ui.access(() -> {

                    try {

                        log.debug("ui.access removeRouterLayoutContent refreshSyncInfoUI()");

                        refreshSyncInfoUI();

                        getUiToPush(percentDataSpan);
                        getUiToPush(heightDataSpan);
                        getUiToPush(progressBar);

                    } catch (Throwable e) {
                        log.error("MainLayout handle push error:", e);
                    }

                });
            });

        }

    }

    public void getUiToPush(Component comp) {
        if (comp != null) {
            if (comp.getUI() != null) {
                Optional<UI> uiGet = comp.getUI();
                if (uiGet != null && uiGet.isPresent()) {
                    UI ui = uiGet.get();
                    if (ui != null) {
                        ui.push();
                    }
                }
            }
        }
    }

    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> instanceChangeEventHandler ");

        currentInstanceId = event.getInstance().getId();

        Instance instanceEnum = event.getInstance();
        List<MenuItem> items = instanceSubMenu.getItems();

        for (MenuItem item : items) {
            item.setChecked(item.getText().equals(instanceEnum.getSymbol()));
        }
    }


    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {

        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> walletChangeDefaultEventHandler ");

        Wallet wallet = event.getWallet();
        String eventWalletText = buildWalletMenuItemText(wallet);
        List<MenuItem> items = walletContextMenu.getItems();

        for (MenuItem item : items) {
            item.setChecked(eventWalletText.equals(item.getText()));
        }

        avatar.setAbbreviation(CompUtil.subAbbreviation(wallet.getName()));
    }


    private static Disposable subscription;
    private static Registration walletChangeDefaultEventRegistration;
    private static Registration walletCreateEventRegistration;
    private static Registration instanceChangeEventRegistration;
    private static Registration instanceRefreshEventRegistration;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        //Hook up to service for live updates

        if (true) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>> subscription ");

            if (subscription != null) {
                subscription.dispose();
            }

            subscription = this.fluxService.delayBlockHeight("height").subscribe(data -> {
                ui.access(() -> {

                    try {

                        log.debug("ui.access refreshSyncInfoUI()");

                        refreshSyncInfoUI();

                        getUiToPush(percentDataSpan);
                        getUiToPush(heightDataSpan);
                        getUiToPush(progressBar);

                    } catch (Throwable e) {
                        log.error("MainLayout handle push error:", e);
                    }

                });
            });

        }

        if (true) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>> instanceChangeEventRegistration ");

            if (instanceChangeEventRegistration != null) {
                instanceChangeEventRegistration.remove();
            }

            instanceChangeEventRegistration = ComponentUtil.addListener(
                    UI.getCurrent(),
                    InstanceChangeEvent.class,
                    event -> {
                        instanceChangeEventHandler(event);
                    }
            );
        }
        if (true) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>> instanceRefreshEventRegistration ");

            if (instanceRefreshEventRegistration != null) {
                instanceRefreshEventRegistration.remove();
            }

            instanceRefreshEventRegistration = ComponentUtil.addListener(
                    UI.getCurrent(),
                    InstanceRefreshEvent.class,
                    event -> {
                        instanceRefreshEventHandler(event);
                    }
            );
        }

        if (true) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>> walletChangeDefaultEventRegistration ");

            if (walletChangeDefaultEventRegistration != null) {
                walletChangeDefaultEventRegistration.remove();
            }

            walletChangeDefaultEventRegistration = ComponentUtil.addListener(
                    UI.getCurrent(),
                    WalletChangeDefaultEvent.class,
                    event -> {
                        walletChangeDefaultEventHandler(event);
                    }
            );
        }

        if (true) {

            if (walletCreateEventRegistration != null) {
                walletCreateEventRegistration.remove();
            }

            log.debug(">>>>>>>>>>>>>>>>>>>>>>>> walletCreateEventRegistration ");

            walletCreateEventRegistration = ComponentUtil.addListener(
                    UI.getCurrent(),
                    WalletCreateEvent.class,
                    event -> {
                        walletCreateEventHandler(event);
                    }
            );
        }

    }

    private void instanceRefreshEventHandler(InstanceRefreshEvent event) {

        refresh_instanceSubMenu();

    }

    private void walletCreateEventHandler(WalletCreateEvent event) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> walletCreateEventHandler ");

        Wallet wallet = event.getWallet();
        String eventWalletText = buildWalletMenuItemText(wallet);
        List<MenuItem> items = walletContextMenu.getItems();

        MenuItem menuItem = walletContextMenu.addItem(buildWalletMenuItemText(wallet), e -> {
            walletContextMenu.getItems().forEach(
                    item -> item.setChecked(false)
            );
            walletService.setDefaultWallet(wallet.getId());
            e.getSource().setChecked(true);
            CompUtil.showSuccess("Default wallet: " + wallet.getName());
            ComponentUtil.fireEvent(UI.getCurrent(), new WalletChangeDefaultEvent(this, wallet));
        });
        menuItem.setCheckable(true);
        menuItem.setChecked(true);

        for (MenuItem item : items) {
            item.setChecked(false);
        }

        avatar.setAbbreviation(CompUtil.subAbbreviation(wallet.getName()));

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

        //EventBusCenter.me().unregister(this);

        log.debug(">>>>>>>>>>>>>>>>>>>>>>>> onDetach " + detachEvent);

        if (subscription != null) {
            subscription.dispose();
        }

        if (walletChangeDefaultEventRegistration != null) {
            walletChangeDefaultEventRegistration.remove();
        }

        if (walletCreateEventRegistration != null) {
            walletCreateEventRegistration.remove();
        }

        if (instanceChangeEventRegistration != null) {
            instanceChangeEventRegistration.remove();
        }

        if (instanceRefreshEventRegistration != null) {
            instanceRefreshEventRegistration.remove();
        }

        super.onDetach(detachEvent);
    }

    //@Override
    //@Subscribe
    public void handle(BlockHeightChangeEvent event) {

        //long instanceId = event.getInstanceId();
        //long height = event.getHeight();

        //if (instanceId != currentInstanceId) {
        //    return;
        //}

        //log.debug("MainLayout handle() >>  height : {} , instanceId: {} , Time: {}", height, instanceId, DateUtil.formatDateTimeFull(LocalDateTime.now()));
        //ui.push();

        try {

            refreshSyncInfoUI();

            if (percentDataSpan != null) {
                percentDataSpan.getUI().get().push();
            }
            if (heightDataSpan != null) {
                heightDataSpan.getUI().get().push();
            }
            if (progressBar != null) {
                progressBar.getUI().get().push();
            }
        } catch (Exception e) {
            log.error("MainLayout handle push error:", e);
        }

    }

    public static MainLayout get() {
        return (MainLayout) UI.getCurrent().getChildren()
                .filter(component -> component.getClass() == MainLayout.class)
                .findFirst().get();
    }

}
