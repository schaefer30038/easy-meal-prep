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
    ArrayList<String> ingredientsList;
    ArrayList<String> toolsList;
    View inputFragmentView;

    private static ArrayList<String> selectedIngredients;
    private static ArrayList<String> selectedTools;
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
        inputFragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        selectedIngredients = new ArrayList<>();
        selectedTools =  new ArrayList<>();
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

        new GetListAsync().execute();
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

    public class GetListAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            Tool tool = new Tool(Statics.connection.getConnection());
            ingredientsList = ingredient.listIngredient();
            toolsList = tool.listTool();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println(ingredientsList.get(3));
            System.out.println(ingredientsList.get(4));
            Spinner spinnerIngred= (Spinner) inputFragmentView.findViewById(R.id.spinnerIngred);
            Spinner spinnerTool = (Spinner) inputFragmentView.findViewById(R.id.spinnerTool);
            ArrayList<StateVO> listVOsIngred = new ArrayList<>();
            for (int i = 0; i < ingredientsList.size(); i++) {
                StateVO stateVOIngred = new StateVO();
                stateVOIngred.setTitle(ingredientsList.get(i));
                stateVOIngred.setSelected(false);
                listVOsIngred.add(stateVOIngred);
            }
            ArrayList<StateVO> listVOsTool = new ArrayList<>();

            for (int i = 0; i < toolsList.size(); i++) {
                StateVO stateVOTool = new StateVO();
                stateVOTool.setTitle(toolsList.get(i));
                stateVOTool.setSelected(false);
                listVOsTool.add(stateVOTool);
            }
            filterAdapter myAdapterTool = new filterAdapter(getActivity(), 0,
                    listVOsTool,toolsList,selectedTools);
            filterAdapter myAdapterIngred = new filterAdapter(getActivity(), 0,
                    listVOsIngred,ingredientsList,selectedIngredients);

            spinnerTool.setAdapter(myAdapterTool);
            spinnerIngred.setAdapter(myAdapterIngred);
        }
    }
    public class SearchFoodAsync extends AsyncTask<String, Void, Void> {
        Food food;
        ResultSet resultSet;
        ArrayList<String> list;


        @Override
        protected Void doInBackground(String... strings) {
            food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            Tool tool = new Tool(Statics.connection.getConnection());
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            String searchName = strings[0];
            if (searchName.length()==0) {
                resultSet = food.listAllFood();
            } else {
                resultSet = food.searchFood(searchName);
            }
            ArrayList<String> toolFoodID = new ArrayList<>();
            ArrayList<String> ingredientFoodID = new ArrayList<>();

            list = new ArrayList<>();
            if (resultSet != null) {
                System.out.println("ASDasd");
                try {
                    while (resultSet.next()) {
                        boolean toolBool = true;
                        boolean ingredientBool = true;
                        int foodID = resultSet.getInt("foodID");
                        String foodName = resultSet.getString("foodName");
                        String foodDescription = resultSet.getString("foodDescription");
                        byte [] foodPic = resultSet.getBytes("foodPic");
                        Object[] array = new Object[4];
                        toolFoodID = tool.listToolFood(foodID);
                        System.out.println(foodID);
                        if (!selectedTools.isEmpty()){
                            for (String t : selectedTools) {
                                System.out.println(t);
                                if (!toolFoodID.contains(t)){
                                    toolBool = false;
                                    break;
                                }
                            }
                        }
                        if (!selectedIngredients.isEmpty()) {
                            ingredientFoodID = ingredient.listIngredientFood(foodID);
                            for (String i : selectedIngredients) {
                                System.out.println(i);
                                if (!ingredientFoodID.contains(i)) {
                                    System.out.println(false);
                                    ingredientBool = false;
                                    break;
                                }
                            }
                        }

                        if (toolBool && ingredientBool) {
                            System.out.println(foodName);
                            list.add(foodName);
                            array[0] = foodID;
                            array[1] = foodName;
                            array[2] = foodDescription;
                            array[3] = foodPic;
                            arrayLists.add(array);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
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