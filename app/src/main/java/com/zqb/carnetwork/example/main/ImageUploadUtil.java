package com.zqb.carnetwork.example.main;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zqb on 2016/5/16.
 */
public class ImageUploadUtil {
    private static ImageUploadUtil imageUploadUtil;
    private static final String boundary=UUID.randomUUID().toString();//边界标识，随机生成
    private static final String end="\r\n";
    private static final String twoHyphens="--";
    private static final String content_type="multipart/form-data";
    private ImageUploadUtil()
    {

    }
    /**
     * 单例模式获取上传工具类
     * @return
     */
    public static ImageUploadUtil getInstance(){
        if(null== imageUploadUtil)
        {
            imageUploadUtil =new ImageUploadUtil();
        }
        return imageUploadUtil;
    }
    private int readTimeOut=10*1000;//读取超时
    private int connectTimeOut=10*1000;//链接超时
    private static final String charset="utf-8";//设置编码格式

    /**
     * android上传文件到服务器
     *
     * @param filePath
     * 需要上传的文件的路径
     * @param fileKey
     * 在网页上<input type=file name=xxx/> xxx就是这里的fileKey
     * @param RequestURL
     * 请求的URL
     */
    private String path=null;
    public void uploadFile(String filePath, final String fileKey,
                           final String RequestURL, final Map<String,String>param)
    {
        path=filePath;
        if(null==filePath)
        {
            Log.i("FILE_ERROR","文件路径为空");
            return;
        }
        Log.i("filePath",filePath);
        final File file=new File(filePath);
        if(file==null||(!file.exists()))
        {
            Log.i("FILE_ERROR","文件不存在");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                toUploadFile(file,fileKey,RequestURL,param);
            }
        }).start();
    }
    private void toUploadFile(File file, String fileKey, String RequestURL,Map<String,String>param){
        try {
            URL url=new URL(RequestURL);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);
            conn.setConnectTimeout(connectTimeOut);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", charset); // 设置编码
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("user-agent", "WinHttpClient");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            DataOutputStream dataOutputStream=new DataOutputStream(conn.getOutputStream());
            /***
             * 以下是用于上传参数
             */
//            StringBuffer sb1=new StringBuffer();
//            String params = "";
//            if (param != null && param.size() > 0)
//            {
//                Iterator<String> it = param.keySet().iterator();
//                while (it.hasNext())
//                {
//                    String key = it.next();
//                    String value = param.get(key);
//                    Log.i("key=",key);
//                    Log.i("value=",value);
//                    sb1.append(twoHyphens).append(boundary).append(end);
//                    sb1.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(end).append(end);
//                    sb1.append(value).append(end);
//                    params = sb1.toString();
//                    //Log.i("params", key + "=" + params + "##");
//                    dataOutputStream.write(params.getBytes());
//                }
//            }

            /**
             * 当文件不为空，把文件打包上传
             * 这里重点注意：
             * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的   比如:abc.png
             */
            StringBuffer sb=new StringBuffer();
            sb.append(twoHyphens);
            sb.append(boundary);
            sb.append(end);
            System.out.println(boundary);
            //Log.i("path",path);
            String fileName=Firstpage.getUsername()+file.getName().substring(file.getName().lastIndexOf("."));
            sb.append("Content-Disposition:form-data; name=\"" + fileKey+ "\"; filename=\"" + fileName + "\""+end);
            sb.append("Content-Type:image/jpg" + end); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append(end);

            dataOutputStream.write(sb.toString().getBytes());

            /**
             * 上传文件
             */
            FileInputStream inputStream=new FileInputStream(file);
            byte[] bytes=new byte[1024];//规定每次写入1024 bytes
            int len=-1;
            while((len=inputStream.read(bytes))!=-1)
            {
                dataOutputStream.write(bytes,0,len);
            }
            inputStream.close();
            dataOutputStream.write(end.getBytes());
            byte[] end_data=(twoHyphens+boundary+twoHyphens+end).getBytes();
            dataOutputStream.write(end_data);
            dataOutputStream.flush();
            /**
             * 获取响应码  200=成功
             * 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            if(res==200)
            {
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String result = br.readLine();
                Log.i("result",result);
                is.close();
            }
            dataOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}

