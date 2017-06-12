package cj_server.com.itmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import cj_server.com.itmonitor.Pojo.Events;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
//    private ImageView imageThumbnail;
    private CircleImageView circleImageView;
    private TextView  tvUserEmail;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CoordinatorLayout mcoordinatorLayout;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ProgressBar mProgresBar;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Upcomming Events");
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase =  FirebaseDatabase.getInstance();

//        setting Recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recylerviewList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);

        getEvents();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.isEmailVerified()){
                        updateUi(user.getEmail());
                        Log.d("photo:","from Main "+user.getDisplayName());
                    }else {
                        Snackbar.make(mcoordinatorLayout,"You need to Verify your  Email",Snackbar.LENGTH_LONG).show();
                    }

                }

            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
//        imageThumbnail = (ImageView) headerView.findViewById(R.id.profile_image);
//        circleImageView = (CircleImageView) headerView.findViewById(R.id.profile_image);
        tvUserEmail = (TextView) headerView.findViewById(R.id.tvEmail);


        navigationView.setNavigationItemSelectedListener(this);

        if(!isNetworkAvailable()){
            Snackbar.make(mcoordinatorLayout, "No Internet Connection Available ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", null).show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(authStateListener !=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar snackbar =  Snackbar.make(mcoordinatorLayout,getString(R.string.exit_string),Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           finish();

                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.events) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "events commin soon", Toast.LENGTH_SHORT).show();
        }else if(id== R.id.news){
            Toast.makeText(getApplicationContext(), "Department News Comming", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.returnform) {
            startActivity(new Intent(this, AvaliableClasses.class));

        }else if(id == R.id.locateClassmate){
            startActivity(new Intent(this, StudentMapsActivity.class));

        } else if (id == R.id.signUp) {
            startActivity(new Intent(this,SignUp.class));
        }else if(id ==  R.id.login){
           startActivity(new Intent(this,Login.class));

        }else if(id==R.id.log_out){
            FirebaseAuth.getInstance().signOut();
        }else if(id==R.id.composeLog){
            startActivity(new Intent(this,StudentLogs.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void updateUi(String email){
        tvUserEmail.setText(email);

    }
    private void getEvents(){
        mRef = mDatabase.getReference(Constants.EVENTS_NODE);
        Log.d("data",mRef.toString());
        FirebaseRecyclerAdapter<Events, myViewHolder> eventAdapter = new FirebaseRecyclerAdapter<Events, myViewHolder>(
                Events.class,
                R.layout.event_custom_layout,
                myViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(myViewHolder viewHolder, final Events model, int position) {
                mProgresBar.setVisibility(View.INVISIBLE);
                viewHolder.mEventName.setText(model.getEventName());
                viewHolder.mEventDiscription.setText(model.getEventDescription());
                viewHolder.mEventDate.setText(model.getEventDate());

                viewHolder.mlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),more_details_activity.class);
                        intent.putExtra(Constants.EVENTNAME, model.getEventName());
                        intent.putExtra(Constants.EVENTDESCRIPTION,model.getEventDescription());
                        intent.putExtra(Constants.EVENTVENUE, model.getEventVenue());
                        intent.putExtra(Constants.EVENTDATE, model.getEventDate());
                        intent.putExtra(Constants.EVENTTIME, model.getEventTime());
                        intent.putExtra(Constants.EVENTTARGET, model.getEventTarget());
                        startActivity(intent);


                    }
                });



            }
        };
        mRecyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();


    }
    public static class  myViewHolder  extends RecyclerView.ViewHolder{
        private TextView mEventName;;
        private TextView mEventDiscription;
        private TextView mEventDate;
        private RelativeLayout mlayout;


        public myViewHolder(View itemView) {
            super(itemView);
            mEventName = (TextView) itemView.findViewById(R.id.ed_eventTitle);
            mEventDiscription =  (TextView)  itemView.findViewById(R.id.event_Desc);
            mEventDate =  (TextView)  itemView.findViewById(R.id.ed_date);
            mlayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);

        }
    }
}
