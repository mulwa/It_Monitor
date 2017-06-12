package cj_server.com.itmonitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cj_server.com.itmonitor.Pojo.AttachmentDetails;
import cj_server.com.itmonitor.Pojo.StudentAccount;
import cj_server.com.itmonitor.databinding.ReturnFormBinding;

import static cj_server.com.itmonitor.Constants.STUDENT_ACC_NODE_NAME;

public class ReturnForm extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private ReturnFormBinding binding;
    private GoogleApiClient mGoogleClient;
    private static final String TAG = "connection";
    private double mlatitude, mlongitude;
    private LocationRequest mlocationRequest;
    private ProgressDialog mProgress;
    private String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=-1.1007117,37.01185&key=AIzaSyDFJIna_nweabquq-hfLg6e8fX4EpgY93M";
    private String address;
    private String place_id;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String UID;
    private  String mSelectedClass;
    private String mCurrentStudentReg;
    private ProgressDialog mProgressDialog;
    private String mCurrentStudentName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_return_form);
//        setContentView(R.layout.activity_return_form);

        database =FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        checks if a student has created account and loged in
        getCurrentUser();
//        get student class from intent

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            mSelectedClass = bundle.getString(Constants.SELECTED_CLASS);

        }
        setSupportActionBar(binding.included.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attachment Details");
        }
        buildGoogleAPIClient();

//        getting student registrationNumber
        DatabaseReference regNoRef = database.getReference(STUDENT_ACC_NODE_NAME).child(UID);
        regNoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StudentAccount currentAccount = dataSnapshot.getValue(StudentAccount.class);
                mCurrentStudentReg = currentAccount.getRegno();
                mCurrentStudentName = currentAccount.getName();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getUser:onCancelled", databaseError.toException());

            }
        });



        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if (validateData()) {
                    showDialog();
                    Log.d(Constants.TAG,"validation Done");
                    if(mCurrentStudentReg != null && mSelectedClass !=null && address != null){
                        DatabaseReference mref =  database.getReference(Constants.ATTACHMENT_DETAILS).child(mSelectedClass);
                        AttachmentDetails attachmentDetails = new AttachmentDetails(mCurrentStudentName,mCurrentStudentReg,binding.edOrganizationName.getText().toString().trim(),binding.edBuildingName.getText().toString(),
                                binding.edBranch.getText().toString(),binding.edFloorNo.getText().toString(),binding.edTel.getText().toString(),binding.edDate.getText().toString()
                                ,address,place_id,mlatitude,mlongitude);

                        mref.child(mCurrentStudentReg).setValue(attachmentDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                hideDialog();
                                if(databaseError == null){
                                    clearUi();
                                    showSnack("Your attachment Details has been saved");

                                }else{
                                    hideDialog();
                                    showSnack("Infromation not saved" + databaseError.getMessage());
                                    Log.d(Constants.TAG,databaseError.getMessage());
                                }

                            }
                        });

                    }else{
                        hideDialog();
                        showSnack("Your Registration Number or Your  Location(enable Location) is Missing");

                    }


                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
            mGoogleClient.connect();

        if(!isLocationEnable() && !isNetworkAvailable()){
            showSnack("Please you  need  to enable location and Internet");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleClient, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(binding.edOrganizationName.getText())) {
            showSnack("Please Enter Organization Name");
            requestFocus(binding.edOrganizationName);
            return false;
        }

        if(TextUtils.isEmpty(binding.edBranch.getText())){
            showSnack("please privide branch or fill None");
            requestFocus(binding.edBranch);
            return false;
        }
        if(TextUtils.isEmpty(binding.edFloorNo.getText())){
            showSnack("Enter office floor number  or enter 0 if not Appplicable");
            requestFocus(binding.edFloorNo);
            return false;
        }
        if (!TextUtils.isDigitsOnly(binding.edFloorNo.getText())){
            showSnack("Floor number must be a number");
            requestFocus(binding.edFloorNo);
            return false;
        }
        if (TextUtils.isEmpty(binding.edDate.getText())){
            showSnack("fill in the date you started your attachment");
            requestFocus(binding.edDate);
            return  false;
        }

        return true;
    }
    private void clearUi(){
        binding.edOrganizationName.setText("");
        binding.edBranch.setText("");
        binding.edDate.setText("");
        binding.edFloorNo.setText("");
        binding.edTel.setText("");
        binding.edBuildingName.setText("");
    }

    private void showSnack(String msg) {
        Snackbar.make(binding.layout, msg, Snackbar.LENGTH_LONG).show();
    }

    protected synchronized void buildGoogleAPIClient() {
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    public void fetchCurrentLocation(){
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(200000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mlocationRequest,this );


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fetchCurrentLocation();

    }


    @Override
    public void onConnectionSuspended(int i) {
        showSnack("location  connecttion suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnack("locatiion connection failed");

    }

    @Override
    public void onLocationChanged(Location location) {
        mlatitude = location.getLatitude();
        mlongitude = location.getLongitude();
        Log.d(TAG,location.getLatitude()+" current  longitude  is"+location.getLongitude());
        fetchAddress(mlatitude,mlongitude);



    }


    public void fetchAddress(double lat , double log){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +  lat +"," + log +"&key=AIzaSyDFJIna_nweabquq-hfLg6e8fX4EpgY93M", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            place_id = response.getJSONArray("results").getJSONObject(0).getString("place_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,address);
                        if(address != null){
                            binding.tvLocation.setText(address);
                            binding.ibuttonLocation.setBackgroundResource(R.drawable.location_on);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    try {
                        Toast.makeText(getApplicationContext(),
                                "Network Error. Try Again Later",
                                Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException npe) {
                        System.err.println(npe);
                    }
                } else if (error instanceof ServerError) {
                    try {
                        Toast.makeText(
                                getApplicationContext(),
                                "Problem Connecting to Server. Try Again Later",
                                Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException npe) {
                        System.err.println(npe);
                    }
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    try {
                        Toast.makeText(getApplicationContext(),
                                "No Connection", Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException npe) {
                        System.err.println(npe);
                    }
                } else if (error instanceof TimeoutError) {
                    try {
                        Toast.makeText(
                                getApplicationContext().getApplicationContext(),
                                "Timeout Error. Try Again Later",
                                Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException npe) {
                        System.err.println(npe);
                    }
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }

        };

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjectRequest,
                "tag");


    }
//    private void showProgress() {
//        mProgress = ProgressDialog.show(this, "Please Wait",
//                "Accessing server...");
//    }
//
//    private void stopProgress() {
//        mProgress.cancel();
//    }


    private boolean isLocationEnable(){
        LocationManager mlocationManager;
        boolean gps_enabled = false;
         boolean network_enabled = false;
        mlocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        network_enabled = mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps_enabled && !network_enabled) {
            // notify user
            showSnack("You need to Activate location services to get your location");
            return false;
        }
        return true;

    }
    private void getCurrentUser(){
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null && mFirebaseUser.isEmailVerified()){
            UID = mFirebaseUser.getUid();

        }else {
            showSnack("Please  Create an Account First and Verify Email before filling the  return form ");
            startActivity(new Intent(getApplicationContext(),SignUp.class));

        }
    }
    private void showDialog(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.creatingAccount));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }
    private void hideDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }


}
