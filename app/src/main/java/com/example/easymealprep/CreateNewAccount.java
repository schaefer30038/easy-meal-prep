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
        if(true){
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