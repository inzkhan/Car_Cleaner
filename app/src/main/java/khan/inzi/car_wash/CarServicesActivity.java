package khan.inzi.car_wash;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CarServicesActivity extends BaseActivity {

    private ArrayList<String> serviceList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        setupToolBar(R.string.select_service);
        final ListView listView = findViewById(R.id.serviceList);
        final Button nextButton = findViewById(R.id.nextBtn);

        serviceList =  new ArrayList<>();
        ArrayAdapter<CharSequence> listAdatper = ArrayAdapter.createFromResource(this,R.array.services_list,android.R.layout.simple_list_item_multiple_choice);

        listView.setAdapter(listAdatper);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String item = adapterView.getItemAtPosition(i).toString();
               serviceList.add(item);

           }
       });

       nextButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               goToMainActivity();


           }
       });




    }

    private void goToMainActivity(){

        Intent sendDataIntent = new Intent(CarServicesActivity.this,MainActivity.class);
        sendDataIntent.putStringArrayListExtra("list",serviceList);
        if(serviceList.isEmpty()){
            Toast.makeText(CarServicesActivity.this, getResources().getString(R.string.select_one_service), Toast.LENGTH_LONG).show();
        }else{
            startActivity(sendDataIntent);
            finish();

        }
    }



}
