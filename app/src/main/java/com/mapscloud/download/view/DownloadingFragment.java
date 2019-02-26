package com.mapscloud.download.view;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.mapscloud.download.DownloadTileManager;
import com.mapscloud.download.R;
import com.mapscloud.download.tools.CommonAdapter;
import com.mapscloud.download.tools.CommonViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mapscloud8 on 2018/12/26.
 */

public class DownloadingFragment extends Fragment {

    private Context mContext;
    private ListView listView;
    private List<DownloadEntity> data = new ArrayList<>();
    private CommonAdapter<DownloadEntity> adapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        registerCallBack();
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listView = new ListView(mContext);
        
        adapter = new CommonAdapter<DownloadEntity>(mContext, data, R.layout.item_downloading_listview) {
            @Override
            public void setViewData(CommonViewHolder commonViewHolder, View currentView, final DownloadEntity item, final int position) {
                TextView tv_downloading_name = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloading_name);
                TextView tv_download_filesize = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_download_filesize);
                final TextView tv_downloading_pause = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloading_pause);
                TextView tv_downloading_delete = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloading_delete);
                final HorizontalProgressBarWithNumber pgb_downloading = (HorizontalProgressBarWithNumber) commonViewHolder.get(commonViewHolder, currentView, R.id.pgb_downloading);
                
                tv_downloading_name.setText(DownloadTileManager.getInstance().subStringLastButOne(item.getUrl()));
                tv_download_filesize.setText(item.getConvertFileSize());
                long size = Aria.download(getActivity()).load(item.getUrl()).getFileSize();
                long progress = item.getCurrentProgress();
                int current = size == 0 ? 0 : (int) (progress * 100 / size);
                pgb_downloading.setProgress(current);

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "test";
                //读取，如果有则删除，没有则创建。
                File file = new File(path);
                if(file != null ){
                    if(file.exists()){
                        file.delete();
                    } else {
                        file.mkdir();
                    }
                }


                //下载状态 默认-1 完成1 停止2 等待3 正在执行4 预处理5 预处理完成6  删除7
                int state = item.getState();
                switch (state){
                    case -1:
                        tv_downloading_pause.setText("预处理");
                        break;
                    case 1:
                        tv_downloading_pause.setText("已完成");
                        break;
                    case 2:
                        tv_downloading_pause.setText("已停止");
                        break;
                    case 3:
                        tv_downloading_pause.setText("等待中");
                        break;
                    case 4:
                        tv_downloading_pause.setText("正在下载");
                        break;
                    case 5:
                        tv_downloading_pause.setText("连接中");
                        break;
                    case 6:
                        tv_downloading_pause.setText("连接中");
                        break;
                    case 7:
                        tv_downloading_pause.setText("已删除");
                        break;
                }


                tv_downloading_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int state = item.getState();
                        switch (state){
                            case -1:
                                tv_downloading_pause.setText("等待中");
                                break;
                            case 1:
                                tv_downloading_pause.setText("已完成");
                                break;
                            case 2:
                                tv_downloading_pause.setText("正在重试");
                                int color2 = Color.parseColor("#2774F4");
                                tv_downloading_pause.setTextColor(color2);
                                Aria.download(getActivity()).load(data.get(position).getUrl()).resume();
                                pgb_downloading.setmTextColor(color2);
                                pgb_downloading.setmReachedBarColor(color2);
                                break;
                            case 3:
                                tv_downloading_pause.setText("暂停");
                                int color3 = Color.parseColor("#2774F4");
                                tv_downloading_pause.setTextColor(color3);
                                Aria.download(getActivity()).load(data.get(position).getUrl()).stop();
                                pgb_downloading.setmTextColor(color3);
                                pgb_downloading.setmReachedBarColor(color3);
                                break;
                            case 4:
                                tv_downloading_pause.setText("暂停");
                                int color4 = Color.parseColor("#2774F4");
                                tv_downloading_pause.setTextColor(color4);
                                pgb_downloading.setmTextColor(color4);
                                pgb_downloading.setmReachedBarColor(color4);
                                Aria.download(getActivity()).load(data.get(position).getUrl()).stop();
                                break;
                            case 5:
                                tv_downloading_pause.setText("正在连接");
                                int color5 = Color.parseColor("#2774F4");
                                tv_downloading_pause.setTextColor(color5);
                                Aria.download(getActivity()).load(data.get(position).getUrl()).resume();
                                pgb_downloading.setmTextColor(color5);
                                pgb_downloading.setmReachedBarColor(color5);
                                break;
                            case 6:
                                tv_downloading_pause.setText("正在连接");
                                int color6 = Color.parseColor("#2774F4");
                                tv_downloading_pause.setTextColor(color6);
                                Aria.download(getActivity()).load(data.get(position).getUrl()).resume();
                                pgb_downloading.setmTextColor(color6);
                                pgb_downloading.setmReachedBarColor(color6);
                                break;
                            case 7:
                                tv_downloading_pause.setText("已删除");
                                break;
                        }

