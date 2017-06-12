package cj_server.com.itmonitor;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import cj_server.com.itmonitor.Pojo.StudentAccount;
import cj_server.com.itmonitor.Pojo.StudentLog;
import cj_server.com.itmonitor.databinding.ComposeClass;

public class composeLog extends BaseActivity {
    private ComposeClass binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthLister;
    private String mUID;
    private DatabaseReference mRef;
    private String mregNO;
    private Date toDate ;
    private SimpleDateFormat dateFormat;
    private  String dNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_compose_log);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toDate = new Date();
        dateFormat = new SimpleDateFormat("E yyyy:MM.dd");
       dNow = dateFormat.format(toDate).toString();

        setSupportActionBar(binding.included.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Day Log");
        }

        mAuthLister = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if(mUser != null){
                    mUID = mUser.getUid();
                    Toast.makeText(getApplicationContext(),"User is logged in getting reg NO",Toast.LENGTH_SHORT).show();
                    getRegNo(mUID);
                }else{
                    Toast.makeText(getApplicationContext(),"User Not Available",Toast.LENGTH_SHORT).show();
                }

            }
        };
        binding.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                saveLog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthLister);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuthLister != null){
            mAuth.removeAuthStateListener(mAuthLister);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_log,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int  id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        if(id ==R.id.sendLog){
            saveLog();

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLog(){
        showProgressDialog("Please Wait Saving You Log");
        DatabaseReference logRef = database.getReference(Constants.LOG_NODE).child(mregNO).push();
        StudentLog studentLog = new StudentLog(binding.edSummary.getText().toString(), binding.edDescription.getText().toString(),dNow);
        logRef.setValue(studentLog, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if(databaseError == null){
                    showSnack(binding.layout,"Your Today Log has been Saved Successfully");
                    clearUI();
                }else {
                    showSnack(binding.layout,"Errror: Not Saved"+databaseError.getMessage());
                }

            }
        });


    }
    private void getRegNo(String uid){
        mRef = database.getReference(Constants.STUDENT_ACC_NODE_NAME).child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StudentAccount account = dataSnapshot.getValue(StudentAccount.class);
                mregNO = account.getRegno();
                Toast.makeText(getApplicationContext(),"Registration no Found"+mregNO,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG, "getUser:onCancelled", databaseError.toException());

            }
        });
    }
    private void clearUI(){
        binding.edDescription.setText("");
        binding.edSummary.setText("");
    }
}
