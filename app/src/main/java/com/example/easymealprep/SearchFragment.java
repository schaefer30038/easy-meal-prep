package com.example.easymealprep;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// THIS WHOLE FILE WAS CREATED IN ITERATION 1
 /**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    TextView title;
    EditText searchBox;
    Button search_button;
    ListView listView;
    GeneralListAdapter generalListAdapter;
    static ArrayList <Object[]> arrayLists;

    private static ArrayList<String> selectedIngredients =  new ArrayList<>();
     private static ArrayList<String> selectedTools =  new ArrayList<>();
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        searchBox = (EditText) inputFragmentView.findViewById(R.id.searchField);
        listView = (ListView) inputFragmentView.findViewById(R.id.list);
        search_button = (Button) inputFragmentView.findViewById(R.id.search_b);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Searching for recipes. Please wait a moment", Toast.LENGTH_SHORT).show();
                String data = (String) searchBox.getText().toString();
                arrayLists = new ArrayList <Object[]>();
                sendData(data);
            }
        });
        //TODO get list of ingredients and shove it in here
        final String[] select_ingredients = {
                "Select Ingredients", "Milk", "Brocolli", "Ketchup", "Jesus",
                "Protein Powder"};

        // TODO get list of tools and shove it in here
        final String[] select_tools = {
                "Select Tools", "Microwave", "Oven", "Stove", "Toaster",
                "Bible"};
        Spinner spinnerIngred= (Spinner) inputFragmentView.findViewById(R.id.spinnerIngred);
        Spinner spinnerTool = (Spinner) inputFragmentView.findViewById(R.id.spinnerTool);
        ArrayList<StateVO> listVOsIngred = new ArrayList<>();
        String[] selected;
         for (int i = 0; i < select_ingredients.length; i++) {
            StateVO stateVOIngred = new StateVO();
            stateVOIngred.setTitle(select_ingredients[i]);
            stateVOIngred.setSelected(false);
            listVOsIngred.add(stateVOIngred);
        }
        ArrayList<StateVO> listVOsTool = new ArrayList<>();

        for (int i = 0; i < select_tools.length; i++) {
            StateVO stateVOTool = new StateVO();
            stateVOTool.setTitle(select_tools[i]);
            stateVOTool.setSelected(false);
            listVOsTool.add(stateVOTool);
        }
        filterAdapter myAdapterTool = new filterAdapter(getActivity(), 0,
                listVOsTool,select_tools,selectedTools);
        filterAdapter myAdapterIngred = new filterAdapter(getActivity(), 0,
                listVOsIngred,select_ingredients,selectedIngredients);

        spinnerTool.setAdapter(myAdapterTool);
        spinnerIngred.setAdapter(myAdapterIngred);
        return inputFragmentView;
    }

    //TODO for search
    private void sendData(String data) {
        //data is recipe title
        System.out.println(data);
        //TODO selectedIngredients is the selcted ingreds same for tools both arraylists contain strings
        for(int i = 0;i < selectedIngredients.size();i++){
            System.out.print(selectedIngredients.get(i));
            System.out.println("Next");
        }
        for(int i = 0;i < selectedTools.size();i++){
            System.out.print(selectedTools.get(i));
            System.out.println("Next");
        }
        new SearchFoodAsync().execute(data);
    }

    public class SearchFoodAsync extends AsyncTask<String, Void, ResultSet> {
        Food food;

        @Override
        protected ResultSet doInBackground(String... strings) {
            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            String foodName = strings[0];
            ResultSet resultSet = food.searchFood(foodName);
            return resultSet;
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            super.onPostExecute(resultSet);
            ArrayList<String> list = new ArrayList<>();
            if (resultSet != null) {
                System.out.println("ASDasd");
                try {
                    while (resultSet.next()) {
                        int foodID = resultSet.getInt("foodID");
                        String foodName = resultSet.getString("foodName");
                        String foodDescription = resultSet.getString("foodDescription");
                        list.add(foodName);
                        byte [] foodPic = resultSet.getBytes("foodPic");
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
            if (list.isEmpty()){
                Toast.makeText(getActivity(), "No result found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Here are your search results", Toast.LENGTH_SHORT).show();
            }
            generalListAdapter = new GeneralListAdapter(getActivity(), R.layout.listview_items, list);
            GeneralListAdapter.listName = "Search List";
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
}