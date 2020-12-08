package com.example.easymealprep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import static com.example.easymealprep.ListMyRecipeFragment.arrayLists;
// THIS WHOLE FILE WAS CREATED IN ITERATION 2
public class ListAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    @SuppressLint("StaticFieldLeak")
    static Activity activity;
    private ArrayList<String> entryData;
    

    public ListAdapter(Context context, int resource, ArrayList arrData) {
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
        final ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deleteBtn);
        final ImageButton editButton = (ImageButton)view.findViewById(R.id.editBtn);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        byte[] foodPic = (byte[]) arrayLists.get(position)[3];
        if (foodPic != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
            imageView.setImageBitmap(bmp);
        }

        // Set the title and button name
        title.setText(entryData.get(position));
        //btnAction.setText("Action " + position);

        // Click listener of button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Logic for delete
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to delete the recipe? This action can't be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteFoodAsync().execute(entryData.get(position));
                                entryData.remove(entryData.get(position));
                                arrayLists.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Logic for Edit
                Statics.currFood[0] = arrayLists.get(position)[0];
                Statics.currFood[1] = arrayLists.get(position)[1];
                Statics.currFood[2] = arrayLists.get(position)[2];
                Statics.currFood[3] = arrayLists.get(position)[3];
                Fragment newFragment = new EditRecipeFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = ListMyRecipeFragment.fragmanager.beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                notifyDataSetChanged();

            }
        });

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

    public class DeleteFoodAsync extends AsyncTask<String,Void,Void> {
        Food food;
        @Override
        protected Void doInBackground(String... strings) {
            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            String foodName = strings[0];
            Statics.check = food.deleteFood(foodName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

