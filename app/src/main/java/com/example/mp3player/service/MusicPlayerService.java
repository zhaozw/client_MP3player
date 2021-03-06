package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by DissoCapB on 2017/1/19.
 */

public class MusicPlayerService extends Service {
    MediaPlayer player=new MediaPlayer();
    private List<String> audioList = null;
    private int listPosition=-1;
    private final IBinder binder=new ServiceBinder();
    String text="000";


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Binding MusicService1", Toast.LENGTH_SHORT).show();
        load();
        if (audioList.size()>0) {
            listPosition = new Random().nextInt(audioList.size());
            initMediaPlayer();
        }
        //播放完成事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                player.seekTo(0);
                next();
            }
        });

        //出错事件
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        return binder;
    }

    public class ServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }





    public String getConnect(){
        Toast.makeText(getApplicationContext(), "connect success!", Toast.LENGTH_SHORT).show();
        return text;
    }

    //设置新列表
    public void setNewMusic(int position,List<String> list){
        listPosition=position;
        audioList=list;
        save();
        start();

    }

    public boolean isPlaying(){
        return  player.isPlaying();
    }
    //返回列表
    public List<String> getAudioList(){
        List<String> list=new ArrayList<String>();
        for(int i=0;i<audioList.size();i++){
            String s1[] =audioList.get(i).split("/");
            list.add(s1[s1.length-1]);
        }
        return list;
    }
    //返回正在播放音乐所在列表位置
    public int getPlayingListPosition(){
        return listPosition;
    }
    //当前播放位置
    public int getCurrentPosition(){
            return player.getCurrentPosition();
    }
    //总长度
    public int getDuration(){
        return player.getDuration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /////////////////////////////////播放方法↓///////////////////////////////////////
    private void initMediaPlayer(){             //初始化
        try {
            File file=new File(audioList.get(listPosition));
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        player.reset();
        if (listPosition >= 0) {
            initMediaPlayer();
            play();
        }
    }
    public void start(int position){
        listPosition=position;
        player.reset();
        if (listPosition >= 0) {
            initMediaPlayer();
            play();
        }
    }
    public void stop(){
        player.reset();
        listPosition=-1;
    }

    public void playOrPause(){
        if(player.isPlaying())
            pause();
        else
            play();
    }

    public void play() {
        if (listPosition >= 0)
            player.start();
    }

    public void pause() {
        if (listPosition >= 0)
            player.pause();
    }
    public void next(){
        if (listPosition>=0){
            listPosition =
                    listPosition >= audioList.size() - 1 ?
                            0 : listPosition + 1;
            player.reset();
            initMediaPlayer();
            play();
        }
    }
    public void back(){
        if (listPosition>=0){
            listPosition =
                    listPosition <= 0 ?
                            audioList.size() - 1 : listPosition - 1;
            player.reset();
            initMediaPlayer();
            play();
        }
    }

    public void seekTo(int i){
        player.seekTo(i);
    }



    /////////////////////////////////播放方法↑///////////////////////////////////////

    /////////////////////////////////正在播放音乐列表路径储存↓///////////////////////////////////////

    public void save(){							//写入文件
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("playingMusicData", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            for(int i=0;i<audioList.size();i++){

                writer.write(audioList.get(i)+"###");
                writer.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void load(){
        listPosition=-1;
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        audioList = new ArrayList<String>();
        try{
            in=openFileInput("playingMusicData");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
            String s1[] =content.toString().split("###");
            for(int c=0;c<s1.length;c++) {
                audioList.add(s1[c]);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /////////////////////////////////正在播放音乐列表路径储存↑///////////////////////////////////////
}
