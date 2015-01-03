package com.saarang.samples.apps.iosched.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.saarang.samples.apps.iosched.R;

import java.net.URL;

/**
 * Created by Arun Padiyan on 01-Jan-15.
 */
public class SponsorsList extends ArrayAdapter<String> {
private final Activity context;
private final String[] id, tittle,logolink,link;

    public SponsorsList(Activity context,String[] id, String[] tittle, String[] logolink, String[] link) {
        super(context, R.layout.sponsors_single_item, id);


        this.context = context;
        this.id =id;
        this.tittle =tittle;
        this.logolink= logolink;
        this.link =link;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sponsors_single_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.sponsor);
        txtTitle.setText(tittle[position]);
        ImageView image =(ImageView) rowView.findViewById(R.id.imageview);
        ImageLoader imgLoader = new ImageLoader(getContext());
        int loader = R.drawable.actionbar_logo;
        if(loadpref("firstjsonloadsucces")) {

        }
        String temp = "http://erp.saarang.org/media/" + logolink[position];
        imgLoader.DisplayImage(temp, loader, image);
        return rowView;
    }
    public  Boolean loadpref(String event){

        SharedPreferences fav= PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean cbvalue=fav.getBoolean(event, false);
        return cbvalue;
    }
}
