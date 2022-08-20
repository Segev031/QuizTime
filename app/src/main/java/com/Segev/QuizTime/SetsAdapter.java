package com.Segev.QuizTime;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SetsAdapter extends BaseAdapter {

    private int numOfSets;

    public SetsAdapter(int numOfSets) {
        this.numOfSets = numOfSets;
    }

    @Override
    public int getCount() {
        return numOfSets;
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
            // design a layout for all the sets
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item_layout,parent,false);
        }
        else
        {
            view = convertView;
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent
                Intent intent = new Intent(parent.getContext(), QuestionActivity.class);
                // put extra data in the intent for the set number
                intent.putExtra("SETNO", position + 1);
                // move to the next activity
                parent.getContext().startActivity(intent);
            }
        });

        // set the number for each set
        ((TextView) view.findViewById(R.id.setNo_tv)).setText(String.valueOf(position+1));

        return view;
    }
}
