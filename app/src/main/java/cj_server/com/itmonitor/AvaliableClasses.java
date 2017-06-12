package cj_server.com.itmonitor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cj_server.com.itmonitor.Adapters.classAdapter;
import cj_server.com.itmonitor.Pojo.NewClass;
import cj_server.com.itmonitor.databinding.availableClasses;


public class AvaliableClasses extends BaseActivity {
    private availableClasses  binding;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    private DatabaseReference classRef;
    private ArrayList<NewClass> classArrayList = new ArrayList<>();
    private classAdapter adapter;
    public String myclass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_avaliable_classes);
        database = FirebaseDatabase.getInstance();
        classRef = database.getReference().child("It-classes");
//        initializing firebaseauth
        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(binding.included.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Available Classes");
        }
        fetchData();

        binding.recylerviewList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        NewClass newClass = classArrayList.get(position);
                        myclass  = newClass.getClassName();
                        Log.d("me",newClass.getClassName());
                       Intent formIntent = new Intent(getApplicationContext(),ReturnForm.class);
                        formIntent.putExtra(Constants.SELECTED_CLASS,newClass.getClassName());
                        startActivity(formIntent);


                    }
                })
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
        }else {
//            Toast.makeText(getApplicationContext(),"User Aready signed"+currentUser.getEmail()+ "Photo url"+currentUser.getPhotoUrl(),Toast.LENGTH_SHORT).show();
//            Log.d("photo","Current user info:"+currentUser.getProviderData());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.available_clas_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected_item = item.getItemId();
        if(selected_item == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setRecylerView(){
        binding.recylerviewList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//        addding vertical lines
        binding.recylerviewList.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));


    }
    private void fetchData(){
        classRef = database.getReference().child("It-classes");
        classRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getAvailableclasses(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAvailableclasses(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       setRecylerView();
    }
    private void getAvailableclasses(DataSnapshot dataSnapshot){
        NewClass newClass = dataSnapshot.getValue(NewClass.class);
        classArrayList.add(newClass);
        adapter = new classAdapter(classArrayList);
        binding.recylerviewList.setAdapter(adapter);
        binding.progressBar.setVisibility(View.GONE);
        setRecylerView();
    }



}