//                        String text = tv_downloading_pause.getText()+"";
//                        if(text.equals("暂停")){
//                            tv_downloading_pause.setText("开始");
//                            int color = Color.parseColor("#F34E33");
//                            tv_downloading_pause.setTextColor(color);
//                            pgb_downloading.setmTextColor(color);
//                            pgb_downloading.setmReachedBarColor(color);
//                            Aria.download(getActivity()).load(data.get(position).getUrl()).stop();
//                        }
//                        if(text.equals("开始")){
//                            tv_downloading_pause.setText("暂停");
//                            int color = Color.parseColor("#2774F4");
//                            tv_downloading_pause.setTextColor(color);
//                            Aria.download(getActivity()).load(data.get(position).getUrl()).resume();
//                            pgb_downloading.setmTextColor(color);
//                            pgb_downloading.setmReachedBarColor(color);
//                        }
                    }
                });

                tv_downloading_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Aria.download(getActivity()).load(data.get(position).getUrl()).cancel(true);
                        refreshData();
                    }
                });
                
            }
        };

        listView.setAdapter(adapter);

        return listView;
    }


    public void refreshData() {
        try{
            data.clear();
//            List<DownLoadInfoEntity> list = DownLoadInfoEntity.findAllData(DownLoadInfoEntity.class);
//            if(list != null && list.size() > 0){
//                for(DownloadEntity entity : list){
//                    if(entity.getState() == -1 || entity.getState() == 1 || entity.getState() == 7){
//
//                    } else {
//                        data.add(entity);
//                    }
//                }
//            }
            final List<DownloadEntity> list = Aria.download(getActivity()).getAllNotCompletTask();
            if(list != null && list.size() > 0){
                data.addAll(list);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(DownloadEntity entity : list){
                            if(entity.getState() != 3 && entity.getState() != 4){
                                Aria.download(getActivity()).load(entity.getUrl()).start();
                            }
                        }
                    }
                }).start();
            }

        } catch (Exception e){
            e.toString();
        }
        if(adapter != null)
            adapter.notifyDataSetChanged();


    }
    
    public void registerCallBack(){
        DownloadTileManager.getInstance().addDownLoadCallBack(downLoadCallBack);
    }

    DownloadTileManager.DownLoadCallBack downLoadCallBack = new DownloadTileManager.DownLoadCallBack() {
        @Override
        public void onPre(DownloadTask task) {
            disposeStatu(task, 5);
        }
        @Override
        public void taskStart(DownloadTask task) {
            disposeStatu(task, 4);
        }
        @Override
        public void taskResume(DownloadTask task) {
            disposeStatu(task, 6);
        }
        @Override
        public void taskStop(DownloadTask task) {
            disposeStatu(task, 2);
        }
        @Override
        public void taskCancel(DownloadTask task) {
            disposeStatu(task, 7);
        }
        @Override
        public void taskFail(DownloadTask task) {
            disposeStatu(task, -1);
        }
        @Override
        public void taskComplete(DownloadTask task) {
            disposeStatu(task, 1);
        }
        @Override
        public void taskRunning(DownloadTask task) {
            disposeStatu(task, 111);
        }
    };

    public void disposeStatu(DownloadTask task, int statu){
        Log.e("<<<<----", task.getDownloadEntity().getUrl() + "-------" + statu);
        boolean isContain = false;
        for(int i = 0; i < data.size(); i ++){
            if(task.getDownloadEntity().getUrl().equals(data.get(i).getUrl())) {
                if(statu == 1){
                    data.remove(i);
                } else if(statu == -1){
                    DownloadEntity entity = task.getDownloadEntity();
                    entity.setCurrentProgress(0);
                    data.set(i, entity);
                    Toast.makeText(getActivity(), DownloadTileManager.getInstance().subStringLastButOne(entity.getUrl()) + "下载失败！", Toast.LENGTH_SHORT).show();
                }else if(statu == 2){
                    DownloadEntity entity = task.getDownloadEntity();
                    Toast.makeText(getActivity(), DownloadTileManager.getInstance().subStringLastButOne(entity.getUrl()) + "停止下载！", Toast.LENGTH_SHORT).show();
                } else if(statu == 7){
                    DownloadEntity entity = task.getDownloadEntity();
                    Toast.makeText(getActivity(), DownloadTileManager.getInstance().subStringLastButOne(entity.getUrl()) + "取消下载！", Toast.LENGTH_SHORT).show();
                } else if(statu == 4){
                    DownloadEntity entity = task.getDownloadEntity();
                    Toast.makeText(getActivity(), DownloadTileManager.getInstance().subStringLastButOne(entity.getUrl()) + "开始下载！", Toast.LENGTH_SHORT).show();
                } else {
                    data.remove(i);
                    data.add(i, task.getDownloadEntity());
                }
//                if(statu == 111){
//                    data.remove(i);
////                    if(i == data.size())
//                        data.add(i, task.getDownloadEntity());
////                    else
////                        data.set(i, task.getDownloadEntity());
//                } else if(statu == 1 || statu == 7){
//                    data.remove(i);
//                }
                isContain = true;
                break;
            }
        }
        if(!isContain)
            data.add(task.getDownloadEntity());
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadTileManager.getInstance().removeDownLoadCallBack(downLoadCallBack);
    }
}
