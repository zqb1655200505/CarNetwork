package com.zqb.carnetwork.example.main;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zqb on 2016/4/16.
 */
public class MusicService extends Service {
    private static MediaPlayer mediaPlayer;// 创建一个媒体播放器的对象
    private int current=0;
    private int state=ConstUtil.STATE_NON;//当前播放状态
    private ContentResolver contentResolver;
    private MusicLoader musicLoader;
    private List<MusicInfo> list;
    private MusicSercieReceiver receiver;
    private int flag=1;
    @Override
    public void onCreate() {
        super.onCreate();
        //注册接收器
        receiver=new MusicSercieReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConstUtil.MUSICSERVICE_ACTION);
        registerReceiver(receiver, filter);
        //加载播放列表
        contentResolver=this.getContentResolver();
        musicLoader=MusicLoader.instance(contentResolver);
        list=musicLoader.getMusicInfoList();
        mediaPlayer=new MediaPlayer();
        //为mediaPlayer的完成事件创建监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current++;
                prepareAndPlay(current);
            }
        });
    }
    /**
     * 装载和播放音乐
     * @param index int index 播放第几首音乐的索引
     * */
    protected void prepareAndPlay(int index) {

        if(index==list.size())
        {
            index=current=0;
        }
        if(index==-1)
        {
            index=current=list.size()-1;
        }
        //发送广播停止,前台Activity更新界面
        Intent intent=new Intent();
        intent.putExtra("current", index);
        flag=1;
        intent.putExtra("isplaying",flag);
        intent.setAction(ConstUtil.MUSICBOX_ACTION);
        sendBroadcast(intent);

        //播放音乐
        String path=list.get(index).getUrl();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.reset();//初始化mediaPlayer对象
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();//准备播放音乐
            mediaPlayer.start();//播放音乐
            state=ConstUtil.STATE_PLAY;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //进入app就播放音乐
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prepareAndPlay(0);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("你停止了service");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(receiver);
        return super.onUnbind(intent);
    }

    //创建广播接收器用于接收前台Activity发去的广播
    class MusicSercieReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int control=intent.getIntExtra("control", -1);//若不成功，返回-1
            int mcurrent=intent.getIntExtra("position",-1);
            if(mcurrent!=current)//点击了列表
            {
                current=mcurrent;
                prepareAndPlay(current);
            }
            else
            {
                switch (control)
                {
                    case ConstUtil.STATE_PLAY://播放音乐
                    {
                        if (state == ConstUtil.STATE_PAUSE) //如果原来状态是暂停
                        {
                            mediaPlayer.start();
                            //发送广播停止,前台Activity更新界面
                            Intent intent1=new Intent();
                            intent1.putExtra("current", current);
                            flag=1;
                            intent1.putExtra("isplaying",flag);
                            intent1.setAction(ConstUtil.MUSICBOX_ACTION);
                            sendBroadcast(intent1);
                            state = ConstUtil.STATE_PLAY;
                        }
                        else
                        {
                            prepareAndPlay(current);
                            state = ConstUtil.STATE_PLAY;
                        }
                        break;
                    }
                    case ConstUtil.STATE_PAUSE://暂停播放
                        if (state==ConstUtil.STATE_PLAY)
                        {
                            mediaPlayer.pause();
                            //发送广播停止,前台Activity更新界面
                            Intent intent2=new Intent();
                            intent2.putExtra("current", current);
                            flag=0;
                            intent2.putExtra("isplaying",flag);
                            intent2.setAction(ConstUtil.MUSICBOX_ACTION);
                            sendBroadcast(intent2);
                            state=ConstUtil.STATE_PAUSE;
                        }
                        break;
                    case ConstUtil.STATE_PREVIOUS://上一首
                        prepareAndPlay(--current);
                        state=ConstUtil.STATE_PLAY;
                        break;
                    case ConstUtil.STATE_NEXT://下一首
                        prepareAndPlay(++current);
                        state=ConstUtil.STATE_PLAY;
                        break;
                    case ConstUtil.STATE_STOP:
                        mediaPlayer.pause();
                        //发送广播停止,前台Activity更新界面
                        Intent intent3=new Intent();
                        intent3.putExtra("current", current);
                        flag=0;
                        intent3.putExtra("isplaying",flag);
                        intent3.setAction(ConstUtil.MUSICBOX_ACTION);
                        sendBroadcast(intent3);
                        onDestroy();
                    default:
                        break;
                }
            }
        }
    }
}
