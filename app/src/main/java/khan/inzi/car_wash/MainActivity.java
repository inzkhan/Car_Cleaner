package khan.inzi.car_wash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.ArrayList;



public class MainActivity extends BaseActivity {

    private static final String SMS_SENT = "SMS_SENT";
    private String  selectedService  = "";
    private String  clientName = "";
    private String  clientPhoneNumber = "";
    private static ProgressDialog progressDialog;
    private static final int SMS_REQUEST_CODE = 101;
    private EditText clientText;
    private EditText clientNumber;
    private StringBuffer stringBuffer = new StringBuffer();




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Name",clientName);
        outState.putString("Number",clientPhoneNumber);
        outState.putString("Service",selectedService);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedService = savedInstanceState.getString("Service");
        clientName = savedInstanceState.getString("Name");
        clientPhoneNumber = savedInstanceState.getString("Phone");

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolBar(R.string.app_name);
        clientText = findViewById(R.id.nameEditText);
        clientNumber = findViewById(R.id.mobileEditText);
        Button sendSmsBtn = findViewById(R.id.smsButton);
        Button serviceBtn = findViewById(R.id.serviceBtn);

        fetchAndArrangeData();
        checkPermissionOnRunTime();
        setUpProgressDialog();

        registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT));

         serviceBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 goToServiceActivity();
             }
         });

        sendSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionOnRunTime();
                validateFieldsAndSendSms();



            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.dev_info:
                clearFields();
                Intent contactIntent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(contactIntent);
                return true;
            case R.id.license:
                 displayLicensesAlertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToServiceActivity(){
        Intent goToServiceActivity = new Intent(MainActivity.this,CarServicesActivity.class);
        startActivity(goToServiceActivity);
        finish();



    }
    private void validateFieldsAndSendSms(){


        clientName = clientText.getText().toString();
        clientPhoneNumber = clientNumber.getText().toString();




        if(!clientName.isEmpty() && clientPhoneNumber.length() == 11  && !(selectedService.isEmpty())){

            String phoneNumber =  getResources().getString(R.string.phone);
            String phoneNumber2 = getResources().getString(R.string.phone2);
            String text = "Name: "+clientName+"\nMobileNo: "+clientPhoneNumber+"\nService : Price"+selectedService;



            sendSMS(phoneNumber,text);
            sendSMS(phoneNumber2,text);
            progressDialog.show();

        }else{
            if(clientName.isEmpty()){
                makeToast("Name can't be empty!");
            }
            if(selectedService.isEmpty()){
                makeToast("Please select a service!");
            }
            if(clientPhoneNumber.length() != 11){
                makeToast("Mobile# can't be less than 11 digits");
            }


        }
    }
    private void setUpProgressDialog(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending.....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    private void displayLicensesAlertDialog() {

        @SuppressLint("InflateParams") WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok,null)
                .show();

    }

    private void checkPermissionOnRunTime(){

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();

        }
    }

    private void fetchAndArrangeData(){

        Intent getData = getIntent();

        ArrayList<String> list = getData.getStringArrayListExtra("list");

        for(int i = 0; i < list.size(); i++){

            String[] info = list.get(i).split(":");
            selectedService = info[0];
            String servicePrice = info[1];
            if(servicePrice.contains("Price")){
                servicePrice = servicePrice.replaceAll("\\bPrice\\b", "");
            }
            if(servicePrice.contains("based")){
                servicePrice = servicePrice.replaceAll("\\bbased\\b", "Based");
            }

            stringBuffer.append("\n");
            stringBuffer.append(selectedService);
            stringBuffer.append(" : ");
            stringBuffer.append(servicePrice);

        }
        selectedService = stringBuffer.toString();
    }


    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_REQUEST_CODE) {
            if (grantResults.length == 0
                    || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }
    private void clearFields(){
        clientName = "";
        clientPhoneNumber = "";
        selectedService = "";


        clientText.setText("");
        clientNumber.setText("");

    }


    public void sendSMS(String phoneNo,String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<PendingIntent> pendingSendIntents = new ArrayList<>();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            for(int i=0; i < parts.size(); i++){
                pendingSendIntents.add(PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0));

            }


            if(isSimExist()){
                smsManager.sendMultipartTextMessage(phoneNo, null, parts, pendingSendIntents,null);

                }else{
                makeToast("NO SIM!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isSimExist() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = 0;
        if (telephonyManager != null) {

            SIM_STATE = telephonyManager.getSimState();
        }

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY){

            return true;
        }

        else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    makeToast("SIM is absent");
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    break;
            }
            return false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT));
    }


    @Override
    protected void onPause(){
        super.onPause();

        if(smsSentReceiver != null){
            unregisterReceiver(smsSentReceiver);
        }

    }




    private BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String messageSentInfo = null;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    messageSentInfo = "Message sent!";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    messageSentInfo = "Error. Message not sent.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    messageSentInfo = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    messageSentInfo = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    messageSentInfo = "Error: Radio off.";
                    break;
                case SmsManager.RESULT_ERROR_LIMIT_EXCEEDED:
                    messageSentInfo = "Error: Msg limit exceeded";
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                makeToast(messageSentInfo);
                clearFields();
                showRatingDialog();
            }



        }
    };

    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showRatingDialog(){
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .ratingBarBackgroundColor(R.color.accent)
                .ratingBarColor(R.color.black)
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.accent)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
    }



}
