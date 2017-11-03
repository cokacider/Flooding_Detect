package com.example.heejun.flooding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HEEJUN on 2017-01-10.
 */

public class ListViewAdapter extends BaseAdapter {
    private final String FINE = "양호";
    private final String WARNING = "경고";
    private final String DANGER = "위험";

    private ArrayList<SensorListItem> listViewItemList = new ArrayList<>();

    public ListViewAdapter() {}

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sensorlist_item, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.list_item_name);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.list_item_location);
        TextView serialNumberTextView = (TextView) convertView.findViewById(R.id.list_item_serialNumber);
        TextView conditionTextView = (TextView) convertView.findViewById(R.id.list_item_conditionText);
        ImageView conditionImageView = (ImageView) convertView.findViewById(R.id.list_item_condition);

        SensorListItem sensorListItem = listViewItemList.get(position);

        nameTextView.setText(sensorListItem.getName());
        locationTextView.setText(sensorListItem.getLocation());
        serialNumberTextView.setText(sensorListItem.getSerialNumber());

        if("1".equals(sensorListItem.getCondition())){
            conditionTextView.setText(FINE);
            conditionImageView.setImageResource(R.drawable.ovalgreen);
        }
        else if("2".equals(sensorListItem.getCondition())){
            conditionTextView.setText(WARNING);
            conditionImageView.setImageResource(R.drawable.ovalyellow);
        }
        else if("3".equals(sensorListItem.getCondition())){
            conditionTextView.setText(DANGER);
            conditionImageView.setImageResource(R.drawable.ovalred);
        }


        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String name, String location, String serialNumber, String condition) {
        SensorListItem item = new SensorListItem();

        item.setName(name);
        item.setLocation(location);
        item.setSerialNumber(serialNumber);
        item.setCondition(condition);

        listViewItemList.add(item);
    }

    public ArrayList<SensorListItem> getListViewItemList() {
        return listViewItemList;
    }

    public void deleteItem(int index) {
        if(listViewItemList.size() > index)
            listViewItemList.remove(index);
    }
    public void clearItem() {
        listViewItemList.clear();
    }
}
