package com.example.easymealprep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment} factory method to
 * create an instance of this fragment.
 */
public class EditRecipeFragment extends Fragment {
    private static final int PICK_IMAGE = 100;
    EditText updateFoodName, updateFoodDescription, updateSteps, updateFoodIngredient, updateFoodTools;
    Button update_pic, delete_update_pic, updateRecipe_button;
    ImageView imageView;
    Uri imageUri;
    Bitmap bitmap;

    public EditRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        // Inflate the layout for this fragment

        updateFoodName = (EditText) inputFragmentView.findViewById(R.id.updateFoodName);
        updateFoodDescription = (EditText) inputFragmentView.findViewById(R.id.updateFoodDescription);
        updateSteps = (EditText) inputFragmentView.findViewById(R.id.updateSteps);
        updateFoodIngredient = (EditText) inputFragmentView.findViewById(R.id.updateFoodIngredient);
        updateFoodTools = (EditText) inputFragmentView.findViewById(R.id.updateFoodTools);

        imageView = (ImageView) inputFragmentView.findViewById(R.id.updateRecipe_iv);

        update_pic = (Button) inputFragmentView.findViewById(R.id.updateRecipePic_btn);
        delete_update_pic = (Button) inputFragmentView.findViewById(R.id.delete_update_pic);
        updateRecipe_button = (Button) inputFragmentView.findViewById(R.id.updateRecipe_btn);
        String foodName = (String) Statics.currFood[1];
        String foodDesc = (String) Statics.currFood[2];
        byte[] foodPic = (byte[]) Statics.currFood[3];
        updateFoodName.setText(foodName);
        updateFoodDescription.setText(foodDesc);

