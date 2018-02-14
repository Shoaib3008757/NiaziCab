package rdm.niazicab;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends BaseActvitvityForDrawer {


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    RelativeLayout rl_tv_pickuplocation, rl_tv_dropofflocation;
    RelativeLayout rl_tv_pickupdate, rl_tv_pickuptime;
    TextView tv_pickuplocation, tv_dropoff;
    TextView tv_pickupdate, tv_pickuptime;

    RelativeLayout rl_et_name, rl_et_phone;
    EditText et_name, et_phone;

    RelativeLayout rl_tv_submit;

    int indecator = -1;
    Spinner sp_car_brand, sp_car_type;
    RelativeLayout rl_sp_car_type;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private int myHour, myMinut, myday, myMonth, myYear;

    private LatLng latlngPickup;
    private LatLng latlngDropoff;

    String orderTosend = null;

    ProgressBar progress_bar;
    String userName = null;
    String phone = null;
    String userPickupLocation = null;
    String userDropoffLocation = null;
    String userPickuplatlng = null;
    String userDropoffLatlng = null;
    String userPickDate = null;
    String userPickTime = null;
    String userDistance = null;
    String userFare = null;
    String userCarType = null;

    AlarmManager alarmManager;
    private PendingIntent pendingIntentForNotification;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        /*PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());
                Log.i("TAG", "Coordenate: " + place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

*/

        init();
        getPickupLocation();
        getDropofflocation();
        onCarBrandselect();
        carNameSelectListener();
        selectDate();
        selectTime();
        setingCursorVisible();

        submitclickListener();

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(MainActivity.this ,R.color.colorGreen)));
        /*getSupportActionBar().setTitle(R.string.about_us);
        getSupportActionBar().setIcon(R.drawable.guard_about);*/


    }

    public void init(){
        rl_tv_pickuplocation = (RelativeLayout) findViewById(R.id.rl_tv_pickuplocation);
        rl_tv_dropofflocation = (RelativeLayout) findViewById(R.id.rl_tv_dropofflocation);
        tv_pickuplocation = (TextView) findViewById(R.id.tv_pickuplocation);
        tv_dropoff = (TextView) findViewById(R.id.tv_dropoff);

        sp_car_brand = (Spinner) findViewById(R.id.sp_car_brand);
        sp_car_type = (Spinner) findViewById(R.id.sp_car_car_type);
        rl_sp_car_type = (RelativeLayout) findViewById(R.id.rl_sp_car_type);

        rl_tv_pickupdate = (RelativeLayout) findViewById(R.id.rl_tv_pickupdate);
        rl_tv_pickuptime = (RelativeLayout) findViewById(R.id.rl_tv_pickuptime);
        tv_pickupdate = (TextView) findViewById(R.id.tv_pickupdate);
        tv_pickuptime = (TextView) findViewById(R.id.tv_pickuptime);

        rl_et_name = (RelativeLayout) findViewById(R.id.rl_et_name);
        rl_et_phone = (RelativeLayout) findViewById(R.id.rl_et_phone);

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_name.setFocusableInTouchMode(true);
        et_phone.setFocusableInTouchMode(true);
        et_name.setSingleLine(true);

        rl_tv_submit = (RelativeLayout) findViewById(R.id.rl_tv_submit);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent notificiationIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        notificiationIntent.putExtra("alarmId", 0);

        pendingIntentForNotification = PendingIntent.getBroadcast(MainActivity.this, 0, notificiationIntent, 0);







    }//end of init

    public void getPickupLocation(){

        rl_tv_pickuplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indecator = 0;
                callingLocationDialog();

            }
        });
    }//end of getting pickup location

    public void getDropofflocation(){

        rl_tv_dropofflocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indecator = 1;
                callingLocationDialog();

            }
        });
    }//end of getting dropoff location


    public void callingLocationDialog(){


        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(new LatLngBounds(new LatLng(23.695,  68.149), new LatLng(35.88250, 76.51333)))//south and north latlong bourdy for pakistan
                            .build(MainActivity.this);




            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    //selectlister for barnd
    public void onCarBrandselect() {

        sp_car_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView) sp_car_brand.getSelectedView()).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

                if (i==0){
                    //do nothing

                    sp_car_type.setVisibility(View.GONE);
                    rl_sp_car_type.setVisibility(View.GONE);

                }

                if (i==1){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.suzuki, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for audi

                if (i==2){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.honda, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for bmw


                if (i==3){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.toyota, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for chvrolet

                if (i==4){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.daewoo, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for daewoo

                if (i==5){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.daihatsu, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for adihatsu

                if (i==6){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.fiat, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for fiat


                if (i==7){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.bmw, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for honda

                if (i==8){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.hyundai, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for hyundai

                if (i==9){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.jaguar, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for jaguar

                if (i==10){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.jeep, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for jeep

                if (i==11){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.kia, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for kia

                if (i==12){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.land_rover, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for land rover

                if (i==13){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.mercedes_benz, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for mercedess

                if (i==14){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.chvrolet, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for audi

                if (i==15){

                    sp_car_type.setVisibility(View.VISIBLE);
                    rl_sp_car_type.setVisibility(View.VISIBLE);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.audi, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_car_type.setAdapter(adapter);

                }//for audi

            }//end of item clicklistener



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }//end of spiner select listner

    public void carNameSelectListener(){

        sp_car_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView) sp_car_type.getSelectedView()).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void selectDate(){

        rl_tv_pickupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                myday = dayOfMonth;
                                myMonth = monthOfYear + 1;
                                myYear = year;
                                tv_pickupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                tv_pickupdate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()- 1000); // for setting set start form current date

                datePickerDialog.show();


            }
        });
    }//end of select date

    public void selectTime(){

        rl_tv_pickuptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {


                                myMinut = minute;
                                myHour = hourOfDay;

                                //tv_pickuptime.setText(hourOfDay + ":" + minute);


                                String hourString = "";
                                if(hourOfDay < 12) {
                                    hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                } else {
                                    hourString = (hourOfDay - 12) < 10 ? "0"+(hourOfDay - 12) : ""+(hourOfDay - 12);
                                }
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                //String secondString = second < 10 ? "0"+second : ""+second;
                                String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                                String time = hourString+":"+minuteString + " " + am_pm;

                                tv_pickuptime.setText(time);
                                tv_pickuptime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));




                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("TAg", "the code is result: " + resultCode);
        Log.e("TAg", "the code is resquest: " + requestCode);
        Log.e("TAg", "the code is Intent: " + data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String plceName = place.getName().toString();
                String plceAddress = place.getAddress().toString();
                LatLng latlng = place.getLatLng();
                Log.i("TAG", "Place: 123" + place.getName());

                Log.i("TAG", "Place: " + place.getAddress());
                Log.i("TAG", "Place Coordinates: " + place.getLatLng());
                
                if (indecator==0){
                    
                    tv_pickuplocation.setText(plceAddress);
                    tv_pickuplocation.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    latlngPickup =latlng;
                }
                else if(indecator==1){
                    tv_dropoff.setText(plceAddress);
                    tv_dropoff.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

                    latlngDropoff = latlng;
                }
                
                
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }//end of onActivity Result


    public void setingCursorVisible(){

        rl_et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_name.setCursorVisible(true);
            }
        });

        rl_et_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_phone.setCursorVisible(true);
            }
        });
    }


    public void submitclickListener(){

        rl_tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String pickLocation = tv_pickuplocation.getText().toString();
                final String drobOffLocation  = tv_dropoff.getText().toString();
                final String pickupDate = tv_pickupdate.getText().toString();
                final String pickupTime = tv_pickuptime.getText().toString();
                final String name = et_name.getText().toString();
                final String phone = et_phone.getText().toString();

                if (pickLocation.length()==0){

                    Toast.makeText(MainActivity.this, "Please Select your Pickup Location", Toast.LENGTH_SHORT).show();
                }
                else if (drobOffLocation.length()==0){

                    Toast.makeText(MainActivity.this, "Please Select your Dropoff Location", Toast.LENGTH_SHORT).show();
                }
                else if (pickupDate.length()==0){

                    Toast.makeText(MainActivity.this, "Please Select your Pickup Date", Toast.LENGTH_SHORT).show();
                }
                else if (pickupTime.length()==0){

                    Toast.makeText(MainActivity.this, "Please Select your Pickup Time", Toast.LENGTH_SHORT).show();
                }
                else if (name.length()==0){

                    Toast.makeText(MainActivity.this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
                }
                else if (phone.length()==0){

                    Toast.makeText(MainActivity.this, "Please Enter Your Phone No.", Toast.LENGTH_SHORT).show();
                }
                else if (phone.length()<10){

                    Toast.makeText(MainActivity.this, "Please Enter Valid Phone No", Toast.LENGTH_SHORT).show();
                }
                else if (sp_car_brand.getSelectedItemPosition() == 0){
                    Toast.makeText(MainActivity.this, "Please Select Vahicle Brand", Toast.LENGTH_SHORT).show();
                }
                else if (sp_car_type.getSelectedItemPosition()==0){
                    Toast.makeText(MainActivity.this, "Please Select Vahicle Type", Toast.LENGTH_SHORT).show();
                }
                else if (isNetworkAvailable()==false){
                    Toast.makeText(MainActivity.this, "No Internet Connection found", Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.i("TAG", "The Pickup LatLng: " + latlngPickup);
                    Log.i("TAG", "The Droboff LatLng: " + latlngDropoff);

                    Double selctedMarketTime =  SphericalUtil.computeDistanceBetween(latlngPickup, latlngDropoff);
                    Log.i("TAG", "The distance is: " + selctedMarketTime);
                    double estimateDriveTime = selctedMarketTime/1000;
                    double aa =  round(estimateDriveTime, 1);
                    int distace = ((int)aa) + 4;
                    Log.i("TAG", "The distance is: " + distace);



                    //for calculating time

                    double esTime = distace/11;
                    Log.i("TAG", "The time is: " + esTime);
                    double timeInMinuts = esTime/60;

                    Log.i("TAG", "The timeinminut is: " + esTime);
                    double timeroudn = round(timeInMinuts, 0);
                    Double d = new Double(timeroudn);
                    int totalTime = d.intValue();


                    String mdistance = distace + " km";
                    String mFare =  "Rs." + distace*20;

                    userName = name;
                    userPickupLocation = pickLocation;
                    userDropoffLocation = drobOffLocation;
                    userPickuplatlng = latlngPickup.toString();
                    userDropoffLatlng = latlngDropoff.toString();
                    userPickDate = pickupDate;
                    userPickTime = pickupTime;
                    userDistance = mdistance;
                    userFare = mFare;
                    userCarType = sp_car_brand.getSelectedItem().toString() + " " + sp_car_type.getSelectedItem().toString();

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("The Estimated Fare");
                    dialog.setMessage("The Total Distance is: " + distace + " Km" +
                           // "\n" + "The Total Estimate Traveling Time: " + totalTime + "minuts" +
                            "\n" + "The Total Estimated Fare is: " + "Rs." + (distace*20));

                    dialog.setPositiveButton("Book Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String textToSend = "Name: " + name
                                    + " \n" + "Phone: " + phone
                                    + " \n" + "Car Type: " + sp_car_brand.getSelectedItem().toString() + " " + sp_car_type.getSelectedItem().toString()
                                    + " \n" + "Pick Address: " + pickLocation
                                    + " \n" + "Pickup Date: " + pickupDate
                                    + " \n" + "Pickup time: " + pickupTime
                                    + " \n" + "Dropoff Location: " + drobOffLocation
                                    + " \n" + "Pickup : " + latlngPickup
                                    + " \n" + "Dropoff : " + latlngDropoff;

                            //send mail here
                           /* Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto","shoaib.ranglerz@gmail.com", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cab Booking Order");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
                            startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), 0);*/

                            orderTosend = textToSend;
                            new SendEmail().execute();

                        }
                    });

                    dialog.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });

                    dialog.show();
                }


            }
        });
    }
    //rouding double
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    //sending email

    public class SendEmail extends AsyncTask<Void,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          progress_bar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            String data = "test";
            try {

                Mail sender = new Mail("emailhere", "passwordhere");
                // sender.addAttachment(Environment.getExternalStorageDirectory()+Imagepath);
                sender.sendMail("New Booking Order",
                        orderTosend,
                        "senderhemailhere",
                        "receiveremailhere");


            } catch (Exception e) {
                Log.d("tag", "Exception Occur" + e.toString());
            }
            return data;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String data) {
            Log.e("tag", "Post Excute Data is " + data);
            progress_bar.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(),"Mail Sent Successfully",Toast.LENGTH_SHORT).show();

            DbHelper helper = new DbHelper();
            MyDatabase db = new MyDatabase(MainActivity.this);
            helper.setPickupaddress(userPickupLocation);
            helper.setDroboffaddress(userDropoffLocation);
            helper.setPickuplatlng(userPickuplatlng);
            helper.setDrobofflatlng(userDropoffLatlng);;
            helper.setPickupdate(userPickDate);
            helper.setPickuptime(userPickTime);
            helper.setEstimateddistance(userDistance);
            helper.setEstimatedfare(userFare);
            helper.setCarType(userCarType);

           long result =  db.insertDatatoDb(helper);
            if (result > -1){
                Log.i("TAG", "Data Inserted to db");

                //setting Notification Intent
                Log.e("TAG", "The alram Hhours: " + myHour);
                Log.e("TAG", "The alram Minut: " + myMinut);
                Log.e("TAG", "The alram year: " + myYear);
                Log.e("TAG", "The alram month: " + myMonth);
                Log.e("TAG", "The alram day: " + myday);

                startAlarmForMorning(myHour, myMinut, myYear, myMonth, myday);
            }



            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Order Submitted Sucessfully");
            alert.setMessage("Thank you " + userName + "! Your Order Has been successfully sumitted, Our Agent Will receive you from your provided location at the time and date you provide " + "\n" + "You also can view your traveling route");
            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.setPositiveButton("Show Map", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    Intent mapActivity = new Intent(MainActivity.this, MapsActivity.class);


                    mapActivity.putExtra("pickuplatlng", latlngPickup);
                    mapActivity.putExtra("dropofflatlng", latlngDropoff);
                    mapActivity.putExtra("pickupaddress", userPickupLocation);
                    mapActivity.putExtra("droboffaddress", userDropoffLocation);
                    startActivity(mapActivity);

                    //showing google map rout here
                }
            });

            alert.show();

           // finish();
        }
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startAlarmForMorning(int timeHour, int timeMinute, int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Log.e("TAG", "Year: " + year);
        Log.e("TAG", "month: " + month);
        Log.e("TAG", "DAY: " + day);
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        Log.e("TAG", "TimeHousr: " + timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentForNotification);

    }




}
