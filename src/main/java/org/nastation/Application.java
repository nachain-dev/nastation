package org.nastation;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback;
import com.teamdev.jxbrowser.browser.event.BrowserClosed;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.browser.event.UpdateBoundsRequested;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.ui.Rect;
import com.teamdev.jxbrowser.ui.Size;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.config.timezone.TimeZoneService;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.vaadin.artur.helpers.LaunchUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Locale;

import static javax.swing.SwingUtilities.invokeLater;

@Push
@Theme(value = "nastation")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@JsModule("./scripts/copytoclipboard.js")
//@JsModule("./scripts/auto-refresh.js")
//@PWA(name = "NaStation", shortName = "NaStation", offlineResources = {"images/logo.png"})

@SpringBootApplication(scanBasePackages = {"org.nastation"})
@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@Slf4j
public class Application implements AppShellConfigurator, ApplicationListener<ApplicationReadyEvent> {

    public static final String NA_STATION = "NaStation";

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {

        TimeZoneService.disabled();

        /* if show twice then disable spring-boot-devtools */
        systemSet();

        //devOnline
        //pro
        //test
        //local
        String profile = "pro";

        if ((args != null && args.length>0 && StringUtils.equalsIgnoreCase(args[0], "nogui"))) {
            runAppServer(args);
            log.info("NaStation is running in NO-GUI mode");
        }else{
            if ("pro".equals(profile) || "test".equals(profile)) {
                runGui(args);
                runAppServer(args);
            } else {
                //Clean.cleanAll();
                LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
            }
        }
    }

    private static void systemSet() {
        Locale.setDefault(Locale.ENGLISH);
        System.setProperty("jxbrowser.license.key", "6P835FT5HB7RTZQXV6G8JY8RWUQMW3S9PZTVZGDM6KLD3V4LHW44UP4LO65RC00F7QKJ");
        System.setProperty("vaadin.proKey", "na-vaadin-pro@protonmail.com/pro-33ed7ea5-7c31-4fd2-a2d8-3a1bae19ef18");
    }

    private static BrowserView browserView = null;
    private static JFrame frame = null;

    public static BrowserView getBrowserView() {
        return browserView;
    }

    private static void runGui(String[] args) {
        //String loadingHtml = "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>loading</title><style>        html, body {            height: 90%;        }        body {            background-color: #233348 ;            display: flex;            align-items: center;            justify-content: center;        }        .title {            color: white;        }        .spinner {            -webkit-animation: rotator 1.4s linear infinite;            animation: rotator 1.4s linear infinite;        }        @-webkit-keyframes rotator {            0% {                transform: rotate(0deg);            }            100% {                transform: rotate(270deg);            }        }        @keyframes rotator {            0% {                transform: rotate(0deg);            }            100% {                transform: rotate(270deg);            }        }        .path {            stroke-dasharray: 187;            stroke-dashoffset: 0;            transform-origin: center;            -webkit-animation: dash 1.4s ease-in-out infinite, colors 5.6s ease-in-out infinite;            animation: dash 1.4s ease-in-out infinite, colors 5.6s ease-in-out infinite;        }        @-webkit-keyframes colors {            0% {                stroke: #4285F4;            }            25% {                stroke: #DE3E35;            }            50% {                stroke: #F7C223;            }            75% {                stroke: #1B9A59;            }            100% {                stroke: #4285F4;            }        }        @keyframes colors {            0% {                stroke: #4285F4;            }            25% {                stroke: #DE3E35;            }            50% {                stroke: #F7C223;            }            75% {                stroke: #1B9A59;            }            100% {                stroke: #4285F4;            }        }        @-webkit-keyframes dash {            0% {                stroke-dashoffset: 187;            }            50% {                stroke-dashoffset: 46.75;                transform: rotate(135deg);            }            100% {                stroke-dashoffset: 187;                transform: rotate(450deg);            }        }        @keyframes dash {            0% {                stroke-dashoffset: 187;            }            50% {                stroke-dashoffset: 46.75;                transform: rotate(135deg);            }            100% {                stroke-dashoffset: 187;                transform: rotate(450deg);            }        }</style></head><body><h1 class='title'>NaStation is initializing</h1><svg class='spinner' width='30px' height='30px' viewBox='0 0 66 66' xmlns='http://www.w3.org/2000/svg'><circle class='path' fill='none' stroke-width='6' stroke-linecap='round' cx='33' cy='33' r='30'></circle></svg></div></body></html>";
        String loadingHtml = "<!DOCTYPE html><html lang=\"en\" ><head><meta charset=\"UTF-8\"><title>NaStation</title><style> body {font-family: sans-serif;background: linear-gradient(253deg, #0cc898, #1797d2, #864fe1);background-size: 300% 300%;-webkit-animation: Background 25s ease infinite;-moz-animation: Background 25s ease infinite;animation: Background 25s ease infinite;}  @-webkit-keyframes Background { 0% {background-position: 0% 50%} 50% {background-position: 100% 50%} 100% {background-position: 0% 50%} }  @-moz-keyframes Background { 0% {background-position: 0% 50%} 50% {background-position: 100% 50%} 100% {background-position: 0% 50%} }  @keyframes Background { 0% {background-position: 0% 50%} 50% {background-position: 100% 50%} 100% {background-position: 0% 50%} }  .full-screen {position: fixed;top: 0;right: 0;bottom: 0;left: 0;background: url(https://i.imgur.com/wCG2csZ.png);background-size: cover;background-position: center;width: 100%;height: 100%;display: -webkit-flex;display: flex;-webkit-flex-direction: column;flex-direction: column;-webkit-align-items: center;align-items: center;-webkit-justify-content: center;justify-content: center;text-align: center;}  h1 {color: #fff;font-weight: 800;font-size: 4em;letter-spacing: -2px;text-align: center;text-shadow: 1px 2px 1px rgba(0, 0, 0, .6);}  h1:after {display: block;color: #fff;letter-spacing: 1px;content: 'loading...';font-size: .4em;text-align: center;}  .button-line {text-transform: uppercase;letter-spacing: 2px;background: transparent;border: 1px solid #fff;color: #fff;text-align: center;font-size: 1.4em;opacity: .8;padding: 20px 40px;text-decoration: none;transition: all .5s ease;margin: 0 auto;display: block;width: 100px;}  .button-line:hover {opacity: 1;}</style></head><body><div class=\"full-screen\"><div><h1>NaStation</h1></div></div></body></html>\n";

        Engine engine = Engine.newInstance(EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).language(Language.ENGLISH_US).build());
        Browser browser = engine.newBrowser();
        browser.set(OpenPopupCallback.class, new DefaultOpenPopupCallback());

