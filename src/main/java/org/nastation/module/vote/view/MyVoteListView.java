package org.nastation.module.vote.view;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.vote.data.Vote;
import org.nastation.module.vote.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.Optional;

@Route(value = MyVoteListView.Route_Value + "/:voteID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(MyVoteListView.Page_Title)
public class MyVoteListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "MyVoteList";
    public static final String Page_Title = "My Vote List";

    private final String VOTE_ID = "voteID";
    private final String VOTE_EDIT_ROUTE_TEMPLATE = "MyVoteList/%d/edit";

    private Grid<Vote> grid = new Grid<>(Vote.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField id;
    private TextField voteInstance;
    private TextField voteAddress;
    private TextField beneficiaryAddress;
    private TextField nominateAddress;
    private TextField amount;
    private TextField status;
    private TextField txHash;
    private DateTimePicker voteTime;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Vote vote;

    public MyVoteListView(@Autowired VoteService voteService) {
        addClassNames("my-vote-list-view", "flex", "flex-col", "h-full");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        //UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

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
        grid.addColumn("voteInstance").setAutoWidth(true).setHeader("Instance");
        grid.addColumn("voteAddress").setAutoWidth(true).setHeader("Vote Address");
        grid.addColumn("beneficiaryAddress").setAutoWidth(true).setHeader("Beneficiary Address");
        grid.addColumn("nominateAddress").setAutoWidth(true).setHeader("Nominate Address");
        grid.addColumn("amount").setAutoWidth(true).setHeader("Amount");
        grid.addColumn("status").setAutoWidth(true).setHeader("Status");
        grid.addColumn("txHash").setAutoWidth(true).setHeader("TX Hash");
        grid.addColumn("voteTime").setAutoWidth(true).setHeader("Vote Time");
        grid.setItems(query -> voteService.list(
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
                UI.getCurrent().navigate(MyVoteListView.class);
            }
        });

        // Configure Form
        //binder = new CollaborationBinder<>(Vote.class, userInfo);

        // Bind fields. This where you'd define e.g. validation rules
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
                Notification.show("An exception happened while trying to submit the vote details.");
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
        voteInstance = new TextField("Vote Instance");
        voteAddress = new TextField("Vote Address");
        beneficiaryAddress = new TextField("Beneficiary Address");
        nominateAddress = new TextField("Nominate Address");

        amount = new TextField("Amount");
        status = new TextField("Status");
        txHash = new TextField("Tx Hash");
        voteTime = new DateTimePicker("Vote Time");
        voteTime.setStep(Duration.ofSeconds(1));
        Component[] fields = new Component[]{id,voteInstance,voteAddress,beneficiaryAddress,nominateAddress, amount, status, txHash, voteTime};

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
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Vote value) {
        this.vote = value;
        String topic = null;
        if (this.vote != null && this.vote.getId() != null) {
            topic = "vote/" + this.vote.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        //binder.setTopic(topic, () -> this.vote);
        //avatarGroup.setTopic(topic);

    }
}