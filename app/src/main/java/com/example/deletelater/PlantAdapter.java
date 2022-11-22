package com.example.deletelater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlantAdapter extends BaseAdapter {
    ArrayList<Plant> items = new ArrayList<Plant>();
    Context context;
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        Plant plant = items.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview,parent,false);
        }

        TextView number = convertView.findViewById(R.id.num);
        TextView nameText = convertView.findViewById(R.id.name);
        ImageView imgview = convertView.findViewById(R.id.imgsrc);

        Plant item = items.get(position);
        number.setText(String.valueOf(position+1));
        nameText.setText(plant.getName());
        imgview.setImageBitmap(plant.getImgsrc());

        return convertView;
    }
    public void addItem(Plant item){
        items.add(item);
    }


}