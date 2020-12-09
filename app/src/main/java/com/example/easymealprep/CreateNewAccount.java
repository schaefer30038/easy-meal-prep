//package com.example.easymealprep;
////
////import androidx.appcompat.app.AppCompatActivity;
////
////import android.os.Bundle;
////
////public class CreateNewAccount extends AppCompatActivity {
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_create_new_account);
////    }
////}


package com.example.easymealprep;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static com.example.easymealprep.Statics.check;

//WHOLE FILE CREATED IN ITERATION 1
public class CreateNewAccount extends AppCompatActivity implements View.OnClickListener{
    TextView newAccountLabel;
    EditText username, password, name, email;
    Button createAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        newAccountLabel = (TextView) findViewById(R.id.newAccountLabel_TextView);

        username = (EditText) findViewById(R.id.username_EditText);
        password = (EditText) findViewById(R.id.password_EditText);
        name = (EditText) findViewById(R.id.name_EditText);
        email = (EditText) findViewById(R.id.email_EditText);

        createAccount = (Button) findViewById(R.id.createAccount_Button);

        createAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.createAccount_Button:  // TODO do the actions
                System.out.println("Create account");
                sendData();
                break;
        }
    }

    private void sendData() {
        AlertDialog.Builder b = new AlertDialog.Builder(CreateNewAccount.this);
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        if(usernameString.length() == 0){
            username.requestFocus();
            username.setError("Please enter a username");
        }
        else if(!usernameString.matches(".{5,20}")){
            username.requestFocus();
            username.setError("Username has to be between 5 and 20 characters");
        }
        else if(!usernameString.matches("[a-zA-Z0-9]+")){
            username.requestFocus();
            username.setError("Username can only be alphabets and digits");
        }
        else if(passwordString.length() == 0){
            password.requestFocus();
            password.setError("Please enter a password");
        }
        else if(!passwordString.matches(".{8,20}")){
            password.requestFocus();
            password.setError("Password has to be between 8 and 20 characters");
        }
        else if(!passwordString.matches("(?=.*[0-9]).{8,20}")){
            password.requestFocus();
            password.setError("Password has to have at least one digit");
        }
         else if(!passwordString.matches("(?=.*[a-z]).{8,20}")){
            password.requestFocus();
            password.setError("Password has to have at least one lower case alphabet");
        }
         else if(!passwordString.matches("(?=.*[A-Z]).{8,20}")){
            password.requestFocus();
            password.setError("Password has to have at least one upper case alphabet");
        }
         else if(!passwordString.matches("(?=.*[-@#$%^<>&+=()]).{8,20}")){
            password.requestFocus();
            password.setError("Password has to have at least one special character from '-@#$%^&+=<>()'");
        }
        else if(!passwordString.matches("(?=\\S+$).{8,20}")){
            password.requestFocus();
            password.setError("Password has either a space or non allowed character");
        }
        else if(nameString.length() == 0){
            name.requestFocus();
            name.setError("Please enter a name");
        }
        else if(!nameString.matches("[a-zA-Z ]+")) {
            name.requestFocus();
            name.setError("Enter only alphabetical characters");
        }
        else if(emailString.length() == 0){
            email.requestFocus();
            email.setError("Please enter an email");
        } else if(!Statics.isValidEmailAddress(emailString)) {
            email.requestFocus();
            email.setError("Please enter a valid email address");
        }
        else {
            new CreateAccountAsync().execute(usernameString, passwordString, nameString, emailString);
        }

    }



    public class CreateAccountAsync extends AsyncTask<String,Void,Void> {
        Account account;
        SQLConnection connect;
        @Override
        protected Void doInBackground(String... strings) {
            connect = new SQLConnection();
            account = new Account(connect.getConnection());
            String accountName = strings[0];
            String userPassword = strings[1];
            String userName = strings[2];
            String userEmail = strings[3];
            System.out.println("create async");
            Statics.check = account.createAccount(accountName, userPassword, userName, userEmail);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("onPost exec");
            connect.closeConnection();
            System.out.print(check);
            if(check){
                Toast.makeText(CreateNewAccount.this,"Account successfully created",Toast.LENGTH_SHORT).show();
                Intent intent2Main = new Intent(CreateNewAccount.this, MainActivity.class);
                startActivity(intent2Main);
            }
            else{
                // Show error
                Toast.makeText(CreateNewAccount.this,"Account already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }
}