        browserView = BrowserView.newInstance(browser);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            WaitingExitFrame waitingExitFrame = new WaitingExitFrame();

            frame = new JFrame(NA_STATION);
            frame.add(browserView);
            frame.setSize(800, 600);

            frame.setIconImage(createIcon());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int flag = JOptionPane.showConfirmDialog(null, "Are you sure to exit ?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (flag == JOptionPane.YES_OPTION) {

                        // Browser Engine close
                        engine.close();

                        // Block data loop exit
                        SystemService.me().setStopRun(true);

                        //System.exit(0);
                        frame.setVisible(false);
                        waitingExitFrame.setVisible(true);
                    }
                }
            });

        });

        browserView.getBrowser().mainFrame().ifPresent(mainFrame -> {
            mainFrame.loadHtml(loadingHtml);
        });

    }

    // Only public classes and static nested classes can be injected into JS.
    public static final class NaBridge {

        @JsAccessible // This public method is accessible from JS.
        @SuppressWarnings("unused") // To be called from JavaScript.
        public void openSwtWindow(String url) {
            //System.out.println("openSwtWindow >> "+url);
        }
    }

    public static class WaitingExitFrame extends JFrame {

        public WaitingExitFrame() {
            super(NA_STATION);
            setBounds(100, 100, 400, 300);

            JLabel jLabel = new JLabel("Waiting for "+NA_STATION+" to exit...", JLabel.CENTER);
            jLabel.setFont(new Font("Consolas", Font.BOLD, 12));

            JPanel p = new JPanel(new BorderLayout());
            p.add(jLabel, BorderLayout.CENTER);

            getContentPane().add(p);
            setIconImage(createIcon());
            setLocationRelativeTo(null);
            setVisible(false);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            /*
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle bounds = new Rectangle(screenSize);
                jframe.setBounds(bounds);
            */

            this.addWindowListener(new WindowAdapter() {

                @Override
                public void windowOpened(WindowEvent e) {
                    //super.windowOpened(e);

                    ThreadUtil.sleepSeconds(2);

                    int count = 0;
                    while (count < 20 && !SystemService.me().isRequestBlockDataOver()) {
                        ThreadUtil.sleepSeconds(3);
                        count++;
                    }

                    // close spring container
                    ctx.close();

                    System.exit(0);
                }
            });
        }
    }

    static ConfigurableApplicationContext ctx = null;

    private static void runAppServer(String[] args) {
        //LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
        SpringApplication app = new SpringApplication(Application.class);
        app.setHeadless(false);
        app.addListeners(new ApplicationPidFileWriter());
        app.setWebApplicationType(WebApplicationType.SERVLET);
        ctx = app.run(args);
    }

    public void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("");
    }

    //public static AdvancedBrowser advancedBrowser;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        //browserView.getBrowser().mainFrame().get().loadHtml("<html><body><h1>NaStation loading now ....</h1></body></html>");
        //browser.navigation().loadUrl("loading.html");

        if (frame != null) {

            frame.setSize(1360, 800);
            frame.setLocationRelativeTo(null);

            //GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            //gd.setFullScreenWindow(frame);
        }

        if (browserView != null) {
            browserView.getBrowser().mainFrame().ifPresent(mainFrame -> {
                mainFrame.loadUrl("http://localhost:" + port);

                /*
                JsObject jsObject = mainFrame.executeJavaScript("window");
                if (jsObject != null) {
                    // Inject Java object into JavaScript and associate it
                    // with the "window.java" JavaScript property.
                    jsObject.putProperty("NaBridge", new NaBridge());
                }
                mainFrame.executeJavaScript("window.NaBridge.openSwtWindow('')");
                */
                //advancedBrowser = new AdvancedBrowser("https://html5test.com//");

                /*
                int count = 0;
                while (true) {

                    if (count++ > 100) {
                        break;
                    }

                    JsObject jsObject = mainFrame.executeJavaScript("window");
                    if (jsObject != null) {
                        // Inject Java object into JavaScript and associate it
                        // with the "window.java" JavaScript property.
                        jsObject.putProperty("NaBridge", new NaBridge());

                        boolean vaadin = jsObject.hasProperty("Vaadin");
                        System.out.println("hasProperty vaadin = " + vaadin);

                        if (vaadin) {
                            break;
                        }
                    }
                    // Call the annotated public method of the injected Java object from JS.
                    //mainFrame.executeJavaScript("window.NaBridge.openSwtWindow('')");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/


            });
        }

    }

    public static Image createIcon() {
        try {
            InputStream in = Application.class.getResourceAsStream("/images/logo.png");
            BufferedImage image = ImageIO.read(in);
            return image;
        } catch (Exception e) {
            log.error("create icon error ", e);
            //JOptionPane.showMessageDialog(frame, "Failed to load form icon", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return null;//Toolkit.getDefaultToolkit().getImage(Application.class.getClassLoader().getResource("/images/logo.png"));
    }

    private final static class DefaultOpenPopupCallback implements OpenPopupCallback {

        private static final int DEFAULT_POPUP_WIDTH = 800;
        private static final int DEFAULT_POPUP_HEIGHT = 600;

        @Override
        public Response on(Params params) {
            Browser browser = params.popupBrowser();
            invokeLater(() -> {
                BrowserView view = BrowserView.newInstance(browser);
                JFrame frame = new JFrame();
                // Set your window icon here.
                // frame.setIconImage(image);
                frame.setIconImage(createIcon());

                frame.add(view, BorderLayout.CENTER);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        invokeLater(browser::close);
                    }
                });

                updateBounds(frame, params.initialBounds());

                browser.on(TitleChanged.class, event -> invokeLater(() ->
                        frame.setTitle(event.title())
                ));
                browser.on(BrowserClosed.class, event -> invokeLater(() -> {
                    frame.setVisible(false);
                    frame.dispose();
                }));
                browser.on(UpdateBoundsRequested.class, event -> invokeLater(() ->
                        updateBounds(frame, event.bounds())
                ));

                frame.setVisible(true);
            });
            return Response.proceed();
        }

        private static void updateBounds(JFrame frame, Rect bounds) {
            Size size = bounds.size();
            if (size.isEmpty()) {
                frame.setLocationByPlatform(true);
                frame.setSize(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT);
            } else {
                com.teamdev.jxbrowser.ui.Point origin = bounds.origin();
                frame.setLocation(new Point(origin.x(), origin.y()));
                Dimension dimension = new Dimension(size.width(), size.height());
                frame.getContentPane().setPreferredSize(dimension);
                frame.pack();
            }
        }
    }
}

