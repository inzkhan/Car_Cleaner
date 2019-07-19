package khan.inzi.car_wash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        setupToolBar(R.string.developer_name);
        if(getSupportActionBar() != null ){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    public void contactMe(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getResources().getString(R.string.developer_email), null));
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.contact_us)));
    }





}
