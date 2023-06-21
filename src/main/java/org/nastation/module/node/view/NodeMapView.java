package org.nastation.module.node.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.components.leafletmap.LeafletMap;
import org.nastation.module.pub.view.MainLayout;

@Route(value = NodeMapView.Route_Value, layout = MainLayout.class)
@PageTitle(NodeMapView.Page_Title)
public class NodeMapView extends VerticalLayout {

    public static final String Route_Value = "NodeMap";
    public static final String Page_Title = "Node Map";

    private LeafletMap map = new LeafletMap();

    public NodeMapView() {
        setSizeFull();
        setPadding(false);
        map.setSizeFull();
        map.setView(40.71916, 285.936356, 5);
        map.addMarker(40.71916, 285.936356,"<b>0.0.0.0 <br/> USA Full Node</b>");
        map.addMarker(35.764941, 260.768127,"<b>0.0.0.0 <br/> NewYork Full Node</b>");
        add(map);
    }
}
