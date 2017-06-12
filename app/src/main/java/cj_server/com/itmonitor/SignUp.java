package cj_server.com.itmonitor;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cj_server.com.itmonitor.Pojo.StudentAccount;
import cj_server.com.itmonitor.databinding.SignUpBinding;

public class SignUp extends BaseActivity {
    private SignUpBinding binding;
    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private static  final String TAG =  "SIGN_UP";
    private FirebaseDatabase database;
    private DatabaseReference mref;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        setSupportActionBar(binding.included.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign up");
        }
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                createAccount(binding.edEmailAddress.getText().toString(),binding.edPassword.getText().toString());

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate(){
        if(TextUtils.isEmpty(binding.edStudentName.getText())){
            showSnack("Please  Enter your  Name");
            requestFocus(binding.edStudentName);
            return false;
        }
        if (TextUtils.isEmpty(binding.edRegNo.getText())){
            showSnack("Registration  No");
            requestFocus(binding.edRegNo);
            return false;
        }
        if(TextUtils.isEmpty(binding.edMobileNo.getText())){
            showSnack("Mobile Number is Required");
            return false;
        }
        if(TextUtils.isEmpty(binding.edEmailAddress.getText().toString())){
            showSnack("Please  enter Email Address");
            requestFocus(binding.edEmailAddress);
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.edEmailAddress.getText().toString()).matches()){
            showSnack("Please Enter a Valid Email Address");
            requestFocus(binding.edEmailAddress);
            return false;
        }
        if (TextUtils.isEmpty(binding.edPassword.getText())){
            showSnack("Enter your Email Address");
            requestFocus(binding.edPassword);
            return false;
        }
        return true;
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validate()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            Log.d(TAG,user.getUid());
                            String userUID = user.getUid();
                            Uri photoUrl = user.getPhotoUrl();
                            saveAccountDetails(binding.edStudentName.getText().toString(),binding.edRegNo.getText().toString(),binding.edMobileNo.getText().toString(),photoUrl,userUID);

//                            sending email verification to created account
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Snackbar.make(binding.layout,"Email Verification has been sent to "+user.getEmail(),Snackbar.LENGTH_INDEFINITE).show();
                                    }else {
                                        showSnack("Email Verifcation failed to send");
                                    }

                                }
                            });
//                            end of sending email verification

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:faure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }
    private void saveAccountDetails(String name, String regNo, String mobileNo,Uri photo, String UID){
        mref = database.getReference("Student Account");
        StudentAccount studentAccount = new StudentAccount(name, regNo,mobileNo,photo);
        mref.child(UID).setValue(studentAccount, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    showSnack("Account Created succefully");
                    clearUi();

                }else {
                    showSnack("An error has occured while"+databaseError.getMessage());
                }

            }
        });


    }
    private void showSnack(String msg) {
        Snackbar.make(binding.layout, msg, Snackbar.LENGTH_LONG).show();
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.creatingAccount));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void clearUi(){
        binding.edStudentName.setText("");
        binding.edEmailAddress.setText("");
        binding.edMobileNo.setText("");
        binding.edRegNo.setText("");
        binding.edPassword.setText("");
    }

}
