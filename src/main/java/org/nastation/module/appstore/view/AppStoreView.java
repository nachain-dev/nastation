package org.nastation.module.appstore.view;

//import com.teamdev.jxbrowser.browser.Browser;
//import com.teamdev.jxbrowser.engine.Engine;
//import com.teamdev.jxbrowser.view.swing.BrowserView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.components.Images;
import org.nastation.data.config.AppConfig;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import static org.nastation.module.appstore.view.AppStoreView.Page_Title;
import static org.nastation.module.appstore.view.AppStoreView.Route_Value;

@PageTitle(Page_Title)
@Route(value = Route_Value, layout = MainLayout.class)
public class AppStoreView extends VerticalLayout {

    public static final String Route_Value = "AppStoreView";
    public static final String Page_Title = "AppStore";

    /*public static void main() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Swing BrowserView");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("https://www.google.com");
    }*/

    /*public static void swt() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("SWT BrowserView");
        shell.setLayout(new GridLayout());

        com.teamdev.jxbrowser.view.swt.BrowserView view = com.teamdev.jxbrowser.view.swt.BrowserView.newInstance(shell, browser);
        view.setSize(700, 500);

        shell.pack();
        shell.open();

        browser.navigation().loadUrl("https://www.google.com");

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        engine.close();
        display.dispose();
    }*/

    public AppStoreView(
            @Autowired AppConfig appConfig
    ) {
        addClassName("home-view");

        Image logo = Images.nac_72x72();
        add(logo);

        H1 title = new H1(Page_Title);
        title.getStyle().set("margin-top", "0");
        add(title);

        add(new Html("<p>Discover NFTs, Blockchain Games, De-Fi, Dapps On Nirvana Chain Dapp Store</p>"));

        Button openBtn = new Button("Open " + Page_Title);
        openBtn.addClickListener(e -> {


            //main();

            //Application.getBrowserView().getBrowser().mainFrame().ifPresent(mainFrame -> {
                //new AdvancedBrowser("https://store.nachain.org/appstore/home");

                //Application.advancedBrowser.getShell().open();

                //JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");

                //mainFrame.executeJavaScript("window.NaBridge.openSwtWindow('https://store.nachain.org/appstore/home')");
            //});

            getUI().ifPresent(ui -> ui.getPage().executeJs(
                    "if ($1 == '_self') this.stopApplication(); window.open($0, $1, $2)",
                    "https://store.nachain.org/appstore/home", Page_Title,"height=600, width=1280"));

            //new AdvancedBrowser("https://html5test.com//");

            //getUI().ifPresent(ui -> ui.getPage().executeJs(
            //        "alert('123')"));

            //getUI().ifPresent(ui -> ui.getPage().executeJs(
            //        "history.back()"));

            //getUI().ifPresent(ui -> ui.getPage().executeJs(
            //        "alert(window.NaBridge==null)"));

            //getUI().ifPresent(ui -> ui.getPage().executeJs(
            //        "window.NaBridge.openSwtWindow($0)","https://store.nachain.org/appstore/home"));

            //new AdvancedJxbBrowser("https://www.nachain.org/");

        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        btnLayout.setSizeUndefined();
        btnLayout.setSpacing(true);
        btnLayout.setPadding(true);
        btnLayout.add(openBtn);

        add(btnLayout);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //UI ui = attachEvent.getUI();
    }


}
