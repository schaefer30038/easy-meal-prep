package com.example.easymealprep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment {
    private static final int PICK_IMAGE = 100;
    EditText enterFoodName, enterFoodDescription, enterSteps, enterFoodIngredient, enterFoodTools;
    Button add_pic, delete_add_pic, createRecipe_button;
    ImageView imageView;
    Uri imageUri;
    Bitmap bitmap;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        // Inflate the layout for this fragment

        enterFoodName = (EditText) inputFragmentView.findViewById(R.id.enterFoodName);
        enterFoodDescription = (EditText) inputFragmentView.findViewById(R.id.enterFoodDescription);
        enterSteps = (EditText) inputFragmentView.findViewById(R.id.enterSteps);
        enterFoodIngredient = (EditText) inputFragmentView.findViewById(R.id.enterFoodIngredient);
        enterFoodTools = (EditText) inputFragmentView.findViewById(R.id.enterFoodTools);

        imageView = (ImageView) inputFragmentView.findViewById(R.id.imageView);

        add_pic = (Button) inputFragmentView.findViewById(R.id.add_pic_btn);
        delete_add_pic = (Button) inputFragmentView.findViewById(R.id.delete_add_pic_btn);
        createRecipe_button = (Button) inputFragmentView.findViewById(R.id.createRecipe_button);

        add_pic.setOnClickListener(new View.OnClickListener() {
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
        delete_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = null;
                imageView.setImageResource(R.drawable.default_food_pic);
            }
        });

        createRecipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Made it to AddRecipeFragment, onClick, createRecipe_button");
                String name = enterFoodName.getText().toString();
                String description = enterFoodDescription.getText().toString();
                String instruction = enterSteps.getText().toString();
                String ingredient = enterFoodIngredient.getText().toString();
                String tool = enterFoodTools.getText().toString();
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
        new CreateRecipeAsync().execute(name, description, imageByte, instruction, ingredient, tool);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        Bitmap result = image;
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
        //    cameraIntent.setType("image/*");
         //   if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(cameraIntent, PICK_IMAGE);
          //  }
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class CreateRecipeAsync extends AsyncTask<Object,Void,Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            Food food = new Food(Statics.connection.getConnection(), Statics.currUserAccount);
            String foodName = (String) objects[0];
            String foodDescription = (String) objects[1];
            byte [] picture = (byte[]) objects[2];
            String instruction = (String) objects[3];

            Statics.check = food.createFood(foodName, foodDescription, picture);
            int foodID = food.getFoodID(foodName);
            Recipe recipe = new Recipe(Statics.connection.getConnection(), Statics.currUserAccount);
            String[] array = instruction.split("\n");
            ArrayList<String> arrayList = new ArrayList<>();
            for(String text:array) {
                arrayList.add(text);
            }
            Statics.check = Statics.check && recipe.createRecipe(foodID, arrayList);
            String ingredientsList = (String) objects[4];
            array = ingredientsList.split("\n");
            Ingredient ingredient = new Ingredient(Statics.connection.getConnection());
            for(String text:array) {
                Statics.check = Statics.check && ingredient.createIngredient(text);
                Statics.check = Statics.check && ingredient.createIngredientFood(foodID, text);
            }
            Tool tool = new Tool(Statics.connection.getConnection());
            String toolsList = (String) objects[5];
            array = toolsList.split("\n");
            for(String text:array) {
                Statics.check = Statics.check && tool.createTool(text);
                Statics.check = Statics.check && tool.createToolFood(foodID, text);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (Statics.check) {
                builder.setMessage("Recipe Created")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Fragment newFragment = new AddRecipeFragment();
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
                builder.setMessage("Recipe Create Failed")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }
        }
    }
}