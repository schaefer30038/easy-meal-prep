package com.example.easymealprep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
// THIS WHOLE FILE WAS CREATED IN ITERATION 1
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    Button delete_acc_btn, update_acc_btn, help_btn;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        delete_acc_btn = (Button) inputFragmentView.findViewById(R.id.delete_account);
        update_acc_btn = (Button) inputFragmentView.findViewById(R.id.update_profile);
        help_btn = (Button) inputFragmentView.findViewById(R.id.help);

        delete_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete your account? This action can't be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteAccountAsync().execute(Statics.currPassword);
                            }
                        }).setNegativeButton("No", null)
                        .create()
                        .show();


            }
        });
        update_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new UpdateProfileFragment();
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
        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new HelpFragment();
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
        return inputFragmentView;
    }
    public class DeleteAccountAsync extends AsyncTask<String,Void,Void> {
        Account account;
        @Override
        protected Void doInBackground(String... strings) {
            String userPassword = strings[0];
            account = new Account(Statics.connection.getConnection());
            Statics.check = account.deleteAccount(userPassword);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent2Main = new Intent(getActivity(), MainActivity.class);
            startActivity(intent2Main);
        }
    }
}