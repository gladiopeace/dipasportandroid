package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.products.IProduct.OrderProductHolder;

public class ShoppingCartAdapter<T> extends BaseAdapter {

    protected static Context sContext;
    protected int mLayoutId;
    protected ArrayList<T> mDataSource = null;
    protected IShop<T> mShoppingCart = null;
    protected ImageLoader<ImageView> mImageLoader;

    @SuppressWarnings("unchecked")
    public ShoppingCartAdapter(final Context context, final int layoutID, ArrayList<T> dataSource) {
        sContext = context;
        mLayoutId = layoutID;
        mShoppingCart = (IShop<T>) ShoppingCart.getInstance();
        mDataSource = dataSource;
        
        mImageLoader = new ImageLoader<ImageView>(sContext);
    }

    @Override
    public int getCount() {
        return (mDataSource != null ? mDataSource.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return (mDataSource != null ? mDataSource.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderProductHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayoutId, parent, false);

            holder = new OrderProductHolder();

            holder.mProductImage = (ImageView) convertView.findViewById(R.id.cart_product_image);
            holder.mProductName = (TextView) convertView.findViewById(R.id.cart_product_name);
            holder.mProductCode = (TextView) convertView.findViewById(R.id.cart_product_code);
            holder.mProductPrice = (TextView) convertView.findViewById(R.id.cart_product_price);

            holder.mProductQuantity = (TextView) convertView.findViewById(R.id.cart_product_quantity);
            holder.mProductQuantity.setOnClickListener(quantityChanged);

            holder.mRemoveProduct = (Button) convertView.findViewById(R.id.cart_product_remove);
            holder.mRemoveProduct.setOnClickListener(removeProductClick);

            convertView.setTag(holder);
        } else {
            holder = (OrderProductHolder) convertView.getTag();
        }

        OrderProduct orderProduct = (OrderProduct)mDataSource.get(position);

        mImageLoader.DisplayImage(orderProduct.getProductInfo().getImagePath(), 0, holder.mProductImage);
        holder.mProductName.setText(orderProduct.getProductInfo().getCode());
        holder.mProductCode.setText(orderProduct.getProductInfo().getCodeWithFormatter());
        holder.mProductPrice.setText(orderProduct.getProductInfo().getPriceWithFormatter());
        holder.mProductQuantity.setText(orderProduct.getQuantity() + "");
        holder.mProductQuantity.setTag(orderProduct.getProductInfo().getCode());
        holder.mRemoveProduct.setTag(orderProduct.getProductInfo().getCode());

        return convertView;
    }

    /**
     * Remove product from cart
     */
    private OnClickListener removeProductClick = new OnClickListener() {

        @Override
        public void onClick(final View v) {
            if (v.getTag() != null) {
                String productCode = v.getTag().toString();
                mShoppingCart.removeProductInterface(productCode);
            }
        }
    };

    /**
     * Change quantity of product
     */
    private OnClickListener quantityChanged = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String productCode = (String) v.getTag();
            if (productCode != null) {
                int currentQuantity = Integer.parseInt(((TextView) v).getText().toString());
                mShoppingCart.chooseQuantityUpdate(productCode, currentQuantity);
            }
        }
    };

    public void refreshData(ArrayList<T> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }

    public void clearData() {
        mDataSource.clear();
        notifyDataSetChanged();
    }

}
