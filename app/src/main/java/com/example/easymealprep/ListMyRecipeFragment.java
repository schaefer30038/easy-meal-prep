package com.example.easymealprep;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

// THIS WHOLE FILE WAS CREATED IN ITERATION 2
public class ListMyRecipeFragment extends Fragment {
    private View view;
    private ListView mListview;
    private ArrayList<String> mArrData;
    private ListAdapter mAdapter;
    private ProgressBar pbar;
    static ArrayList <Object[]> arrayLists;
    static FragmentManager fragmanager;
    // TODO add titles for recipes to list
    // TODO connect to FoodFragment to view recipe

    public ListMyRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        arrayLists = new ArrayList <Object[]>();
        view=inflater.inflate(R.layout.fragment_list, container,false);
        mListview = (ListView) view.findViewById(R.id.recipeTitles);
        mArrData = new ArrayList<String>();
        mAdapter = new ListAdapter(getActivity(),R.layout.customlistlayout, mArrData);
        mListview.setAdapter(mAdapter);
        new ListUserFoodAsync().execute();


        return view;
    }

    public class ListUserFoodAsync extends AsyncTask<Void,Void,Void> {
        Food food;
        ResultSet resultSet;
        @Override
        protected Void doInBackground(Void... voids) {
            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            resultSet = food.listUserFood();
            return null;
        }
        // TODO get only my recipe
        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> list = new ArrayList<>();
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        int foodID = resultSet.getInt("foodID");
                        String foodName = resultSet.getString("foodName");
                        String foodDescription = resultSet.getString("foodDescription");
                        list.add(foodName);
                        byte[] foodPic = resultSet.getBytes("foodPic");
                        Object[] array = new Object[4];
                        array[0] = foodID;
                        array[1] = foodName;
                        array[2] = foodDescription;
                        array[3] = foodPic;
                        arrayLists.add(array);
                        System.out.println(foodName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            mAdapter = new ListAdapter(getActivity(), R.layout.customlistlayout, list);
            mListview.setAdapter(mAdapter);
            fragmanager = getFragmentManager();
        //    pbar.setVisibility(View.GONE);


            mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Statics.currFood[0] = arrayLists.get(position)[0];
                    Statics.currFood[1] = arrayLists.get(position)[1];
                    Statics.currFood[2] = arrayLists.get(position)[2];
                    Statics.currFood[3] = arrayLists.get(position)[3];
                    Fragment newFragment = new FoodFragment();
                    // consider using Java coding conventions (upper first char class names!!!)
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
            });


        }
    }
}