package cj_server.com.itmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cj_server.com.itmonitor.databinding.LoginClass;

public class Login extends BaseActivity {
    private LoginClass loginBinding;
    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser  mcurrentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(loginBinding.included.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign in");
        }

        loginBinding.chShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    loginBinding.edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }else {
                    loginBinding.edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });
        loginBinding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if(validate()){
                    signInUser(loginBinding.edEmailAddress.getText().toString(),loginBinding.edPassword.getText().toString());
                }
            }
        });

        loginBinding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUp.class));
            }
        });
        loginBinding.edForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnack(loginBinding.layout,"Password Recovery Comming soon");
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();

                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        if(authStateListener !=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
        super.onResume();
    }

    private boolean validate(){
        if(TextUtils.isEmpty(loginBinding.edEmailAddress.getText().toString())){
            showSnack(loginBinding.layout,"Please Enter email Adress");
            requestFocus(loginBinding.edEmailAddress);
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(loginBinding.edEmailAddress.getText().toString()).matches()){
            showSnack(loginBinding.layout,"Please Enter a Valid Email Address");
            requestFocus(loginBinding.edEmailAddress);
            return false;
        }
        if(TextUtils.isEmpty(loginBinding.edPassword.getText().toString())){
            showSnack(loginBinding.layout,"Please Enter Your Password");
            requestFocus(loginBinding.edPassword);

        }
        return true;
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.authenticating));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void signInUser(String email, String password){
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mcurrentUser = mAuth.getCurrentUser();
                    showSnack(loginBinding.layout, "Successfully Logged in"+mcurrentUser.getEmail() +mcurrentUser.getPhotoUrl());

                                
                    if(mcurrentUser.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    }else{
                        showSnack(loginBinding.layout,"Please Verify Your Email First");
                        clearUi();
                    }



                }else {
//                    logging user failled
                    Log.d("Login", task.getException().toString());
                    showSnack(loginBinding.layout, task.getException().getMessage());
                }
                hideProgressDialog();

            }
        });
    }
    private void clearUi(){
        loginBinding.edPassword.setText("");
        loginBinding.edEmailAddress.setText("");
    }
}