        if (foodPic != null) {
            bitmap = BitmapFactory.decodeByteArray(foodPic, 0, foodPic.length);
            imageView.setImageBitmap(bitmap);
        }
        new GetRecipeInfoAsync().execute();
        update_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Made it to AddRecipeFragment, onClick, button");

                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Made it to AddRecipeFragment, onClick, button, first if statement");

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
                }
                else {
                    System.out.println("Made it to AddRecipeFragment, onClick, button, else statement");
                    startGallery();
                }
            }
        });

        delete_update_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = null;
                imageView.setImageResource(R.drawable.default_food_pic);
            }
        });

        updateRecipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Made it to AddRecipeFragment, onClick, createRecipe_button");
                String name = updateFoodName.getText().toString();
                String description = updateFoodDescription.getText().toString();
                String instruction = updateSteps.getText().toString();
                String ingredient = updateFoodIngredient.getText().toString();
                String tool = updateFoodTools.getText().toString();
                try {
                    sendData(name, description, instruction, ingredient, tool, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return inputFragmentView;
    }

    private void sendData(String name, String description, String instruction, String ingredient, String tool, Bitmap bitmap) throws IOException {
        byte[] imageByte = null;
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap = getResizedBitmap(bitmap, 1000);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);

            imageByte = stream.toByteArray();
        }
        new UpdateRecipeAsync().execute(name, description, imageByte, instruction, ingredient, tool);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        Bitmap result = image;
        if (image == null)
            System.out.println("null bitmap");
        int width = image.getWidth();
        int height = image.getHeight();
        if (maxSize < width || maxSize < height) {
            System.out.println("resize bitmap");
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            result = Bitmap.createScaledBitmap(image, width, height, true);
        }
        System.out.println("no resize bitmap");

        return result;
    }


    private void startGallery() {
        System.out.println("Made it to AddRecipeFragment, startGallery");

        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // cameraIntent.setType("image/*");
       // if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, PICK_IMAGE);
        //}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Made it to AddRecipeFragment, onActivityResult");
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            System.out.println("Made it to AddRecipeFragment, onActivityResult, if statement");
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                System.out.println("Made it to AddRecipeFragment, onActivityResult, try statement");
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public class GetRecipeInfoAsync extends AsyncTask<Void,Void,Void> {
        ResultSet recipeResultSet;
        ArrayList [] ingredientsList;
        ArrayList [] toolsList;
        @Override
        protected Void doInBackground(Void... voids) {
            int foodID = (int) Statics.currFood[0];
            Recipe recipe = new Recipe(Statics.connection.getConnection(), Statics.currUserAccount);
            recipeResultSet = recipe.searchOneRecipe(foodID);
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            ingredientsList = ingredient.listIngredientFood(foodID);
            Tool tool = new Tool(Statics.connection.getConnection());
            toolsList = tool.listToolFood(foodID);
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
                        instruction = instruction + inst + "\n";
                    }
                } catch (SQLException e) {
                    System.out.println("recipe error post");
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("resultset null");
            }
            String ingredientsText = "";
            for (int i = 0; i < ingredientsList[1].size(); i++) {
                if (i < ingredientsList[1].size() - 1)
                    ingredientsText += ingredientsList[1].get(i) + "\n";
                else
                    ingredientsText += ingredientsList[1].get(i);
            }
            String toolsText = "";
            for (int i = 0; i < toolsList[1].size(); i++) {
                if (i < toolsList[1].size() - 1)
                    toolsText += toolsList[1].get(i) + "\n";
                else
                    toolsText += toolsList[1].get(i);
            }
            System.out.println(instruction);

            updateSteps.setText(instruction);
            updateFoodIngredient.setText(ingredientsText);
            updateFoodTools.setText(toolsText);
        }
    }

    public class UpdateRecipeAsync extends AsyncTask<Object,Void,Void> {
        String message = "";
        @Override
        protected Void doInBackground(Object... objects) {
            boolean bool = false;
            int foodID = (int) Statics.currFood[0];
            Food food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            String foodName = (String) objects[0];
            String foodDescription = (String) objects[1];
            byte [] picture = (byte[]) objects[2];
            String instruction = (String) objects[3];
            food.updateFood(foodID, null, null, null);
            bool = food.updateFood(foodID, foodName, foodDescription, picture);
            if (!bool) {
                message = " Food picture size might be too large.";
                return null;
            }

            Recipe recipe = new Recipe(Statics.connection.getConnection(), Statics.currUserAccount);
            String[] array;
            ArrayList<String> arrayList = new ArrayList<String>();
            String inst = "";
            int cc = 0;
            for (int i = 0; i < instruction.length(); i++) {
                inst += instruction.charAt(i);
                cc++;
                if (cc == 200 || i == instruction.length() - 1) {
                    arrayList.add(inst);
                    cc = 0;
                    inst = "";
                }
            }
            bool = recipe.updateRecipe(foodID, arrayList);
            if (!bool) {
                message = " Entry for the directions might have an issue";
                return null;
            }

            String ingredientsList = (String) objects[4];
            ingredientsList = ingredientsList.replaceAll(", ",",");
            array = ingredientsList.split(",");
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            arrayList = new ArrayList<>();
            for(String text:array) {
                arrayList.add(text);
                ingredient.createIngredient(text);
            }
            bool = ingredient.updateIngredient(foodID, arrayList);
            if (!ingredient.deleteIngredient()) {
                System.out.println("ingredient delete failed");
            }
            if (!bool) {
                message = " Entry for the ingredients might have an issue";
                return null;
            }

            Tool tool = new Tool(Statics.connection.getConnection());
            String toolsList = (String) objects[5];
            toolsList.replaceAll(", ", ",");
            array = toolsList.split(",");
            arrayList = new ArrayList<>();
            for(String text:array) {
                arrayList.add(text);
                tool.createTool(text);
            }
            bool = tool.updateToolFood(foodID, arrayList);

            if (tool.deleteTool()) {
                System.out.println("tool delete failed");
            }
            if (!bool) {
                message = " Entry for the tools might have an issue";
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (Statics.check) {
                builder.setMessage("Recipe Updated")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Fragment newFragment = new ListMyRecipeFragment();
                        // consider using Java coding conventions (upper first char class names!!!)
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                    }
                })
                .create()
                .show();
            } else {
                builder.setMessage("Recipe Update Failed." + message)
                .setNegativeButton("Retry", null)
                .create()
                .show();
            }
        }
    }
}