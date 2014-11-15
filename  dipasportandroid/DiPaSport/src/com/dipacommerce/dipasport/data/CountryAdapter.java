package com.dipacommerce.dipasport.data;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CountryAdapter extends BaseAdapter {

    private Map<String, String> mDataSource;
    private Context mContext;
    
    
    public CountryAdapter(Context context, Map<String, String> datasource) {
        mContext = context;
        mDataSource = datasource;
    }
    
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int arg0) {
        return getKey(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View container, ViewGroup parent) {
        
        if(container == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            container = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        
        TextView label = (TextView)container.findViewById(android.R.id.text1);
        if(label != null){
            String country = getKey(position);
            String country_id = getValue(position);
            label.setText(country_id);
            label.setTag(country);
        }
        
        return container;
    }
    
    private String getKey(int position){
        int i = 0;
        for(String key : mDataSource.keySet()){
            if(i == position){
                return key;
            }
            i++;
        }
        return "";
    }
    
    private String getValue(int position){
        int i = 0;
        for(String value : mDataSource.values()){
            if(i == position){
                return value;
            }
            i++;
        }
        return "";
    }
    
}
