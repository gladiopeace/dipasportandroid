package com.dipacommerce.dipasport.products;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.IDiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerGroup;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.utils.URLFix;

public abstract class ProductManager extends DiPaSport<JSONObject, List<ProductInfo>> implements IProduct<JSONObject> {

    public void fetchAll() {

    }

    @Override
    public abstract void fetch(String productCode);

    @Override
    public abstract void fetchProductFeatures();

    @Override
    public abstract void filter(String productCode, String categoryId, String idUser);

    @Override
    public abstract void filterByCategory(String categoryId, int page, String idUser);

    @Override
    public abstract void registerCallbackProductsFeature(Callback<JSONObject> callback);

    @Override
    public abstract void registerCallbackProductDetaill(Callback<JSONObject> callback);

    @Override
    public abstract void registerCallbackSearchResult(Callback<JSONObject> callback);

    @Override
    public abstract void registerCallbackSearchByCategory(Callback<JSONObject> callback);

    @Override
    public List<ProductInfo> getData(JSONObject jsonObject) {
        List<ProductInfo> listProduct = new ArrayList<ProductInfo>();
        try {
            int errorCode = jsonObject.getInt(IDiPaSport.JSON_STATUS_TAG.ERROR_CODE);
            if (errorCode == 1) {
                return null;
            }

            JSONArray products = jsonObject.getJSONArray(IProduct.JSON_TAG.DATA);

            for (int i = 0; i < products.length(); i++) {
                ProductInfo product = new ProductInfo();
                String name = products.getJSONObject(i).getString(IProduct.JSON_TAG.Name);
                String imagePath = products.getJSONObject(i).getString(IProduct.JSON_TAG.Image);
                String entityId = products.getJSONObject(i).getString(IProduct.JSON_TAG.EntityID);
                product.setName(name);
                product.setImagePath(URLFix.Fix(imagePath));
                product.setEntiryID(entityId);
                listProduct.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return listProduct;
    }

    @Override
    public ProductInfo convertJSONProductDetail(JSONObject jsonObject) {
        int errorCode = 0;
        try {
            errorCode = jsonObject.getInt(IDiPaSport.JSON_STATUS_TAG.ERROR_CODE);
            if (errorCode == 1) {
                return null;
            }

            JSONObject data = jsonObject.getJSONObject(IProduct.JSON_TAG.DATA);

            // UPDATE customer group id
            String price_type = sCustomer.getPriceType(jsonObject);
            sCustomer.setJSONPrice(price_type);

            String gid = jsonObject.getString(CustomerInfo.JSON_TAG.GroupId);
            int groupId = Integer.parseInt(gid);
            sCustomer.updateCustomerInfo(groupId);

            ProductInfo productInfo = getProductData(data);

            String entity_id = data.getString(IProduct.JSON_TAG.EntityID);
            productInfo.setEntiryID(entity_id);
            String availability = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Availability_note);

            if (TextUtils.isEmpty(availability)) {
                String unavailable = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.UnAvailable);
                if (TextUtils.isEmpty(unavailable)) {
                    String available = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Available);
                    if (!TextUtils.isEmpty(available)) {
                        availability = String.format(sCtx.getResources().getString(R.string.str_detail_product_available), available);
                    }
                } else {
                    availability = String.format(sCtx.getResources().getString(R.string.str_detail_product_unavailable), unavailable);
                }
            }

            if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
                if (availability.toLowerCase(Locale.getDefault()).equals("disponibile")) {
                    availability = "available".toUpperCase(Locale.getDefault());
                }
            }

            String quickoverview = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.QuickOverview);
            String suitablefor = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Suitablefor);
            String partcondition = "";
            try {
                partcondition = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PartCondition);
            } catch (JSONException e) {
            }
            JSONArray media_image = data.getJSONArray(IProduct.JSON_PRODUCT_DETAIL_TAG.Images);
            ArrayList<String> imgPath = new ArrayList<String>();

            for (int i = 0; i < media_image.length(); i++) {
                String img = media_image.getString(i);
                imgPath.add(img);
            }

            productInfo.setAvailability(availability);
            productInfo.setQuickoverview(quickoverview);
            productInfo.setSuitablefor(suitablefor);
            productInfo.setPartCondition(partcondition);
            productInfo.setMediaImage(imgPath);

            return productInfo;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ProductInfo> convertJSONSearchResultToList(JSONObject jsonObject) {
        ArrayList<ProductInfo> list = new ArrayList<ProductInfo>();
        int errorCode = 0;
        try {
            errorCode = jsonObject.getInt(IDiPaSport.JSON_STATUS_TAG.ERROR_CODE);
            if (errorCode == 1) {
                return list; // empty array
            }

            JSONArray jsonArray = jsonObject.getJSONArray(IProduct.JSON_TAG.DATA);
            // UPDATE customer group id
            String price_type = sCustomer.getPriceType(jsonObject);
            sCustomer.setJSONPrice(price_type);

            String gid = jsonObject.getString(CustomerInfo.JSON_TAG.GroupId);
            int groupId = Integer.parseInt(gid);
            sCustomer.updateCustomerInfo(groupId);

            ProductInfo product;
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    product = getProductData(jsonArray.getJSONObject(i));
                    String entity_id = jsonArray.getJSONObject(i).getString(IProduct.JSON_TAG.EntityID);
                    product.setEntiryID(entity_id);

                    list.add(product);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * For details page
     * 
     * @param data
     * @return
     */
    private ProductInfo getProductData(JSONObject data) {
        ProductInfo productInfo = null;
        productInfo = new ProductInfo();

        String name = "";
        try {
            name = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Codicedipa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String code = "";
        try {
            code = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String codeOem = "";
        try {
            codeOem = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.CodeOem);
        } catch (JSONException e) {
            // e.printStackTrace(); ignore it
            // Log.e("codeoem", name);
        }

        String imagePath = "";
        try {
            imagePath = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.Image);
        } catch (JSONException e) {
            //
            e.printStackTrace();
        }

        String price = "";
        try {
            String jsontag = sCustomer.getJSONPrice();
            int group = sCustomer.getCustomerInfo().getGroupId();
            if (group == CustomerGroup.OFF_S) {
                if (sCustomer.getJSONPrice() == IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFF_S) {
                    JSONArray arrayPrices = data.getJSONArray(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFF_S);
                    if (arrayPrices.length() > 0) {
                        for (int i = 0; i < arrayPrices.length(); i++) {
                            int cus_group = Integer.parseInt(arrayPrices.getJSONObject(i).getString("cust_group"));
                            if (cus_group == group) {
                                price = String.valueOf(arrayPrices.getJSONObject(i).getInt("price"));
                            }
                        }
                    }
                }
            } else {
                price = data.getString(jsontag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String pricePerCustomer = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PricePerCustomer);
            if (!pricePerCustomer.equals("")) {
                price = pricePerCustomer;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set default price
        if (TextUtils.isEmpty(price)) {
            try {
                price = data.getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.NORMALE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        productInfo.setName(name);
        productInfo.setImagePath(imagePath);
        productInfo.setPrice(price);
        productInfo.setCode(code);
        productInfo.setCode_oem(codeOem);

        return productInfo;
    }
}
