package com.dipacommerce.dipasport.categories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.JSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

class CategoryImp extends CategoryManager {

    private Callback<JSONObject> mCallbackCategories;

    private CategoryInfo mCategorySelected;

    /**
     * 
     * @param context
     */
    public CategoryImp() {
        if (sJSONManager == null) {
            sJSONManager = JSONManager.getNewInstance(sCtx);
        }
    }

    @Override
    public void fetchAll() {
        if (mCallbackCategories != null) {
            sJSONManager.registerCallback(mCallbackCategories);
        }
        sJSONManager.setURL(URLFormatter.buildUrlCategories());
        sJSONManager.execute();
    }

    @Override
    public void registerCallbackCategories(Callback<JSONObject> callback) {
        mCallbackCategories = callback;
    }

    @Deprecated
    public List<CategoryInfo> convertJSONToList(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        List<CategoryInfo> listCategories = new ArrayList<CategoryInfo>();
        try {
            JSONArray categories;
            CategoryInfo catInfo;

            if (getStatus(jsonObject) == CODE.OK) {
                categories = jsonObject.getJSONArray(ICategory.JSON_TAG.Data);

                for (int i = 0; i < categories.length(); i++) {
                    catInfo = new CategoryInfo();
                    String cat_name = categories.getJSONObject(i).getString(ICategory.JSON_TAG.Value);
                    String cat_id = categories.getJSONObject(i).getString(ICategory.JSON_TAG.EntityID);

                    catInfo.setTitle(cat_name);
                    catInfo.setId(cat_id);

                    listCategories.add(catInfo);
                }

            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return listCategories;
    }

    @Override
    public void setCategorySelected(CategoryInfo category) {
        mCategorySelected = category;
    }

    @Override
    public CategoryInfo getCategorySelected() {
        return mCategorySelected;
    }

    @Override
    public List<CategoryInfo> getData(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        List<CategoryInfo> listCategories = new ArrayList<CategoryInfo>();
        try {
            JSONArray categories;
            CategoryInfo catInfo;
            ArrayList<CategoryInfo> listSubMenu;

            if (getStatus(jsonObject) == CODE.ERROR) {
                return null;
            }

            categories = jsonObject.getJSONArray(ICategory.JSON_TAG.Data);

            for (int i = 0; i < categories.length(); i++) {
                // Search in sub menu
                int id = categories.getJSONObject(i).getInt(ICategory.JSON_TAG.ID);
                if(id != 29 && id != 50){ continue; }
                    
                Object obj = categories.getJSONObject(i).get(ICategory.JSON_TAG.SubMenu);
                if (!obj.equals(null)) {

                    catInfo = new CategoryInfo();
                    String title = categories.getJSONObject(i).getString(ICategory.JSON_TAG.Title);
                    catInfo.setId(id + "");
                    catInfo.setTitle(title);

                    JSONArray submenu = (JSONArray) obj;
                    listSubMenu = new ArrayList<CategoryInfo>();
                    CategoryInfo subCat;
                    for (int j = 0; j < submenu.length(); j++) {
                        subCat = new CategoryInfo();
                        String subId = submenu.getJSONObject(j).getString(ICategory.JSON_TAG.ID);
                        String subTitle = submenu.getJSONObject(j).getString(ICategory.JSON_TAG.Title);
                        if(subId.equals("430") || subId.equals("418") || subId.equals("22")){
                            continue;
                        }
                        subCat.setId(subId);
                        subCat.setTitle(subTitle);

                        listSubMenu.add(subCat);
                    }

                    catInfo.setSubmenu(listSubMenu);
                    listCategories.add(catInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return listCategories;
    }

}
