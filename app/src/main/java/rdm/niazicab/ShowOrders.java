package rdm.niazicab;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import rdm.niazicab.Adapters.OrderListAdapter;

public class ShowOrders extends AppCompatActivity {


    RecyclerView recylerview;
    OrderListAdapter mAdapter;
    TextView tv_text_no_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        init();
        getDatabaseData();

    }

    public void init(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ShowOrders.this ,R.color.colorGreen)));
        recylerview = (RecyclerView) findViewById(R.id.recylerview);
        tv_text_no_order = (TextView) findViewById(R.id.tv_text_no_order);

    }

    public void getDatabaseData(){

        MyDatabase db = new MyDatabase(ShowOrders.this);

        ArrayList<DbHelper> datalist = new ArrayList<>();
        datalist = db.getOrders();


        if (datalist.isEmpty()){
            tv_text_no_order.setVisibility(View.VISIBLE);
        }


        mAdapter = new OrderListAdapter(ShowOrders.this, datalist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ShowOrders.this, LinearLayoutManager.VERTICAL, false);


        recylerview.setLayoutManager(horizontalLayoutManagaer);
        recylerview.setItemAnimator(new DefaultItemAnimator());
        recylerview.setAdapter(mAdapter);


    }
}
