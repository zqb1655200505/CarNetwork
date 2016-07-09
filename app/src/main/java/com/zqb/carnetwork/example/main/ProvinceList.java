package com.zqb.carnetwork.example.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.ProvinceInfoJson;
import com.zqb.carnetwork.R;

import java.util.ArrayList;
import java.util.List;

public class ProvinceList extends AppCompatActivity {

    private ListView lv_list;
    private ProvinceListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_list);

        lv_list = (ListView) findViewById(R.id.province_list);
        mAdapter=new ProvinceListAdapter(this,getData2());
        lv_list.setAdapter(mAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView province= (TextView) view.findViewById(R.id.txt_name);
                Intent intent=new Intent(ProvinceList.this,CityList.class);
                intent.putExtra("province_name",province.getText().toString());
                intent.putExtra("province_id",province.getTag().toString());
                startActivityForResult(intent,20);
            }
        });
    }


    private List<ListModel> getData2() {

        List<ListModel> list = new ArrayList<ListModel>();
        List<ProvinceInfoJson> provinceList = WeizhangClient.getAllProvince();

        for (ProvinceInfoJson provinceInfoJson : provinceList) {
            String provinceName = provinceInfoJson.getProvinceName();
            int provinceId = provinceInfoJson.getProvinceId();
            ListModel model = new ListModel();
            model.setTextName(provinceName);
            model.setNameId(provinceId);
            list.add(model);
        }
        return list;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data==null)
        {
            //Toast.makeText(this,"city返回为空",Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle=data.getExtras();
        String city_name=bundle.getString("city_name");
        String city_id=bundle.getString("city_id");
        //Toast.makeText(this,city_name+city_id,Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        intent.putExtra("city_name",city_name);
        intent.putExtra("city_id",city_id);
        setResult(1,intent);
        finish();
    }

}
