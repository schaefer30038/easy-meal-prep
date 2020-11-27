package com.example.easymealprep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// THIS WHOLE FILE WAS CREATED IN ITERATION 1
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView title, login;
    EditText username, password;
    Button newAccount, loginB;


    //ImageView uploadPicture_ImageView;
    private ProgressBar prog;
    // TODO: this string will be "Username" if account doesn't exist
    // it will be "Password" if the password is wrong and    if username and password is a match it will return "Match"
    //static String login;
    // Stores the current username
    //static String currUser;

    //static boolean done = false;
    //public static final int GET_FROM_GALLERY = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.title_TextView);
        // loginScreenLabel = (TextView) findViewById(R.id.loginScreenLabel_TextView);
        login = (TextView) findViewById(R.id.login_TextView);

        username = (EditText) findViewById(R.id.username_EditText);
        password = (EditText) findViewById(R.id.password_EditText);

        newAccount = (Button) findViewById(R.id.newAccount_Button);
        loginB = (Button) findViewById(R.id.loginB_Button);

        newAccount.setOnClickListener(this);
        loginB.setOnClickListener(this);

        prog = (ProgressBar)findViewById(R.id.progressBar) ;
        prog.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        password.setText("");
        if(Statics.connection != null)
        Statics.connection.closeConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.newAccount_Button:
                System.out.println("Made it in switch statement newAccountButton");
                Intent intent2CreateNewAccountPage = new Intent(MainActivity.this, CreateNewAccount.class);
                startActivity(intent2CreateNewAccountPage);
                break;

            case R.id.loginB_Button:
                System.out.println("Made it in switch statement loginB");
                prog.setVisibility(View.VISIBLE);
                sendData();
                break;
        }
    }

    private void sendData() {
        String currUser = username.getText().toString();
        String pass = password.getText().toString();
        System.out.println("Testing BEFRORE LoginAccountAsync");
        new LoginAccountAsync().execute(currUser,pass);
        System.out.println("Testing AFTER LoginAccountAsync");
    }

    public class LoginAccountAsync extends AsyncTask<String,Void,Void> {
        Account account;
        @Override
        protected Void doInBackground(String... strings) {
            Statics.connection = new SQLConnection();

            System.out.println("Entered in LoginAccountAsync:doInBackground ");
            String accountName = strings[0];
            String password = strings[1];
            account = new Account(Statics.connection.getConnection());
            Statics.check = account.loginAccount(accountName,password);
            ResultSet resultSet = account.getFavorite();
            Statics.currFavList = new ArrayList<>();
            if (resultSet != null) {
                System.out.println("ASDasd");
                try {
                    System.out.println("try favlist");
                    while (resultSet.next()) {
                        System.out.println("while favlist");
                        int foodID = resultSet.getInt("foodID");
                        Statics.currFavList.add(foodID);
                        System.out.println(foodID);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Statics.check + "do in bac");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("onPost exec1");
            super.onPostExecute(aVoid);
            System.out.println("onPost exec2");
            System.out.println("onPost exec3");
            System.out.println("Inside sendData handler, Run method");
            if(Statics.check){
                System.out.println("loginCheck works");
                Intent intent2Main = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent2Main);
            }
            else {
                System.out.println("loginCheck didnt work");


                // Show error
                Toast.makeText(MainActivity.this, "Incorrect Login Credentials", Toast.LENGTH_SHORT).show();
            }
            prog.setVisibility(View.GONE);
            System.out.println("main activity done");
        }
    }
}
