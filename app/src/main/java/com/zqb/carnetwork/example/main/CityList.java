package com.zqb.carnetwork.example.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;
import com.zqb.carnetwork.R;

import java.util.ArrayList;
import java.util.List;

public class CityList extends AppCompatActivity {

    private ListView lv_list;
    private CityListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        lv_list= (ListView) findViewById(R.id.list_city);
        Bundle bundle=getIntent().getExtras();
        String province_name=bundle.getString("province_name");
        String province_id=bundle.getString("province_id");
        mAdapter=new CityListAdapter(this,getData(province_id));
        lv_list.setAdapter(mAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView city= (TextView) view.findViewById(R.id.city_name);
                Intent intent=new Intent();
                intent.putExtra("city_name",city.getText().toString());
                intent.putExtra("city_id",city.getTag().toString());
                setResult(20,intent);
                finish();
            }
        });
    }


    private List<ListModel> getData(String provinceId) {
        List<ListModel> list = new ArrayList<ListModel>();

        List<CityInfoJson> cityList = WeizhangClient.getCitys(Integer
                .parseInt(provinceId));

        for (CityInfoJson cityInfoJson : cityList) {
            String cityName = cityInfoJson.getCity_name();
            int cityId = cityInfoJson.getCity_id();

            ListModel model = new ListModel();
            model.setNameId(cityId);
            model.setTextName(cityName);
            list.add(model);
        }

        return list;
    }
}
