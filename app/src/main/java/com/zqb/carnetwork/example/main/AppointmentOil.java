package com.zqb.carnetwork.example.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;

public class AppointmentOil extends AppCompatActivity {

    private List<MyCarItem> myCarItems;
    private CarListItemAdapter carListItemAdapter;
    private ListView mListview;
    private RequestQueue mQueue;
    private final int[] ID=new int[100];
    private String[] car=new String[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_oil);
        mQueue= Volley.newRequestQueue(AppointmentOil.this);
        myCarItems=new LinkedList<MyCarItem>();
        mListview= (ListView) findViewById(R.id.carList);
        String username=Firstpage.getUsername();
        String Request=NetUrl.query_car+"?username="+username;
        JsonArrayRequest req = new JsonArrayRequest(Request, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++)
                {
                    JSONObject jsonObject=response.optJSONObject(i);
                    Log.i("jsonObject",jsonObject.toString());
                    String brand=jsonObject.optString("brand");
                    String type=jsonObject.optString("type");
                    String pic=jsonObject.optString("pic");
                    car[i]=brand+type;
                    int id=jsonObject.optInt("id");
                    ID[i]=id;
                    myCarItems.add(new MyCarItem(brand,type,pic,id));
                }
                carListItemAdapter=new CarListItemAdapter(myCarItems,getApplicationContext());
                mListview.setAdapter(carListItemAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mQueue.add(req);

        final Bundle bundle=getIntent().getExtras();
        final String from=bundle.getString("from");
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (from)
                {
                    case "appointment":
                    {
                        Intent intent=new Intent(AppointmentOil.this,AppointmentInfor.class);
                        intent.putExtra("flag",0);
                        intent.putExtra("car",car[position]);
                        intent.putExtra("car_id",ID[position]);
                        startActivity(intent);
                        break;
                    }
                    case "rulebreak":
                    {
                        Intent intent=new Intent(AppointmentOil.this,RulebreakQuery.class);
                        intent.putExtra("id",ID[position]);
                        startActivity(intent);
                        break;
                    }
                    case "gasstation":
                    {
                        Intent intent=new Intent(AppointmentOil.this,AppointmentInfor.class);
                        intent.putExtra("flag",1);
                        intent.putExtra("station_name",bundle.getString("station_name"));
                        intent.putExtra("car",car[position]);
                        intent.putExtra("car_id",ID[position]);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }

}
