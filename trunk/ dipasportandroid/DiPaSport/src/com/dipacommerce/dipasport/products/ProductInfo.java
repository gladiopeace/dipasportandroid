package com.dipacommerce.dipasport.products;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.shoppingcart.ICart;

public class ProductInfo extends DiPaSport<Object, Object> {
    String codicedipa;
    String name;
    String code;
    String price;
    long priceTotal = -1;
    String characteristics;
    String imagePath;
    String entiryID;
    Bitmap image;
    String description;
    String shortDescription;

    String code_oem;
    String availability;
    String quickOverview;
    String suitablefor;
    String partCondition;
    String suggestPrice = "";
    JSONObject rawJSON;
    ICart mShoppingCart;

    ArrayList<String> media_image;

    public ProductInfo() {

    }

    // this constructor support shopping cart database
    public ProductInfo(String code, String name, long price, String imagePath) {
        this.name = code;
        this.codicedipa = name;
        this.price = String.valueOf(price);
        this.imagePath = imagePath;
    }

    public ArrayList<String> getMedia_image() {
        return media_image;
    }

    public void setMediaImage(ArrayList<String> media_image) {
        this.media_image = media_image;
    }

    public Spanned getPartConditionWithFormatter() {
        String rawStr = String.format("<b>%s:</b> %s", sCtx.getString(R.string.str_detail_partcondition), partCondition);
        return Html.fromHtml(rawStr);
    }

    public void setPartCondition(String partCondition) {
        this.partCondition = partCondition;
    }

    public String getQuickoverview() {
        return quickOverview;
    }

    public Spanned getQuickoverviewWithFormatter() {
        String rawStr = String.format("<b>%s:</b> %s", sCtx.getString(R.string.str_detail_quick_overview), quickOverview);
        return Html.fromHtml(rawStr);
    }

    public void setQuickoverview(String quickoverview) {
        this.quickOverview = quickoverview;
    }

    public String getSuitablefor() {
        return suitablefor;
    }

    public Spanned getSuitableforWithFormatter() {
        String rawStr = String.format("<b>%s:</b> %s", sCtx.getString(R.string.str_detail_suitablefor), suitablefor);
        return Html.fromHtml(rawStr);
    }

    public void setSuitablefor(String suitablefor) {
        this.suitablefor = suitablefor;
    }

    public long getPriceNumber() {
        String[] tmp = splitPrice();
        if (tmp[0].length() > 0) {
            priceTotal = Integer.parseInt(tmp[0]);
        }
        return priceTotal;
    }

    private String[] splitPrice() {
        String[] tmp = new String[] { "", "" };
        if (price != null) {
            tmp = price.split("\\.");
        }
        return tmp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getEntityID() {
        return entiryID;
    }

    public void setEntiryID(String entiryID) {
        this.entiryID = entiryID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return codicedipa;
    }

    public void setName(String name) {
        this.codicedipa = name;
    }

    public String getCode() {
        return name;
    }

    public String getCodeWithFormatter() {
        if (getCode() != null) {
            int str_id = R.string.str_product_code;
            return sCtx.getString(str_id).toUpperCase(Locale.getDefault()) + " " + getCode();
        } else {
            return "";
        }
    }

    public void setCode(String name) {
        this.name = name;
    }

    public String getCode_oem() {
        return code_oem;
    }

    public Spanned getCode_oem_withFormatter() {
        int str_id = R.string.str_product_code_oem;
        String rawStr = String.format("<b>%s</b> %s", sCtx.getString(str_id).toUpperCase(Locale.getDefault()), code_oem);
        return Html.fromHtml(rawStr);
    }

    public void setCode_oem(String code_oem) {
        this.code_oem = code_oem;
    }

    public String getPrice() {
        return splitPrice()[0] + Constants.DOUBLE_ZERO;
    }

    public String getPriceWithFormatter() {
        return String.format("%s %s%s %s", sCtx.getString(R.string.str_product_price_prefix), sCtx.getString(R.string.euro),
                getPrice(), sCtx.getString(R.string.str_product_price_suffix));
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getAvailability() {
        return availability;
    }

    public Spanned getAvailabilityWithFormatter() {
        String rawStr = String.format("<b>%s:</b> %s", sCtx.getString(R.string.str_detail_availability), availability);
        return Html.fromHtml(rawStr);
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void addSuggestPrice(String price) {
        String rawString = String.format(Locale.getDefault(), "<br/>%s", price);
        suggestPrice += rawString;
    }

    public String getSuggestPrice() {
        return suggestPrice;
    }
    
    public boolean clearSuggestPrice(){
        suggestPrice = "";
        return TextUtils.isEmpty(suggestPrice);
    }

    public void setRawJSON(JSONObject rawJSON) {
        this.rawJSON = rawJSON;
    }

    public JSONObject getRawJSON() {
        return rawJSON;
    }
}
