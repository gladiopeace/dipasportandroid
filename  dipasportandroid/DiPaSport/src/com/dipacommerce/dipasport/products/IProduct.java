package com.dipacommerce.dipasport.products;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dipacommerce.dipasport.data.SearchResultAdapter;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.shoppingcart.AddToCartButton;

public interface IProduct<T> {

    public class JSON_TAG {
        public static final String DATA = "DATA";
        public static final String Name = "name";
        public static final String Image = "image";
        public static final String EntityID = "entity_id";
    }

    public class JSON_PRODUCT_DETAIL_TAG {
        // public static final String Sku = "sku";
        public static final String Codicedipa = "codicedipa"; // product name
                                                              // here
        public static final String Name = "name";
        public static final String Image = "image";
        public static final String SmallImage = "small_image";
        public static final String Thumbnail = "thumbnail";
        public static final String SpecialPrice = "special_price";
        public static final String Description = "description"; // don't used
        public static final String ShortDescription = "short_description"; // don't
                                                                           // used
        public static final String Status = "status";
        public static final String Images = "media_image";
        public static final String PartCondition = "condizionedelpezzo";
        public static final String CodeOem = "codicioem";
        public static final String Availability_note = "note";
        public static final String QuickOverview = "description";
        public static final String Suitablefor = "applicazione";
        public static final String PricePerCustomer = "prices_per_customer";
        public static final String UnAvailable = "disponibilenondisponibile";
        public static final String Available = "disponibile";

        // Price by customer group
        public class PRICE {
            public static final String NORMALE = "price";
            public static final String OFFICINA = "msrp";
            public static final String RICAMBISTA = "prezzoconsrivenditori";
            public static final String RICAMBISTA_A = RICAMBISTA;
            public static final String GROSSISTA = "prezzoconsgrossisti";
            public static final String GROSSISTA_A = GROSSISTA;
            public static final String OFF_S = "tier_price";
            public static final String PURCHASE_PRICE = "prezzodiacquisto";
            public static final String COMPETITIVE_PRICE = "prezziconcorrenti";
        }
    }

    /**
     * Used by {@link SearchResultAdapter}
     * 
     */
    public class ProductHolder {
        public ImageView mProductImage;
        public TextView mProductName;
        public TextView mProductCode;
        public TextView mProductPrice;
        public AddToCartButton mAddToCart;
        public Button mOpenProduct;
    }

    public class OrderProductHolder {
        public ImageView mProductImage;
        public TextView mProductName;
        public TextView mProductCode;
        public TextView mProductPrice;
        public TextView mProductQuantity;
        public Button mRemoveProduct;
    }

    public class BUNDLE {
        public static final String PRODUCT_IMAGE_PATH = "PRODUCT_IMAGE_PATH";
        public static final String PRODUCT_CODE = "PRODUCT_CODE";
        public static final String PRODUCT_NAME = "PRODUCT_NAME";
        public static final String PRODUCT_ID = "ID";
    }

    /**
     * Support barcode history
     *
     */
    public class HISTORY {
        public static final String ROWID = "ROWID";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String TIME = "time";
    }

    public static final int QUANTITY_MIN = 1;
    public static final int QUANTITY_MAX = 50;

    public void fetch(final String productCode);

    public void fetchProductFeatures();

    @Deprecated
    public void filter(final String productCode, final String categoryId, final String idUser);

    public void filter(final String productCode, final int page, final String idUser);

    public void filterByCategory(final String categoryId, final int page, final String idUser);

    public void registerCallbackProductsFeature(final Callback<T> callback);

    public void registerCallbackProductDetaill(final Callback<T> callback);

    public void registerCallbackSearchResult(final Callback<T> callback);

    public void registerCallbackSearchByCategory(final Callback<T> callback);

    public ProductInfo convertJSONProductDetail(final T jsonObject);
}
