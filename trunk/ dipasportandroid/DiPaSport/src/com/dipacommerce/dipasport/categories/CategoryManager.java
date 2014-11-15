package com.dipacommerce.dipasport.categories;

import java.util.List;

import org.json.JSONObject;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

public abstract class CategoryManager extends DiPaSport<JSONObject, List<CategoryInfo>> implements ICategory {

    /**
     * @param callback
     */
    public abstract void registerCallbackCategories(final Callback<JSONObject> callback);

    /**
     * @param category
     */
    public abstract void setCategorySelected(CategoryInfo category);

    /**
     * @return
     */
    public abstract CategoryInfo getCategorySelected();

}
