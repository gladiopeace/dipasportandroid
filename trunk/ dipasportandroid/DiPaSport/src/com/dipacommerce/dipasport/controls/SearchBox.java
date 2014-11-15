package com.dipacommerce.dipasport.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;

public class SearchBox extends LinearLayout {

    private LayoutInflater mInflater = null;
    private EditText mSearchContent;
    private ImageButton mSearchButton;

    private ISearch mSearchCallback = null;

    /**
     * Implement this interface on inherited classes when you need catch event
     * of the search button pressed in the search box.
     * 
     */
    public interface ISearch {
        public void onSearch(CharSequence searchContent);

        public void onScanBarCode();
    }

    public SearchBox(Context context) {
        super(context);
        initViews();
    }

    public SearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    /**
     * 
     */
    private void initViews() {
        if (mInflater == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.ctrl_search_box, this, true);

            mSearchContent = (EditText) findViewById(R.id.search_content);
            mSearchButton = (ImageButton) findViewById(R.id.search_button);

            mSearchButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DiPaSport.hideKeyboard();
                    if (mSearchCallback != null) {
                        String keyword = mSearchContent.getText().toString();
                        if (keyword.length() == 0) {
                            Toast.makeText(getContext(), getContext().getString(R.string.str_warning_search_keyword_empty), Toast.LENGTH_LONG).show();
                            return; // don't search
                        }
                        mSearchCallback.onSearch(mSearchContent.getText().toString());
                    }
                }
            });

            mSearchContent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // remove search by barcode
                    // showSearchMethods();
                }
            });

            mSearchContent.setOnEditorActionListener(new OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mSearchButton.performClick();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 
     * @param searchCallback
     */
    public void setCallbacks(ISearch searchCallback) {
        this.mSearchCallback = searchCallback;
    }

    /**
     * 
     * @return
     */
    public void setContent(CharSequence content) {
        mSearchContent.setText(content);
    }

    /**
     * 
     * @return
     */
    public CharSequence getContent() {
        return mSearchContent.getText();
    }
}
