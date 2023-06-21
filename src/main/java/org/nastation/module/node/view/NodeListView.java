package org.nastation.module.node.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.nastation.module.node.data.NodeInfo;
import org.nastation.module.pub.view.MainLayout;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Route(value = NodeListView.Route_Value, layout = MainLayout.class)
@PageTitle(NodeListView.Page_Title)
public class NodeListView extends Div {

    public static final String Route_Value = "NodeList";
    public static final String Page_Title = "Node List";

    private GridPro<NodeInfo> grid;
    private ListDataProvider<NodeInfo> dataProvider;

    private Grid.Column<NodeInfo> idColumn;
    private Grid.Column<NodeInfo> clientColumn;
    private Grid.Column<NodeInfo> amountColumn;
    private Grid.Column<NodeInfo> statusColumn;
    private Grid.Column<NodeInfo> typeColumn;
    private Grid.Column<NodeInfo> dateColumn;

    public NodeListView() {
        addClassName("node-list-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<>(getClients());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createClientColumn();
        createAmountColumn();
        createStatusColumn();
        createTypeColumn();
        createDateColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(NodeInfo::getId, "id").setHeader("ID").setWidth("120px").setFlexGrow(0);
    }

    private void createClientColumn() {
        clientColumn = grid.addColumn(new ComponentRenderer<>(client -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(client.getClient());
            hl.add(span);
            return hl;
        })).setComparator(client -> client.getClient()).setHeader("Client IP");
    }

    private void createAmountColumn() {
        amountColumn = grid
                .addEditColumn(NodeInfo::getAmount,new NumberRenderer<>(client -> client.getAmount(), NumberFormat.getCurrencyInstance(Locale.US)))
                .text((item, newValue) -> item.setAmount(Double.parseDouble(newValue)))
                .setComparator(client -> client.getAmount()).setHeader("Amount");
    }

    private void createTypeColumn() {
        typeColumn = grid.addEditColumn(NodeInfo::getClient, new ComponentRenderer<>(client -> {
            Span span = new Span();
            span.setText(client.getType());
            span.getElement().setAttribute("theme", "badge " + client.getStatus());
            return span;
        })).select((item, newValue) -> item.setStatus(newValue), Arrays.asList("Pending", "Success", "Error"))
                .setComparator(client -> client.getStatus()).setHeader("Type");
    }

    private void createStatusColumn() {
        statusColumn = grid.addEditColumn(NodeInfo::getClient, new ComponentRenderer<>(client -> {
            Span span = new Span();
            span.setText(client.getStatus());
            span.getElement().setAttribute("theme", "badge " + client.getStatus().toLowerCase());
            return span;
        })).select((item, newValue) -> item.setStatus(newValue), Arrays.asList("FULL NODE", "Common NODE"))
                .setComparator(client -> client.getStatus()).setHeader("Status");
    }

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateRenderer<>(client -> LocalDate.parse(client.getDate()),
                        DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(client -> client.getDate()).setHeader("Date").setWidth("180px").setFlexGrow(0);
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(Integer.toString(client.getId()), idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField clientFilter = new TextField();
        clientFilter.setPlaceholder("Filter");
        clientFilter.setClearButtonVisible(true);
        clientFilter.setWidth("100%");
        clientFilter.setValueChangeMode(ValueChangeMode.EAGER);
        clientFilter.addValueChangeListener(event -> dataProvider
                .addFilter(client -> StringUtils.containsIgnoreCase(client.getClient(), clientFilter.getValue())));
        filterRow.getCell(clientColumn).setComponent(clientFilter);

        TextField amountFilter = new TextField();
        amountFilter.setPlaceholder("Filter");
        amountFilter.setClearButtonVisible(true);
        amountFilter.setWidth("100%");
        amountFilter.setValueChangeMode(ValueChangeMode.EAGER);
        amountFilter.addValueChangeListener(event -> dataProvider.addFilter(client -> StringUtils
                .containsIgnoreCase(Double.toString(client.getAmount()), amountFilter.getValue())));
        filterRow.getCell(amountColumn).setComponent(amountFilter);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setItems(Arrays.asList("Pending", "Success", "Error"));
        statusFilter.setPlaceholder("Filter");
        statusFilter.setClearButtonVisible(true);
        statusFilter.setWidth("100%");
        statusFilter.addValueChangeListener(
                event -> dataProvider.addFilter(client -> areStatusesEqual(client, statusFilter)));
        filterRow.getCell(statusColumn).setComponent(statusFilter);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.setItems(Arrays.asList("FULL NODE", "Common NODE"));
        typeFilter.setPlaceholder("Filter");
        typeFilter.setClearButtonVisible(true);
        typeFilter.setWidth("100%");
        typeFilter.addValueChangeListener(
                event -> dataProvider.addFilter(client -> areStatusesEqual(client, typeFilter)));
        filterRow.getCell(typeColumn).setComponent(typeFilter);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPlaceholder("Filter");
        dateFilter.setClearButtonVisible(true);
        dateFilter.setWidth("100%");
        dateFilter.addValueChangeListener(event -> dataProvider.addFilter(client -> areDatesEqual(client, dateFilter)));
        filterRow.getCell(dateColumn).setComponent(dateFilter);
    }

    private boolean areStatusesEqual(NodeInfo client, ComboBox<String> statusFilter) {
        String statusFilterValue = statusFilter.getValue();
        if (statusFilterValue != null) {
            return StringUtils.equals(client.getStatus(), statusFilterValue);
        }
        return true;
    }

    private boolean areDatesEqual(NodeInfo client, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = LocalDate.parse(client.getDate());
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }

    // the miners are closed now
    private List<NodeInfo> getClients() {
        return Arrays.asList(
                createClient(4957, "https://randomuser.me/api/portraits/women/42.jpg", "192.168.1.1", 47427.0,
                        "Success", "2019-05-09"),
                createClient(675, "https://randomuser.me/api/portraits/women/24.jpg", "192.168.1.1", 70503.0,
                        "Success", "2019-05-09"),
                createClient(6816, "https://randomuser.me/api/portraits/men/42.jpg", "192.168.1.1", 58931.0,
                        "Success", "2019-05-07"),
                createClient(5144, "https://randomuser.me/api/portraits/women/76.jpg", "192.168.1.1", 25053.0,
                        "Pending", "2019-04-25"),
                createClient(9800, "https://randomuser.me/api/portraits/men/24.jpg", "192.168.1.1", 7319.0,
                        "Pending", "2019-04-22"),
                createClient(3599, "https://randomuser.me/api/portraits/women/94.jpg", "192.168.1.1", 18441.0,
                        "Error", "2019-04-17"),
                createClient(3989, "https://randomuser.me/api/portraits/men/76.jpg", "192.168.1.1", 33376.0, "Pending",
                        "2019-04-17"),
                createClient(1077, "https://randomuser.me/api/portraits/men/94.jpg", "192.168.1.1", 75774.0,
                        "Success", "2019-02-26"),
                createClient(8942, "https://randomuser.me/api/portraits/men/16.jpg", "192.168.1.1", 82531.0,
                        "Pending", "2019-02-21"));
    }

    private NodeInfo createClient(int id, String img, String client, double amount, String status, String date) {
        NodeInfo c = new NodeInfo();
        c.setId(id);
        c.setImg(img);
        c.setClient(client);
        c.setAmount(amount);
        c.setStatus(status);
        c.setDate(date);
        c.setType("FULL NODE");

        return c;
    }
};
