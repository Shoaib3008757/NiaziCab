package rdm.niazicab.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import rdm.niazicab.DbHelper;
import rdm.niazicab.MapsActivity;
import rdm.niazicab.R;

/**
 * Created by User-10 on 20-Nov-17.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {


    private int lastPosition = -1;

    private Activity activity;
    private String[] data;
    ArrayList<DbHelper> contactList;
    private static LayoutInflater inflater=null;


    ProgressBar progressBar;

    private Bitmap image = null;
    boolean mExpanded;

    public OrderListAdapter(Activity a, ArrayList<DbHelper> contactList1) {
        activity = a;
        //data=d;
        this.contactList = contactList1;



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custome_order_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {




        //  imageLoader.DisplayImage(contactList.get(position).get("imageurl"), holder.imageView)

        String id = contactList.get(position).getId();
        String pickupaddress = contactList.get(position).getPickupaddress();
        String dropoffaddress = contactList.get(position).getDroboffaddress();
        String pickuplatlng = contactList.get(position).getPickuplatlng();
        String dropofflatlng = contactList.get(position).getDrobofflatlng();
        String date = contactList.get(position).getPickupdate();
        String time = contactList.get(position).getPickuptime();
        String distance = contactList.get(position).getEstimateddistance();
        String fare = contactList.get(position).getEstimatedfare();
        String cartype = contactList.get(position).getCarType();

        Log.i("TAG", "Value of Id: " + id);
        Log.i("TAG", "Value of pickupaddress: " + pickupaddress);
        Log.i("TAG", "Value of dropoffaddress: " + dropoffaddress);
        Log.i("TAG", "Value of pickuplatlng: " + pickuplatlng);
        Log.i("TAG", "Value of dropofflatlng: " + dropofflatlng);
        Log.i("TAG", "Value of date: " + date);
        Log.i("TAG", "Value of time: " + time);
        Log.i("TAG", "Value of distance: " + distance);
        Log.i("TAG", "Value of fare: " + fare);
        Log.i("TAG", "Value of cartype: " + cartype);


        holder.tv_pickupaddress.setText(pickupaddress);
        holder.tv_dropoffpaddress.setText(dropoffaddress);
        holder.tv_date.setText(date);
        holder.tv_time.setText(time);
        holder.tv_cartype.setText(cartype);
        holder.tv_distance.setText(distance);
        holder.tv_fare.setText(fare);
        int ordernumber = position+1;
        holder.tv_orderNumber.setText("Order Number: " + ordernumber);

        holder.tv_orderNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(activity, MapsActivity.class);
                activity.startActivity(i);
            }
        });





    }

    @Override
    public int getItemCount() {

        return contactList.size();


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_pickupaddress, tv_dropoffpaddress, tv_date, tv_time, tv_cartype,tv_distance,tv_fare, tv_orderNumber;


        public MyViewHolder(View view) {
            super(view);

            tv_pickupaddress = (TextView)view.findViewById(R.id.tv_pickupaddress);
            tv_dropoffpaddress = (TextView)view.findViewById(R.id.tv_dropoffpaddress);
            tv_date = (TextView)view.findViewById(R.id.tv_date);
            tv_time = (TextView)view.findViewById(R.id.tv_time);
            tv_cartype = (TextView)view.findViewById(R.id.tv_cartype);
            tv_distance = (TextView)view.findViewById(R.id.tv_distance);
            tv_fare = (TextView)view.findViewById(R.id.tv_fare);
            tv_orderNumber = (TextView)view.findViewById(R.id.tv_orderNumber);




        }
    }



}
