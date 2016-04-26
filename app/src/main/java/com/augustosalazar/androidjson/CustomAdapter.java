package com.augustosalazar.androidjson;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private List<DataEntry> listEntries;

    public CustomAdapter(Context context, List<DataEntry> listEntries) {
        this.context = context;
        this.listEntries = listEntries;
    }

    @Override
    public int getCount() {
        return listEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return listEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataEntry entry = listEntries.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, null);
        }

        TextView f1 = (TextView) convertView.findViewById(R.id.tvField1);
        TextView f2 = (TextView) convertView.findViewById(R.id.tvField2);

        f1.setText(String.valueOf(entry.getFistName()));
        f2.setText(String.valueOf(entry.getLastName()));

        convertView.setTag(entry);

        return convertView;
    }

    @Override
    public void onClick(View v) {
    }
}
