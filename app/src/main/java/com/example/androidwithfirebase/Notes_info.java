package com.example.androidwithfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Notes_info extends BaseAdapter {
    Context cntxt;
    String[] Title, Desc;
    LayoutInflater lf;

    public Notes_info(String[] Title, String[] Desc, Context cntxt) {
        this.Desc = Desc;
        this.Title = Title;
        this.cntxt = cntxt;
        lf = LayoutInflater.from(cntxt);
    }

    public void updateData(String[] newTitleArray, String[] newDescriptionArray) {
        Title = newTitleArray;
        Desc = newDescriptionArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Title.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = lf.inflate(R.layout.notes_acc, null);

        TextView Titlee = view.findViewById(R.id.title);
        TextView Descc = view.findViewById(R.id.desc);

        Titlee.setText(Title[i]);
        Descc.setText(Desc[i]);

        return view;
    }
}
