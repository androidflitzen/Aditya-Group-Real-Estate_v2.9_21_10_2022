package com.flitzen.adityarealestate_new.Items;

public class Item_location_list {
    public String latitud;
    public String longitud;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitud(String latitud) {
        return this.latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud(String longitud) {
        return this.longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
