package com.example.easymealprep;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// MOST OF FILE WAS CREATED IN ITERATION 1
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodFragment#} factory method to
 * create an instance of this fragment.
 */
public class FoodFragment extends Fragment {

    TextView foodNameTV, foodDescTV, ingredientsTV, toolsTV, instructionsTV;
    ToggleButton fav_toggle;
    ImageView foodPicIV;

    public FoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_food, container, false);
        // Inflate the layout for this fragment
        foodNameTV = (TextView) inputFragmentView.findViewById(R.id.food_name_tv);
        foodDescTV = (TextView) inputFragmentView.findViewById(R.id.food_desc_tv);
        foodPicIV = (ImageView) inputFragmentView.findViewById(R.id.food_pic_iv);
        // TODO ingredient list when implements
        ingredientsTV = (TextView) inputFragmentView.findViewById(R.id.ingredients_tv);
        toolsTV = (TextView) inputFragmentView.findViewById(R.id.tools_tv);
        instructionsTV = (TextView) inputFragmentView.findViewById(R.id.instructions_tv);
        fav_toggle = (ToggleButton) inputFragmentView.findViewById(R.id.fav_toggle);
        int foodID = (int) Statics.currFood[0];
        System.out.print("Favorite List:");
        for (int i = 0; i < Statics.currFavList.size(); i++) {
            System.out.print(Statics.currFavList.get(i)+", ");
        }
        System.out.println();
        String foodName = (String) Statics.currFood[1];
        String foodDesc = (String) Statics.currFood[2];
        byte[] foodPic = (byte[]) Statics.currFood[3];
        if (Statics.currFavList.contains(foodID)) {
            fav_toggle.setChecked(true);
        } else {
            fav_toggle.setChecked(false);
        }
        fav_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
                new FavoriteFoodAsync().execute(isChecked);
            }
        });
        foodNameTV.setText(foodName);
        foodDescTV.setText(foodDesc);

        if (foodPic == null)
            foodPicIV.setImageResource(R.drawable.default_food_pic);
        else {
            Bitmap bmp = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
            foodPicIV.setImageBitmap(bmp);
        }
        sendData(foodID);
        return inputFragmentView;
    }

    private void sendData(int foodID) {
        new GetRecipeInfoAsync().execute(foodID);
    }

    public class FavoriteFoodAsync extends AsyncTask<Boolean,Void,Void> {
        Account account;

        @Override
        protected Void doInBackground(Boolean... booleans) {
            Statics.connection = new SQLConnection();

            System.out.println("Entered in LoginAccountAsync:doInBackground ");
            boolean fav = booleans[0];
            int foodID = (int) Statics.currFood[0];
            account = new Account(Statics.connection.getConnection());
            if (fav) {
                boolean set = account.setFavorite(foodID);
                Statics.currFavList.add(foodID);
                if (set)
                    System.out.println("set pass");
                else
                    System.out.println("set fail");
            } else {
                boolean set = account.deleteFavorite(foodID);
                Statics.currFavList.remove((Object)foodID);
                if (set)
                    System.out.println("remove pass");
                else
                    System.out.println("remove fail");
            }
            return null;
        }
    }
    public class GetRecipeInfoAsync extends AsyncTask<Integer,Void,Void> {
        ResultSet recipeResultSet;
        ArrayList [] ingredientsList;
        ArrayList [] toolsList;
        @Override
        protected Void doInBackground(Integer... integers) {
            Recipe recipe = new Recipe(Statics.connection.getConnection(), Statics.currUserAccount);
            int foodID = integers[0];
            System.out.println(foodID);
            recipeResultSet = recipe.searchOneRecipe(foodID);
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            ingredientsList = ingredient.listIngredientFood(foodID);
            Tool tool = new Tool(Statics.connection.getConnection());
            toolsList = tool.listToolFood(foodID);//ADDED IN ITERATION 2
            System.out.println("recipe back");
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            String instruction = "";
            if (recipeResultSet != null) {
                System.out.println("recipe post");
                try {
                    while (recipeResultSet.next()) {
                        int step = recipeResultSet.getInt("step");
                        String inst = recipeResultSet.getString("instruction");
                        instruction = instruction + step + ". " + inst + "\n";
                    }
                } catch (SQLException e) {
                    System.out.println("recipe error post");
                    e.printStackTrace();
                }
            }
            String ingredientsText = "";
            for (int i = 0; i < ingredientsList[1].size(); i++) {//ADDED IN ITERATION 2
                if (i < ingredientsList[1].size() - 1)
                    ingredientsText += ingredientsList[1].get(i) + ", ";
                else
                    ingredientsText += ingredientsList[1].get(i);
            }
            String toolsText = "";
            for (int i = 0; i < toolsList[1].size(); i++) {//ADDED IN ITERATION 2
                if (i < toolsList[1].size() - 1)
                    toolsText += toolsList[1].get(i) + ", ";
                else
                    toolsText += toolsList[1].get(i);
            }
            System.out.println(instruction);

            instructionsTV.setText(instruction);
            ingredientsTV.setText(ingredientsText);
            toolsTV.setText(toolsText);
        }
    }
}