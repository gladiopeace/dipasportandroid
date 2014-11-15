package com.dipacommerce.dipasport.data;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerGroup;
import com.dipacommerce.dipasport.customer.CustomerManager;
import com.dipacommerce.dipasport.customer.Customers;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.products.IProduct.ProductHolder;
import com.dipacommerce.dipasport.shoppingcart.AddToCartButton;
import com.dipacommerce.dipasport.views.FragmentProductViews;

public class SearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayoutId;
    private ArrayList<ProductInfo> mDataSource;
    private static CustomerManager mCustomer;
    private int mTabId = R.id.tab_search; // as default
    private int mParentTabId = 0; // search tab as default

    // image downloader here
    private ImageLoader<ImageView> mImageLoader;

    private SearchResultAdapter(Context context, int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mCustomer = Customers.getInstance();
        mImageLoader = new ImageLoader<ImageView>(mContext);
    }

    @SuppressWarnings("unused")
    private SearchResultAdapter(Context context, int layoutId, ArrayList<ProductInfo> datasource) {
        this(context, layoutId);
        mDataSource = excludeBOProduct(datasource);
    }

    public SearchResultAdapter(Context context, int layoutId, ArrayList<ProductInfo> datasource, int tabId, int parentTabId) {
        this(context, layoutId);
        mDataSource = excludeBOProduct(datasource);
        mTabId = tabId;
        mParentTabId = parentTabId;
    }

    @Override
    public int getCount() {
        return (mDataSource != null) ? mDataSource.size() : -1;
    }

    @Override
    public Object getItem(int position) {
        return (mDataSource != null) ? mDataSource.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayoutId, parent, false);

            holder = new ProductHolder();

            holder.mProductImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.mProductName = (TextView) convertView.findViewById(R.id.product_name);
            holder.mProductCode = (TextView) convertView.findViewById(R.id.product_code);
            holder.mProductPrice = (TextView) convertView.findViewById(R.id.product_price);

            AddToCartButton addtocart = (AddToCartButton) convertView.findViewById(R.id.product_add_to_cart);
            Button openproduct = (Button) convertView.findViewById(R.id.product_open);
            if (!mCustomer.isLogin()) {
                // don't show 'price' and 'add to cart' button if customer is
                // not login
                addtocart.setVisibility(View.INVISIBLE);
                holder.mProductPrice.setVisibility(View.INVISIBLE);
            }
            holder.mAddToCart = addtocart;
            holder.mOpenProduct = openproduct;

            holder.mAddToCart.setOnClickListener(AddToCart);
            holder.mOpenProduct.setOnClickListener(OpenProductView);
            holder.mProductImage.setOnClickListener(OpenProductView);
            convertView.setTag(holder);
        } else {
            holder = (ProductHolder) convertView.getTag();
        }

        // Set data to view here
        ProductInfo product = mDataSource.get(position);

        mImageLoader.DisplayImage(product.getImagePath(), 0, holder.mProductImage);
        holder.mProductName.setText(product.getName());
        if (product.getCodeWithFormatter().length() != 0) {
            holder.mProductCode.setText(product.getCodeWithFormatter());
        } else {
            holder.mProductCode.setVisibility(View.GONE);
        }
        if (mCustomer.isLogin()) {
            if (mCustomer.getCustomerInfo().getGroupId() == CustomerGroup.NOPREZZI) {
                holder.mProductPrice.setText(mContext.getResources().getString(R.string.str_detail_price_unavailable));
            } else if(mCustomer.getCustomerInfo().getGroupId() == CustomerGroup.BANNATI){
                holder.mProductPrice.setText(mContext.getResources().getString(R.string.str_customer_banned));
            }else {
                holder.mProductPrice.setText(product.getPriceWithFormatter());
            }
            holder.mProductPrice.setVisibility(View.VISIBLE);
        } else {
            holder.mProductPrice.setVisibility(View.GONE);
        }
        //holder.mProductPrice.setText(product.getPriceWithFormatter());

        holder.mOpenProduct.setTag(product);
        holder.mProductImage.setTag(product);
        holder.mAddToCart.setProduct(product);

        return convertView;
    }

    private OnClickListener AddToCart = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // Add this product to cart
            Toast.makeText(mContext, "add to cart", Toast.LENGTH_LONG).show();

        }
    };

    private OnClickListener OpenProductView = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ProductInfo product = (ProductInfo) v.getTag();
            String entity_id = product.getEntityID();
            String name = product.getName();

            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(IProduct.BUNDLE.PRODUCT_ID, entity_id);
            bundle.putString(IProduct.BUNDLE.PRODUCT_NAME, name);
            bundle.putInt("tabid", mParentTabId);
            FragmentProductViews f = new FragmentProductViews();
            f.setArguments(bundle);
            FragmentTransaction ta = fm.beginTransaction();
            ta.replace(mTabId, f);
            ta.commit();
        }
    };
    
    /**
     * Exclude the products code name begin with 'BO'
     * @param datasource
     * @return
     */
    private ArrayList<ProductInfo> excludeBOProduct(ArrayList<ProductInfo> datasource){
        ArrayList<ProductInfo> tmp = new ArrayList<ProductInfo>();
        for (ProductInfo productInfo : datasource) {
            if(productInfo.getCode().indexOf("BO") == -1){
                tmp.add(productInfo);
            }
        }
        return tmp;
    }
    

}
