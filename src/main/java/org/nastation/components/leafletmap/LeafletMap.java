package org.nastation.components.leafletmap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

//@JsModule("./components/leafletmap/leaflet-map.ts")
//@Tag("leaflet-map")
//@CssImport("leaflet/dist/leaflet.css")
public class LeafletMap extends Component implements HasSize {

    public void setView(double latitude, double longitude, int zoomLevel) {
        //getElement().callJsFunction("setView", latitude, longitude, zoomLevel);
    }

    public void addMarker(double latitude, double longitude, String zoomLevel) {
        //getElement().callJsFunction("addMarker", latitude, longitude, zoomLevel);
    }

}
