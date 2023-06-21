package org.nastation.common.util;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.protocol.NFTProtocol;
import org.nastation.common.model.AppNewVersion;
import org.nastation.common.service.SystemService;
import org.nastation.components.QrImageSource;
import org.nastation.components.style.*;
import org.nastation.components.style.css.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/*

ComponentUtil.setData(UI.getCurrent(), YourClass.class, yourClassInstance); and ComponentUtil.getData(UI.getCurrent(), YourClass.class);

RouterLink l = new RouterLink("link", MainView.class);
QueryParameters params = QueryParameters.simple(Map.of("foo", "bar"));
l.setQueryParameters(params);

*/
@Slf4j
public class CompUtil {

    public static final String IMG_PATH = "images/";

    /**
     * Thread-unsafe formatters.
     */
    private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal
            .withInitial(() -> new DecimalFormat("###,###.00", DecimalFormatSymbols.getInstance(Locale.US)));

    private static final ThreadLocal<DateTimeFormatter> dateFormat = ThreadLocal
            .withInitial(() -> DateTimeFormatter.ofPattern("MMM dd, YYYY"));

    /* ==== BUTTONS ==== */

    // Styles

    public static Button createPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createPrimaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createTertiaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryInlineButton(String text) {
        return createButton(text, ButtonVariant.LUMO_TERTIARY_INLINE);
    }

