package com.dipacommerce.dipasport.shoppingcart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerGroup;
import com.dipacommerce.dipasport.customer.CustomerManager;
import com.dipacommerce.dipasport.customer.Customers;
import com.dipacommerce.dipasport.products.ProductInfo;

public final class AddToCartButton extends LinearLayout {

    private LayoutInflater mInflater = null;
    private Button mButtonAddToCart;

    private ICart mShoppingCart;
    private ProductInfo mProductOrder;
    private int mProductQuantity = 1;

    private CustomerManager mCustomer;

    public AddToCartButton(Context context) {
        super(context);
        initViews();
    }

    public AddToCartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public AddToCartButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public void setProduct(final ProductInfo productOrder) {
        mProductOrder = productOrder;
    }

    private void initViews() {
        mCustomer = Customers.getInstance();
        mShoppingCart = ShoppingCart.getInstance();
        
        if (mInflater == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.ctrl_add_to_cart_button, this, true);
        }

        mButtonAddToCart = (Button) findViewById(R.id.add_to_cart);
        if (mButtonAddToCart != null) {
            update();
        }
    }

    public void setAddToCartEvent() {
        mButtonAddToCart.setOnClickListener(mAddToCartClick);
    }

    public void unSetAddToCartEvent() {
        mButtonAddToCart.setOnClickListener(null);
    }

    private OnClickListener mAddToCartClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mShoppingCart.isExist(mProductOrder.getCode())) {
                mShoppingCart.chooseQuantityAdd(mProductOrder, mProductQuantity);
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.str_product_existing_incart), Toast.LENGTH_LONG).show();
            }
        }
    };
    
    public void update(){
        boolean g0 = mCustomer.getCustomerInfo().getGroupId() == CustomerGroup.NOT_LOGGED_IN;
        boolean g16 = mCustomer.getCustomerInfo().getGroupId() == CustomerGroup.NOPREZZI;
        boolean g17 = mCustomer.getCustomerInfo().getGroupId() == CustomerGroup.BANNATI;
        
        if (!mCustomer.isLogin() || (g0 || g16 || g17)) {
            mButtonAddToCart.setVisibility(View.INVISIBLE);
        } else {
            mButtonAddToCart.setVisibility(View.VISIBLE);
            setAddToCartEvent();
        }
    }

}

