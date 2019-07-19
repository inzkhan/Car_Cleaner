package khan.inzi.car_wash;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {


    public void setupToolBar(int title){

        Toolbar toolbar = findViewById(R.id.toolbar3);
        toolbar.setTitle(title);
        toolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(toolbar);

    }
}