    public static Button createTertiaryInlineButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_TERTIARY_INLINE);
    }

    public static Button createTertiaryInlineButton(String text,
                                                    VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_TERTIARY_INLINE);
    }

    public static Button createSuccessButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createSuccessPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createSuccessPrimaryButton(String text,
                                                    VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorButton(String text) {
        return createButton(text, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorPrimaryButton(String text,
                                                  VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastButton(String text) {
        return createButton(text, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastPrimaryButton(String text,
                                                     VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    // Size

    public static Button createSmallButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SMALL);
    }

    public static Button createSmallButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SMALL);
    }

    public static Button createSmallButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SMALL);
    }

    public static Button createLargeButton(String text) {
        return createButton(text, ButtonVariant.LUMO_LARGE);
    }

    public static Button createLargeButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_LARGE);
    }

    public static Button createLargeButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_LARGE);
    }

    // Text

    public static Button createButton(String text, ButtonVariant... variants) {
        Button button = new Button(text);
        button.addThemeVariants(variants);
        button.getElement().setAttribute("aria-label", text);
        return button;
    }

    // Icon

    public static Button createButton(VaadinIcon icon,
                                      ButtonVariant... variants) {
        Button button = new Button(new Icon(icon));
        button.addThemeVariants(variants);
        return button;
    }

    // Text and icon

    public static Button createButton(String text, VaadinIcon icon,
                                      ButtonVariant... variants) {
        Icon i = new Icon(icon);
        i.getElement().setAttribute("slot", "prefix");
        Button button = new Button(text, i);
        button.addThemeVariants(variants);
        return button;
    }

    /* ==== TEXTFIELDS ==== */

    public static TextField createSmallTextField() {
        TextField textField = new TextField();
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        return textField;
    }

    /* ==== LABELS ==== */

    public static Label createLabel(FontSize size, TextColor color,
                                    String text) {
        Label label = new Label(text);
        setFontSize(size, label);
        setTextColor(color, label);
        return label;
    }

    public static Label createLabel(FontSize size, String text) {
        return createLabel(size, TextColor.BODY, text);
    }

    public static Label createLabel(TextColor color, String text) {
        return createLabel(FontSize.M, color, text);
    }

    public static Label createH1Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H1);
        return label;
    }

    public static Label createH2Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H2);
        return label;
    }

    public static Label createH3Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H3);
        return label;
    }

    public static Label createH4Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H4);
        return label;
    }

    public static Label createH5Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H5);
        return label;
    }

    public static Label createH6Label(String text) {
        Label label = new Label(text);
        label.addClassName(LumoStyles.Heading.H6);
        return label;
    }

    /* === MISC === */

	/*public static String formatAddress(Address address) {
		return address.getStreet() + "\n" + address.getCity() + ", "
				+ address.getCity() + " " + address.getZip();
	}*/

    public static Button createFloatingActionButton(VaadinIcon icon) {
        Button button = createPrimaryButton(icon);
        button.addThemeName("fab");
        return button;
    }

	/*
	public static FlexLayout createPhoneLayout() {
		TextField prefix = new TextField();
		prefix.setValue("+358");
		prefix.setWidth("80px");

		TextField number = new TextField();
		number.setValue(DummyData.gettext());

		FlexBoxLayout layout = new FlexBoxLayout(prefix, number);
		layout.setFlexGrow(1, number);
		layout.setSpacing(Right.S);
		return layout;
	}*/

    /* === NUMBERS === */

    public static String formatAmount(Double amount) {
        return decimalFormat.get().format(amount);
    }

    public static String formatAmount(int amount) {
        return decimalFormat.get().format(amount);
    }

    public static Label createAmountLabel(double amount) {
        Label label = createH5Label(formatAmount(amount));
        label.addClassName(LumoStyles.FontFamily.MONOSPACE);
        return label;
    }

    public static String formatUnits(int units) {
        return NumberFormat.getIntegerInstance().format(units);
    }

    public static Label createUnitsLabel(int units) {
        Label label = new Label(formatUnits(units));
        label.addClassName(LumoStyles.FontFamily.MONOSPACE);
        return label;
    }

    /* === ICONS === */

    public static Icon createPrimaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.PRIMARY, i);
        return i;
    }

    public static Icon createSecondaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.SECONDARY, i);
        return i;
    }

    public static Icon createTertiaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.TERTIARY, i);
        return i;
    }

    public static Icon createDisabledIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.DISABLED, i);
        return i;
    }

    public static Icon createSuccessIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.SUCCESS, i);
        return i;
    }

    public static Icon createErrorIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(TextColor.ERROR, i);
        return i;
    }

    public static Icon createSmallIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(IconSize.S.getClassName());
        return i;
    }

    public static Icon createLargeIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(IconSize.L.getClassName());
        return i;
    }

    // Combinations

    public static Icon createIcon(IconSize size, TextColor color,
                                  VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassNames(size.getClassName());
        setTextColor(color, i);
        return i;
    }

    /* === DATES === */

    public static String formatDate(LocalDate date) {
        return dateFormat.get().format(date);
    }

    public static String formatDate(LocalDateTime date) {
        return dateFormat.get().format(date);
    }

    /* === NOTIFICATIONS === */

    public static void showNotification(String text) {
        Notification.show(text, 3000, Notification.Position.BOTTOM_CENTER);
    }

    /* === CSS UTILITIES === */

    public static void setAlignSelf(AlignSelf alignSelf,
                                    Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("align-self",
                    alignSelf.getValue());
        }
    }

    public static void setBackgroundColor(String backgroundColor,
                                          Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("background-color",
                    backgroundColor);
        }
    }

    public static void setBorderRadius(BorderRadius borderRadius,
                                       Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("border-radius",
                    borderRadius.getValue());
        }
    }

    public static void setBoxSizing(BoxSizing boxSizing,
                                    Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("box-sizing",
                    boxSizing.getValue());
        }
    }

    public static void setColSpan(int span, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("colspan",
                    Integer.toString(span));
        }
    }

    public static void setFontSize(FontSize fontSize,
                                   Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("font-size",
                    fontSize.getValue());
        }
    }

    public static void setFontWeight(FontWeight fontWeight,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("font-weight",
                    fontWeight.getValue());
        }
    }

    public static void setLineHeight(LineHeight lineHeight,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("line-height",
                    lineHeight.getValue());
        }
    }

    public static void setLineHeight(String value,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("line-height",
                    value);
        }
    }

    public static void setMaxWidth(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("max-width", value);
        }
    }

    public static void setOverflow(Overflow overflow, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("overflow",
                    overflow.getValue());
        }
    }

    public static void setPointerEvents(PointerEvents pointerEvents, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("pointer-events",
                    pointerEvents.getValue());
        }
    }

    public static void setShadow(Shadow shadow, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("box-shadow",
                    shadow.getValue());
        }
    }

    public static void setTextAlign(TextAlign textAlign,
                                    Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("text-align",
                    textAlign.getValue());
        }
    }

    public static void setTextColor(TextColor textColor, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("color", textColor.getValue());
        }
    }

    public static void setTextOverflow(TextOverflow textOverflow, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("text-overflow", textOverflow.getValue());
        }
    }

    public static void setTheme(String theme, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("theme", theme);
        }
    }

    public static void setTooltip(String tooltip, Component... components) {
        for (Component component : components) {
            component.getElement().setProperty("title", tooltip);
        }
    }

    public static void setWhiteSpace(WhiteSpace whiteSpace,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().setProperty("white-space",
                    whiteSpace.getValue());
        }
    }

    public static void setWidth(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("width", value);
        }
    }


    /* === ACCESSIBILITY === */

    public static void setAriaLabel(String value, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("aria-label", value);
        }
    }


    public static Component createComponent(String mimeType, String fileName,
                                            InputStream stream) {
        if (mimeType.startsWith("text")) {
            return createTextComponent(stream);
        } else if (mimeType.startsWith("image")) {
            Image image = new Image();
            try {

                byte[] bytes = IOUtils.toByteArray(stream);
                image.getElement().setAttribute("src", new StreamResource(
                        fileName, () -> new ByteArrayInputStream(bytes)));
                try (ImageInputStream in = ImageIO.createImageInputStream(
                        new ByteArrayInputStream(bytes))) {
                    final Iterator<ImageReader> readers = ImageIO
                            .getImageReaders(in);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        try {
                            reader.setInput(in);
                            image.setWidth(reader.getWidth(0) + "px");
                            image.setHeight(reader.getHeight(0) + "px");
                        } finally {
                            reader.dispose();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setSizeFull();
            return image;
        }
        Div content = new Div();
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                mimeType, MessageDigestUtil.sha256(stream.toString()));
        content.setText(text);
        return content;

    }

    public static Component createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "Reading stream exception";
        }
        return new Text(text);
    }

    public static void showOutput(String text, Component content,
                                  HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }

    public static void showTxSuccess(String action) {
        showSuccess("New transaction of " + StringUtils.defaultIfEmpty(action, "unknown action") + " was sent successfully");
    }

    public static String showTxFail(String action) {

        String msg = "Failed to "+StringUtils.defaultIfEmpty(action, "unknown action");
        CompUtil.showError(msg);

        return msg;
    }

    public static void showSuccess(String text) {
        Notification notification = Notification.show(text, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void showError(String text) {
        Notification notification = Notification.show(text, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static Button createLargePrimaryBtn(String text) {
        Button btn = new Button(text);
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return btn;
    }

    public static Button createLargeDefaultBtn(String text) {
        Button btn = new Button(text);
        return btn;
    }

    public static HorizontalLayout configLayout(HasComponents comp) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        layout.setPadding(true);
        layout.setMargin(true);
        layout.setSpacing(true);
        comp.add(layout);
        return layout;
    }

    public static Component createTitle(String text) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(new H3(text));
        return layout;
    }

    public static Component createBtnPairLayout(Button left, Button right) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        buttonLayout.addClassName("button-layout");
        right.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button copy = new Button("Copy and backup", new Icon(VaadinIcon.COPY));

        buttonLayout.add(left);
        buttonLayout.add(right);
        return buttonLayout;
    }

    public static Image createImage2(String path) {
        Image image = null;
        try {

            File file = org.springframework.util.ResourceUtils.getFile("classpath:META-INF/resources/images/" + path);
            //System.out.println(file.getAbsolutePath());

            String filename = path;
            byte[] imageBytes = FileUtils.readFileToByteArray(file);
            StreamResource resource = new StreamResource(filename, () -> new ByteArrayInputStream(imageBytes));
            image = new Image(resource, filename);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image createIcon2(String path) {
        Image image = null;

        try {
            String filename = path;
            byte[] imageBytes = FileUtils.readFileToByteArray(new File("/icons/" + filename));
            StreamResource resource = new StreamResource(filename, () -> new ByteArrayInputStream(imageBytes));
            image = new Image(resource, filename);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image createImage(String path) {
        Image image = null;

        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:META-INF/resources/images/" + path);
            String filename = path;
            byte[] imageBytes = FileUtils.readFileToByteArray(new File(file.getAbsolutePath()));
            StreamResource resource = new StreamResource(filename, () -> new ByteArrayInputStream(imageBytes));
            image = new Image(resource, filename);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image createImageBySrc(String path) {
        Image image = new Image();
        image.setClassName("logo");
        image.setSrc(CompUtil.IMG_PATH + path);
        return image;
    }

    public static Image createImageBySrc(String path,int width,int height) {
        Image image = new Image();
        image.setWidth(String.valueOf(width));
        image.setHeight(String.valueOf(height));
        image.setClassName("logo");
        image.setSrc(CompUtil.IMG_PATH + path);
        return image;
    }

    public static Image createNaLogo() {
        return CompUtil.createImageBySrc("logos/na.png");
    }

    public static Image createNaLogo(int width,int height) {
        return CompUtil.createImageBySrc("logos/na.png",width,height);
    }

    public static Image createNaLogo_x80() {
        return CompUtil.createImageBySrc("logos/na.png");
    }

    public static Image createNomcLogo() {
        return CompUtil.createImageBySrc("logos/nomc.png");
    }

    public static Image createUsdnLogo() {
        return CompUtil.createImageBySrc("logos/usdn.png");
    }

    public static Image createCommonCoinLogo() {
        return CompUtil.createImageBySrc("logos/common.png");
    }

    public static Image createIcon(String path) {
        Image image = null;

        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:META-INF/resources/icons/" + path);
            String filename = path;
            byte[] imageBytes = FileUtils.readFileToByteArray(file);
            StreamResource resource = new StreamResource(filename, () -> new ByteArrayInputStream(imageBytes));
            image = new Image(resource, filename);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image createQrImage(QrImageSource qis) throws IOException {
        //layout.addComponent(new Image("Image title", resource));
        //QrImageSource qis = new QrImageSource();
        //qis.setText(text);
        //qis.setHeight(300);
        //qis.setWidth(300);

        //StreamResource imageResource = new StreamResource("myimage.png",
        //        () -> getClass().getResourceAsStream("/images/logo.png"));
        //
        //Image image = new Image(imageResource, "");

        String text = qis.getText();

        InputStream stream = qis.getStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(stream));
        StreamResource resource = new StreamResource(text + ".png", () -> byteArrayInputStream);

        return new Image(resource, text);
    }

    public static void setClipboardText(String text) {
        UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", text);
        CompUtil.showSuccess("Copied");
    }


    public static void showNewVersionDialog(AppNewVersion appNewVersion) {

        if (appNewVersion == null) {
            return;
        }

        boolean isForceUpdate = appNewVersion.getForceUpdate() == 1;

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("New version found (" + appNewVersion.getVersionNumberDesc() + ")");

        String versionInfo = appNewVersion.getVersionInfo();

        String[] split = StringUtils.split(versionInfo, ";");
        String html = "";
        for (String line : split) {
            html += line + "<br/>";
        }

        String textHtml = "<div><p>" + html + "</p><p>Do you want to <b>upgrade</b> or <b>cancel</b> ?</p><p style='color: #dc3545!important;'>*Please <b>close</b> the current program before running the new version of the application.</p></div>";
        dialog.setText(new Html(textHtml).getElement());

        Button saveButton = new Button("Upgrade", VaadinIcon.CHECK_CIRCLE_O.create());
        saveButton.addClickListener(e -> {
            //LaunchUtil.launchBrowser(appNewVersion.getAppUrl(), "Visit app download url");
            //dialog.getUI().ifPresent(ui -> ui.getPage().open(appNewVersion.getAppUrl()));
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    java.net.URI uri = java.net.URI.create(appNewVersion.getAppUrl());
                    java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                    if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                        dp.browse(uri);
                    }
                } catch (Exception ex) {
                    log.error("Open app new version url error", ex);
                }
            }

            if (!isForceUpdate) {
                dialog.close();
                SystemService.me().setAppNewVersion(null);
            }

        });
        saveButton.getElement().setAttribute("theme", "primary");
        dialog.setConfirmButton(saveButton.getElement());

        if (!isForceUpdate) {
            dialog.setCancelButton("Cancel", e -> dialog.close());
        }

        dialog.open();
    }

    public static ConfirmDialog showLoadingDialog(String text) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Loading...");
        String textHtml = "<div>"+text+"</div>";
        dialog.setText(new Html(textHtml).getElement());
        dialog.open();
        return dialog;
    }

    // CompUtil.showEnableByBlockHeightDialog()
    public static ConfirmDialog showEnableByBlockHeightDialog(long currentBlockHeight) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("TIPS");
        String textHtml = "<div>Now that the new version of the miner software has been detected, the deployment has been completed, and the core team is verifying the node data, maybe it will take a while to complete this work.</div>";
        //String textHtml = "<div>The current module function will be enabled in block: 3123440. Number of remaining blocks: "+(3123440-currentBlockHeight)+"</div>";
        dialog.setText(new Html(textHtml).getElement());
        dialog.open();
        return dialog;
    }

    public static ConfirmDialog showDeleteConfirmDialog(String title,String content) {

        final ConfirmDialog dialog = new ConfirmDialog(
                title,
                content,
                "Delete", (event) -> {
            Notification.show("Delete completed");
        });

        dialog.open();

        return dialog;
    }


    public static UI getMainLayout() {

        UI ui = null;
        try {
            ui = UI.getCurrent().getUI().get();
        } catch (Exception e) {
            log.error("get main layout error:", e);
        }

        return ui;
    }

    public static String subAbbreviation(String text) {
        if (text == null) {
            text = "";
        }
        return StringUtils.substring(text, 0, text.length() > 3 ? 3 : text.length());
    }

    public static H2 getTextCenterH2(String title) {
        H2 h2 = new H2();
        h2.setText(title);
        h2.getStyle().set("text-align", "center");
        return h2;
    }

    public static TextField getNacFeeText(String label) {
        TextField tf = new TextField(label);
        tf.setValue("- NAC");
        tf.setReadOnly(true);
        return tf;
    }

    public static FormLayout getCenterFormLayout(Component... components) {
        FormLayout formLayout = new FormLayout(components);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        if (ArrayUtils.isNotEmpty(components)) {
            for (Component comp : components) {
                formLayout.setColspan(comp, 2);
            }
        }

        return formLayout;
    }

    public static String tokenBalanceEntryLabelGenerator(Map.Entry<Long, BigInteger> tokenBalanceEntry) {
        if (tokenBalanceEntry == null) {
            return "";
        }
        return String.format("%s : %s", TokenUtil.getTokenSymbol(tokenBalanceEntry.getKey()), new BigDecimal(NumberUtil.bigIntToNacDouble(tokenBalanceEntry.getValue())).setScale(8, BigDecimal.ROUND_DOWN).toPlainString());
    }

    public static  String nftCollLabelGenerator(NftCollWrap wrap) {

        if (wrap == null) {
            return "";
        }

        NftCollection nftCollection = wrap.getNftCollection();
        long mintAmount = nftCollection.getMintAmount();
        long mintTotal = nftCollection.getNftProtocol().getMintPricesBatch().stream().mapToLong(Long::longValue).sum();

        long instance = nftCollection.getInstance();
        String instanceName = InstanceUtil.getInstanceName(instance);

        NFTProtocol nftProtocol = nftCollection.getNftProtocol();

        long mintTokenId = nftProtocol.getMintTokenId();
        String mintTokenSymbol = TokenUtil.getTokenSymbol(mintTokenId);

        return String.format("%s [Mint by %s] (%s/%s)", instanceName, mintTokenSymbol,mintAmount,mintTotal);
    }

}
