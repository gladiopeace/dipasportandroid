package com.dipacommerce.dipasport.categories;

public abstract class Categories {
    private static CategoryImp sCategory;

    public static CategoryManager getInstance() {
        if (sCategory == null) {
            sCategory = new CategoryImp();
        }
        return sCategory;
    }
}
