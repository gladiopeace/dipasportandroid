package com.dipacommerce.dipasport.barcode;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.utils.DateTimeUtils;

public class HistoryAdapter extends ArrayAdapter<Bundle> {

    private List<Bundle> mObjects;

    static class Holder {
        public TextView Code;
        public TextView Name;
        public TextView Time;
    }

    public HistoryAdapter(Context context, List<Bundle> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ctrl_history, parent, false);

            holder = new Holder();
            holder.Code = (TextView) convertView.findViewById(R.id.history_code);
            holder.Name = (TextView) convertView.findViewById(R.id.history_name);
            holder.Time = (TextView) convertView.findViewById(R.id.history_time);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Bundle bundle = mObjects.get(position);

        holder.Code.setText(bundle.getString(IProduct.HISTORY.CODE));
        holder.Name.setText(bundle.getString(IProduct.HISTORY.NAME));
        String datetime = DateTimeUtils.ConvertToFullDateTime(bundle.getLong(IProduct.HISTORY.TIME), DateTimeUtils.DATE_TIME_PATTERN);
        holder.Time.setText(datetime);

        return convertView;
    }
    
    public void refresh(){
        mObjects.clear();
        notifyDataSetChanged();
    }
}
