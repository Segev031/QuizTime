package com.Segev.QuizTime;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class catGridAdapter extends BaseAdapter {

    private static List<String> catList;

   public catGridAdapter(List<String> catList) {
        this.catList = catList;
    }

    @Override
    public int getCount() {
        return catList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        View view;

        if(convertView == null)
        {
            // design a layout for all the categories
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent,false);
        }
        else
        {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent
                Intent intent = new Intent(parent.getContext(), SetsActivity.class);
                // put extra data in the intent of the category name and his ID
                intent.putExtra("CATEGORY", catList.get(position));
                intent.putExtra("CATEGORY_ID", position+ 1);
                // move to the next activity
                parent.getContext().startActivity(intent);
            }
        });

        // set the name for each category
        ((TextView) view.findViewById(R.id.catName)).setText(catList.get(position));
        // set images for each category
        int[] mar=new int[catList.size()];
        mar[0]=R.drawable.music_cat1;
        mar[1]=R.drawable.math_cat;
        mar[2]=R.drawable.animals_cat1;
        mar[3]=R.drawable.sports_cat1;
        mar[4]=R.drawable.food_cat;
        mar[5]=R.drawable.israel_cat;
        view.setBackgroundResource(mar[position]);
        return view;
    }
}
