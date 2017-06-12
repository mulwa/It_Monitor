package cj_server.com.itmonitor;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import cj_server.com.itmonitor.databinding.MoreEventBinding;

public class more_details_activity extends AppCompatActivity {
    private Bundle bundle;
    private MoreEventBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this, R.layout.activity_more_details_activity);
        setSupportActionBar(binding.included.toolbar);

        bundle = getIntent().getExtras();
        if(bundle != null){

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(bundle.getString(Constants.EVENTNAME));
            }
            binding.eventName.setText(bundle.getString(Constants.EVENTNAME));
            binding.eventDescrition.setText(bundle.getString(Constants.EVENTDESCRIPTION));
            binding.eventVenue.setText(bundle.getString(Constants.EVENTVENUE));
            binding.eventDate.setText(bundle.getString(Constants.EVENTDATE));
            binding.eventTime.setText(bundle.getString(Constants.EVENTTIME));
            binding.edTarget.setText(bundle.getString(Constants.EVENTTARGET));

        }//end bundle check
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
