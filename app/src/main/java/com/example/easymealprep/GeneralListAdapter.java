package com.example.easymealprep;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
// THIS WHOLE FILE WAS CREATED IN ITERATION 2
public class GeneralListAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private Activity activity;
    private ArrayList<String> entryData;
    static String listName;


    public GeneralListAdapter(Context context, int resource, ArrayList arrData) {
        super();
        this.context = context;
        this.resource = resource;
        this.entryData = arrData;
    }

    public int getCount() {
        // return the number of records
        return entryData.size();
    }

    // getView method is called for each item of ListView
    public View getView(final int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, parent, false);
        // view = inflater.inflate(R.layout.customlistlayout, parent, false);
        // view = inflater.inflate(R.layout.customlistlayout, null, true);


        // get the reference of textView and button
        TextView title = (TextView) view.findViewById(R.id.recipeName);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        if (listName.equals("Search List")) {
            byte[] foodPic = (byte[]) SearchFragment.arrayLists.get(position)[3];
            if (foodPic != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
                imageView.setImageBitmap(bmp);
            }
        }

        if (listName.equals("All List")) {
            byte[] foodPic = (byte[]) ListRecipeFragment.arrayLists.get(position)[3];
            if (foodPic != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
                imageView.setImageBitmap(bmp);
            }
        }

        if (listName.equals("Favorites List")) {
            byte[] foodPic = (byte[]) FavoritesFragment.arrayLists.get(position)[3];
            if (foodPic != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
                imageView.setImageBitmap(bmp);
            }
        }

        // Set the title and button name
        title.setText(entryData.get(position));
        //btnAction.setText("Action " + position);

        // Click listener of button



        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


}

