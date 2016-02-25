package com.ubi.jason.sensorcollect.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubi.jason.sensorcollect.R;

import java.util.ArrayList;

public class CustomListAdapterDrawer extends BaseAdapter {

    private static final String TAG = "ADAPTER_DRAWER";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static LayoutInflater inflater;
    private static Context context;
    private static String nav_titles[];
    private static TypedArray nav_icons;
    private ViewHolder holder;
    private int viewType;
    private ArrayList<String> drawerValues;

    public CustomListAdapterDrawer(Context context, String[] nav_titles, ArrayList drawerValues, TypedArray nav_icons) {
        this.context = context;
        this.nav_titles = nav_titles;
        this.nav_icons = nav_icons;
        this.drawerValues = drawerValues;
    }

    @Override
    public int getCount() {
        return nav_titles.length + 1;
    }

    @Override
    public String getItem(int position) {
        return nav_titles[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {
            convertView = inflater.inflate(R.layout.drawer_list_header, null);
            holder = new ViewHolder(convertView, viewType);
        } else {
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
            holder = new ViewHolder(convertView, viewType);
        }
        convertView.setTag(holder);
        if (viewType == TYPE_ITEM) {
            String v = drawerValues.get(position - 1);
            if (!v.equals("")) {
                holder.drawer_value.setText(v);
            } else {
                holder.drawer_value.setText("Por definir");
            }
            holder.drawer_option.setText(nav_titles[position - 1]+":");
            holder.drawer_icon.setImageResource(nav_icons.getResourceId(position - 1, -1));
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    static class ViewHolder {
        TextView drawer_option, drawer_value;
        ImageView drawer_icon;

        public ViewHolder(View convertView, int viewType) {
            if (viewType == TYPE_ITEM) {
                drawer_option = (TextView) convertView.findViewById(R.id.tvOption);
                drawer_value = (TextView) convertView.findViewById(R.id.tvValue);
                drawer_icon = (ImageView) convertView.findViewById(R.id.ivDrawerIcon);
            }
        }
    }
}
