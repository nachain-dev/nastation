package org.nastation.module.vote.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.vote.data.VoteNode;
import org.nastation.module.vote.service.VoteNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Route(value = VoteNodeListView.Route_Value + "/:voteID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(VoteNodeListView.Page_Title)
public class VoteNodeListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "VoteNodeList";
    public static final String Page_Title = "Vote Node List";

    private final String VOTE_ID = "voteID";
    private final String VOTE_EDIT_ROUTE_TEMPLATE = "VoteNodeList/%d/edit";

    private Grid<VoteNode> grid = new Grid<>(VoteNode.class, false);

    private TextField id;
    private TextField voteInstance;
    private TextField voteAddress;
    private TextField amount;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private VoteNode voteNode;

    public VoteNodeListView(@Autowired VoteNodeService voteNodeService) {
        addClassNames("my-vote-list-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("ID");
        grid.addColumn("voteInstance").setAutoWidth(true).setHeader("Vote Instance");
        grid.addColumn("voteAddress").setAutoWidth(true).setHeader("Vote Address");
        grid.addColumn("amount").setAutoWidth(true).setHeader("Vote Amount");
        grid.setItems(query -> voteNodeService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(VOTE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(VoteNodeListView.class);
            }
        });

        // Configure Form
        //binder = new CollaborationBinder<>(Vote.class, userInfo);

        //Bind fields. This where you'd define e.g. validation rules
        //binder.forField(amount, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
        //        .bind("amount");
        //binder.forField(status, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
        //        .bind("status");

        //binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                /*
                if (this.vote == null) {
                    this.vote = new Vote();
                }
                binder.writeBean(this.vote);

                voteService.update(this.vote);
                clearForm();
                refreshGrid();
                Notification.show("Vote details stored.");
                UI.getCurrent().navigate(VoteListView.class);
                */
            } catch (Exception validationException) {
                Notification.show("An exception happened while trying to store the vote details.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> voteId = event.getRouteParameters().getInteger(VOTE_ID);
        /*
        if (voteId.isPresent()) {
            Optional<Vote> voteFromBackend = voteService.get(voteId.get());
            if (voteFromBackend.isPresent()) {
                populateForm(voteFromBackend.get());
            } else {
                Notification.show(String.format("The requested vote was not found, ID = %d", voteId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(VoteListView.class);
            }
        }
        */
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
        voteInstance = new TextField("Instance");
        voteAddress = new TextField("Vote Address");
        amount = new TextField("Amount");
        Component[] fields = new Component[]{id,voteInstance,voteAddress,amount};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        //createButtonLayout(editorLayoutDiv);

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
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(VoteNode value) {
        this.voteNode = value;
        String topic = null;
        if (this.voteNode != null && this.voteNode.getId() != null) {
            topic = "vote/" + this.voteNode.getId();
        } else {
        }
        //binder.setTopic(topic, () -> this.vote);
        //avatarGroup.setTopic(topic);

    }
}