package com.mobilemakers.remindmetv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ShowAdapter extends ArrayAdapter<Show>{

    Context mContext;
    List<Show> mShows;

    public ShowAdapter(Context context,List<Show> shows) {
        super(context, R.layout.list_item_show, shows);
        mShows = shows;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = reuseOrGenerateRowView(convertView, parent);
        displayShowInRow(position, rowView);
        return rowView;
    }

    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if (convertView != null) {
            rowView = convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_show, parent, false);
        }
        return rowView;
    }

    private void displayShowInRow(int position, View rowView) {
        if (rowView != null){
            if (position%2 == 0) {
                rowView.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light127));
            }else {
                rowView.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light192));
            }
            TextView textViewName = (TextView)rowView.findViewById(R.id.text_view_show_name);
            TextView textViewChannel = (TextView)rowView.findViewById(R.id.text_view_show_channel);

            textViewName.setText(mShows.get(position).getName());
            textViewChannel.setText(mShows.get(position).getChannel());
        }
    }
}
