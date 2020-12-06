package com.example.easymealprep;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// THIS WHOLE FILE WAS CREATED IN ITERATION 1
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment {

    EditText name, password, email;
    Button update_button;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inputFragmentView = inflater.inflate(R.layout.fragment_update_profile, container, false);
        // Inflate the layout for this fragment
        name = (EditText) inputFragmentView.findViewById(R.id.etUpdateName);
        password = (EditText) inputFragmentView.findViewById(R.id.etUpdatePassword);
        email = (EditText) inputFragmentView.findViewById(R.id.etUpdateEmailAddress);
        update_button = (Button) inputFragmentView.findViewById(R.id.update_confirm);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendName = (String) name.getText().toString();
                String sendPassword = (String) password.getText().toString();
                String sendEmail = (String) update_button.getText().toString();
                sendData(sendName, sendPassword, sendEmail);
            }
        });
        return inputFragmentView;
    }

    private void sendData(String sendName, String sendPassword, String sendEmail) {
        new UpdateAccountAsync().execute(sendPassword, sendName, sendEmail);
    }

    public class UpdateAccountAsync extends AsyncTask<String,Void,Void> {
        Account account;
        @Override
        protected Void doInBackground(String... strings) {
            String userPassword = strings[0];
            String userName = strings[1];
            String userEmail = strings[2];
            System.out.println("async task background");
            account = new Account(Statics.connection.getConnection());
            Statics.check = account.updateAccount(userPassword, userName, userEmail);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("async task post");
            if (Statics.check) {
                Fragment newFragment = new SettingFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                Toast.makeText(getActivity(), "Account info updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Account not updated", Toast.LENGTH_SHORT).show();
            }
            Statics.loop = false;
        }
    }
}