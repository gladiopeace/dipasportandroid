package com.dipacommerce.dipasport.categories;

import org.json.JSONObject;

/**
 * 
 *
 * @param <T>
 *            extends {@link JSONObject}
 * @param <V>
 *            return value
 */
public interface ICategory  {

    public class JSON_TAG {
        public static final String Categories = "categories";

        public static final String Data = "DATA";
        public static final String Value = "value";
        public static final String EntityID = "entity_id";

        public static final String ID = "id";
        public static final String Title = "title";
        public static final String SubMenu = "submenu";

    }

    /**
     * 
     * @return a list categories
     */
    public void fetchAll();
}
