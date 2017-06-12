package cj_server.com.itmonitor;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cj_server.com.itmonitor.Pojo.AttachmentDetails;
import cj_server.com.itmonitor.databinding.studentMap;

public class StudentMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public  final int ERROR_CODE = 100;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mclassRef;
    private String MselectedClas = "May2018";
    private final float ZOOM_VALUE = 15;
    private studentMap mapBinding;
    private View view;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView MrecyclerView;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checking if map services is available
        if(servicesOk()){
//            setContentView(R.layout.activity_student_maps);
            mapBinding = DataBindingUtil.setContentView(this,R.layout.activity_student_maps);
            setSupportActionBar(mapBinding.included.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Nearby Classmate");
            }


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            view = findViewById(R.id.comment_buttom_sheet);
            bottomSheetBehavior = BottomSheetBehavior.from(view);
            bottomSheetBehavior.setPeekHeight(100);
            MrecyclerView = (RecyclerView) findViewById(R.id.Rv_student_list);
            MrecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            MrecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL ));

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        remove method parameter
        getData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_map_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.search){
            Toast.makeText(getApplicationContext(), "search not available ",Toast.LENGTH_LONG).show();
        }else if(itemId == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public boolean servicesOk(){
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog  = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_CODE);
            dialog.show();
        }else{
            Toast.makeText(this,"Can not Connect to mapping services",Toast.LENGTH_LONG).show();
        }
        return false;
    }
    private void getData(){
        mclassRef = mDatabase.getReference(Constants.ATTACHMENT_DETAILS).child(MselectedClas);
        mclassRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.TAG, dataSnapshot.toString());
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    AttachmentDetails attachmentDetails =  snapshot.getValue(AttachmentDetails.class);
                    // Adding a marker in all student location and move the camera
                    LatLng area = new LatLng(attachmentDetails.getLat(),attachmentDetails.getLng());
                    mMap.addMarker(new MarkerOptions().position(area).title(attachmentDetails.getStudentName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(area,ZOOM_VALUE));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<AttachmentDetails,myViewHolder> listAdapter = new FirebaseRecyclerAdapter<AttachmentDetails, myViewHolder>(
                AttachmentDetails.class,
                R.layout.student_list_custom_layout,
                myViewHolder.class,
                mclassRef

        ) {
            @Override
            protected void populateViewHolder(myViewHolder viewHolder, final AttachmentDetails model, int position) {
                viewHolder.tvStudentName.setText(model.getStudentName());
                viewHolder.tvOrganizationName.setText(model.getOrganizationName());
                viewHolder.tvMobileNo.setText(model.getMobileNumber());
                viewHolder.imCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Calling "+model.getMobileNumber(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        };
        MrecyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();


    }
    public static class myViewHolder extends RecyclerView.ViewHolder{
        private TextView tvStudentName;
        private TextView tvOrganizationName;
        private TextView tvMobileNo;
        private ImageButton imCall;


        public myViewHolder(View itemView) {
            super(itemView);
            tvStudentName = (TextView) itemView.findViewById(R.id.ed_student_name);
            tvOrganizationName = (TextView) itemView.findViewById(R.id.ed_organization_name);
            tvMobileNo = (TextView) itemView.findViewById(R.id.ed_mobile_no);
            imCall = (ImageButton) itemView.findViewById(R.id.ib_call);

        }
    }

}
