package com.example.easymealprep;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//WHOLE FILE CREATED IN ITERATION1
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    ListView listView;
    static ArrayList <Object[]> arrayLists;
    GeneralListAdapter generalListAdapter;
    ArrayList <String> nameList;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);
        // Inflate the layout for this fragment
        listView = (ListView) inputFragmentView.findViewById(R.id.fav_list);
        arrayLists = new ArrayList<>();
        nameList = new ArrayList<>();
        new GetFavoriteAsync().execute();
        return inputFragmentView;
    }

    public class GetFavoriteAsync extends AsyncTask<Void,Void,Void> {
        Food food;
        ResultSet resultSet;
        @Override
        protected Void doInBackground(Void... voids) {
            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
//            arrayLists = food.getFavoritesFoodName(Statics.currFavList);
//            for (int i : Statics.currFavList) {
                ResultSet resultSet =  food.listAllFood();
                if (resultSet != null) {
                    try {
                        while (resultSet.next()) {
                            int foodID = resultSet.getInt("foodID");
                            String foodName = resultSet.getString("foodName");
                            String foodDescription = resultSet.getString("foodDescription");
                            byte [] foodPic = resultSet.getBytes("foodPic");
                            if (Statics.currFavList.contains(foodID)) {
                                Object[] obj = new Object[4];
                                obj[0] = foodID;
                                obj[1] = foodName;
                                obj[2] = foodDescription;
                                obj[3] = foodPic;
                                arrayLists.add(obj);
                                nameList.add(foodName);
                                System.out.println("GetFavoriteAsync " + foodID);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            generalListAdapter = new GeneralListAdapter(getActivity(), R.layout.listview_items, nameList);
            GeneralListAdapter.listName = "Favorites List";
            listView.setAdapter(generalListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
//    public class GetFavoritesFoodNameAsync extends AsyncTask<Void,Void,Void> {
//        Food food;
//        ResultSet resultSet;
//        @Override
//        protected Void doInBackground(Void... avoid) {
//            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
//            arrayLists = food.getFavoritesFoodName(list);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            for (int i = 0; i < arrayLists.size(); i++) {
//                nameList.add((String) arrayLists.get(i)[1]);
//                System.out.println(nameList.get(i));
//            }
//            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameList);
//
//            listView.setAdapter(arrayAdapter);
//
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Statics.currFood[0] = arrayLists.get(position)[0];
//                    Statics.currFood[1] = arrayLists.get(position)[1];
//                    Statics.currFood[2] = arrayLists.get(position)[2];
//                    Statics.currFood[3] = arrayLists.get(position)[3];
//                    Fragment newFragment = new FoodFragment();
//                    // consider using Java coding conventions (upper first char class names!!!)
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                    // Replace whatever is in the fragment_container view with this fragment,
//                    // and add the transaction to the back stack
//                    transaction.replace(R.id.fragment_container, newFragment);
//                    transaction.addToBackStack(null);
//
//                    // Commit the transaction
//                    transaction.commit();
//                }
//            });
//        }
//    }
}