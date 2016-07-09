package com.zqb.carnetwork.example.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zqb.carnetwork.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class RuleBreakResult extends AppCompatActivity {

    private ResultAdapter resultAdapter;
    private List<MyRuleBreakResultItem>mData;
    private TextView tv_result;
    private ListView result_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_break_result);
        Bundle bundle=getIntent().getExtras();
        String result_json=bundle.getString("result");
        result_list= (ListView) findViewById(R.id.result_list);
        tv_result= (TextView) findViewById(R.id.result);
        //tv_result.setText(result_json);
        try {
            JSONObject jsonObject=new JSONObject(result_json);
            int status=jsonObject.optInt("status");
            switch (status)
            {
                case 2000://正常，无违章记录
                {
                    tv_result.setText("恭喜您暂无违章记录");
                    Toast.makeText(RuleBreakResult.this,"恭喜您暂无违章记录",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 2001://正常（有违章记录）
                {
                    mData=new LinkedList<MyRuleBreakResultItem>();
                    int total_scole=jsonObject.optInt("total_scole");
                    String total_money=jsonObject.optInt("total_money")+"";
                    int count=jsonObject.optInt("count");
                    tv_result.setText("共违章 "+count+" 次，"+"共扣分 "+total_scole+" 分");
                    JSONArray jsonArray=jsonObject.optJSONArray("historys");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object=jsonArray.optJSONObject(i);
                        String occur_date=object.optString("occur_date");
                        String occur_area=object.optString("occur_area");
                        String infor=object.optString("info");
                        mData.add(new MyRuleBreakResultItem(total_money,occur_area,occur_date,infor));
                    }
                    resultAdapter=new ResultAdapter(getApplicationContext(),mData);
                    result_list.setAdapter(resultAdapter);
                    break;
                }
                case 5000://请求超时，请稍后重试
                {
                    Toast.makeText(RuleBreakResult.this,"请求超时，请稍后重试",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 5001://交管局系统连线忙碌中，请稍后再试
                {
                    Toast.makeText(RuleBreakResult.this,"交管局系统连线忙碌中，请稍后再试",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 5002://恭喜，当前城市交管局暂无您的违章记录
                {
                    Toast.makeText(RuleBreakResult.this,"恭喜，当前城市交管局暂无您的违章记录",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 5005://车辆查询数量超过限制
                {
                    Toast.makeText(RuleBreakResult.this,"车辆查询数量超过限制",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
