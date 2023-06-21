package org.nastation.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(String caption, String text, String confirmButtonText,
                         Runnable confirmListener) {

        final VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        add(content);

        add(new H3(caption));
        add(new Span(text));

        final HorizontalLayout buttons = new HorizontalLayout();
        buttons.setPadding(false);
        add(buttons);

        final Button confirm = new Button(confirmButtonText, e -> {
            confirmListener.run();
            close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(confirm);

        final Button cancel = new Button("Cancel", e -> close());
        buttons.add(cancel);

    }

}


//final Button deleteButton = new Button(
//        VaadinIcon.MINUS_CIRCLE_O.create(), event -> {
//
//    // Ask for confirmation before deleting stuff
//    final ConfirmDialog dialog = new ConfirmDialog(
//            "Please confirm",
//            "Are you sure you want to delete the category? Books in this category will not be deleted.",
//            "Delete", () -> {
//        DataService.get()
//                .deleteCategory(category.getId());
//        dataProvider.getItems().remove(category);
//        dataProvider.refreshAll();
//        Notification.show("Category Deleted.");
//    });
//
//    dialog.open();
//
//});
//deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

