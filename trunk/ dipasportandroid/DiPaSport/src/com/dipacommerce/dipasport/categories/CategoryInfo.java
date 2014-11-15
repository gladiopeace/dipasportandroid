package com.dipacommerce.dipasport.categories;

import java.util.ArrayList;

public class CategoryInfo {
    private String title;
    private String id;

    private ArrayList<CategoryInfo> submenu;

    public CategoryInfo() {
    }

    public ArrayList<CategoryInfo> getSubmenu() {
        return submenu;
    }

    public void setSubmenu(ArrayList<CategoryInfo> submenu) {
        this.submenu = submenu;
    }

    public CategoryInfo(String name, String id) {
        this.title = name;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title.trim();
    }
}
