package com.example.easymealprep;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {
    EditText username_reset;
    Button resetPassword_Button;
    String userInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        username_reset = (EditText) findViewById(R.id.username_reset);
        resetPassword_Button = (Button) findViewById(R.id.resetPassword_Button);
        resetPassword_Button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.resetPassword_Button:  // TODO do the actions
                System.out.println("Reset Password");
                if (username_reset.getText().toString().length() == 0){
                    username_reset.requestFocus();
                    username_reset.setError("Please enter either a username or password");
                    break;
                }
                sendData();
                break;
        }
    }

    private void sendData() {
        userInput = username_reset.getText().toString();
        try {
            LongOperation l = new LongOperation();
            l.execute();  //sends the email in background
            Toast.makeText(ResetPassword.this, l.get(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public class LongOperation extends AsyncTask<Void, Void, String> {

        boolean bool = false;
        SQLConnection connect;

        @Override
        protected String doInBackground(Void... params) {
            String body = "";
            String password = "";
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                char ch = (char) (rand.nextInt(95) + 32);
                while (((int) ch == 47) || ((int) ch == 92)){
                    ch = (char) (rand.nextInt(95) + 32);
                }
                password += ch;
            }
            System.out.println(password);

            body += "The password for your EZ Meal Prep has been reset. Your new password is '" + password
                    + "' and you can use the password to sign in to the app and resetting it in the setting.\n"
                    + "Thank You";

            connect = new SQLConnection();
            Account account = new Account(connect.getConnection());
            boolean email = false;
            String recipient = "";
            email = Statics.isValidEmailAddress(userInput);
            recipient = account.getEmail(email, userInput);
            System.out.println(recipient);
            bool = account.resetPassword(email, userInput, password);
            if (bool && recipient != null) {
                try {
                    GMailSender sender = new GMailSender("ezmealprepapp@gmail.com", "sz&ZjckQB");
                    sender.sendMail("EZ Meal Prep Password Reset",
                            body, "ezmealprepapp@gmail.com",
                            recipient);
                } catch (Exception e) {
                    Log.e("error", e.getMessage(), e);
                    return "Email Not Sent";
                }
                return "The new password is sent to your email address. If it is not in the inbox check your junk folder.";
            }
            else {
                return "The username or the email is incorrect.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            connect.closeConnection();
            Log.e("LongOperation", result + "");
        }
    }
}