package org.nastation.module.dfs.view;

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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.apache.commons.compress.utils.Lists;
import org.nastation.module.dapp.view.PublishDAppFormView;
import org.nastation.module.dfs.data.FileItem;
import org.nastation.module.dfs.service.FileItemService;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@PageTitle(PinFileItemListView.Page_Title)
@Route(value = "PinFileItemListView/:fileItemID?/:action?(edit)", layout = MainLayout.class)
public class PinFileItemListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "PinFileItemListView";
    public static final String Page_Title = "Pin File List";

    private final String FILE_ITEM_ID = "fileItemID";
    private final String FILE_ITEM_EDIT_ROUTE_TEMPLATE = "PinFileItemListView/%d/edit";

    private Grid<FileItem> grid = new Grid<>(FileItem.class, false);

    private TextField id;
    private TextField fileName;
    private TextField fileSize;
    private TextField fileType;
    private TextField fileHash;
    private DateTimePicker addTime;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<FileItem> binder;

    private FileItem fileItem;

    private FileItemService fileItemService;

    public PinFileItemListView(@Autowired FileItemService fileItemService) {
        this.fileItemService = fileItemService;
        addClassNames("file-item-list-view", "flex", "flex-col", "h-full");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("ID");
        grid.addColumn("fileName").setAutoWidth(true).setHeader("File Name");
        grid.addColumn("fileSize").setAutoWidth(true).setHeader("File Size");
        grid.addColumn("fileType").setAutoWidth(true).setHeader("File Type");
        grid.addColumn("fileHash").setAutoWidth(true).setHeader("File Hash");
        grid.addColumn("addTime").setAutoWidth(true).setHeader("Add Time");

        /*Action*/
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, rowData) -> {
            Button deploy = new Button("Deploy");
            deploy.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

            deploy.addClickListener(e -> {
                try {
                    UI.getCurrent().navigate(PublishDAppFormView.class);
                } catch (Exception validationException) {
                    Notification.show("exception");
                }
            });

            Button view = new Button("View");
            view.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(view, deploy);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);


        grid.setItems(query -> fileItemService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        refreshGrid();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FILE_ITEM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PinFileItemListView.class);
            }
        });

        // Configure Form
        //binder = new BeanValidationBinder<>(FileItem.class);
        // Bind fields. This where you'd define e.g. validation rules
        //binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                /*
                if (this.fileItem == null) {
                    this.fileItem = new FileItem();
                }
                binder.writeBean(this.fileItem);

                fileItemService.update(this.fileItem);
                clearForm();
                refreshGrid();
                Notification.show("FileItem details stored.");
                UI.getCurrent().navigate(PinFileItemListView.class);
                */
            } catch (Exception exception) {
                Notification.show("An exception happened while trying to store the fileItem details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> fileItemId = event.getRouteParameters().getInteger(FILE_ITEM_ID);
        if (fileItemId.isPresent()) {
            Optional<FileItem> fileItemFromBackend = fileItemService.get(fileItemId.get());
            if (fileItemFromBackend.isPresent()) {
                populateForm(fileItemFromBackend.get());
            } else {
                Notification.show(String.format("The requested fileItem was not found, ID = %d", fileItemId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PinFileItemListView.class);
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
        fileName = new TextField("File Name");
        fileSize = new TextField("File Size");
        fileType = new TextField("File Type");
        //bucketName = new TextField("Bucket Name");
        fileHash = new TextField("File Hash");
        //authorAddress = new TextField("Author Address");
        addTime = new DateTimePicker("Add Time");
        addTime.setStep(Duration.ofSeconds(1));
        Component[] fields = new Component[]{id, fileName, fileSize, fileType, fileHash, addTime};
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
        List<FileItem> dataList = Lists.newArrayList();

        /*
        for (int i = 1; i <= 10; i++) {
            String addr = "N123" + i;
            FileItem one = new FileItem();
            one.setId(i + 1);
            one.setFileHash("0x00000" + i);
            one.setFileName("name" + i);
            one.setFileSize("size" + i);
            one.setFileType(".NAPP");
            one.setFee("0.1 NAC");

            dataList.add(one);
        }
        */

        grid.setItems(dataList);
        grid.getDataProvider().refreshAll();

    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(FileItem value) {
        this.fileItem = value;
        binder.readBean(this.fileItem);

    }
}
