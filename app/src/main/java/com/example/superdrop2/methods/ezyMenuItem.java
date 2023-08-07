package com.example.superdrop2.methods;

public class ezyMenuItem {
    private int iconResource;
    private String name;

    public ezyMenuItem(int iconResource, String name) {
        this.iconResource = iconResource;
        this.name = name;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getName() {
        return name;
    }
}
