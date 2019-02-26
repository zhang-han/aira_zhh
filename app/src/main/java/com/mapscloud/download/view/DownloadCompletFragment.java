package com.mapscloud.download.view;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.mapscloud.download.bean.TileGridBean;
import com.mapscloud.download.tools.CommonAdapter;
import com.mapscloud.download.tools.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mapscloud8 on 2018/12/26.
 */

public class DownloadCompletFragment extends Fragment {
    private FragmentActivity mContext;
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

        adapter = new CommonAdapter<DownloadEntity>(mContext, data, R.layout.item_download_complet_listview) {
            @Override
            public void setViewData(CommonViewHolder commonViewHolder, View currentView, final DownloadEntity item, final int position) {
                TextView tv_downloaded_item_name = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloaded_item_name);
                TextView tv_downloaded_item_filesize = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloaded_item_filesize);
                TextView tv_downloaded_item_show = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloaded_item_show);
                TextView tv_downloaded_item_delete = (TextView) commonViewHolder.get(commonViewHolder, currentView, R.id.tv_downloaded_item_delete);

                tv_downloaded_item_filesize.setText(item.getConvertFileSize());
                tv_downloaded_item_name.setText(DownloadTileManager.getInstance().subStringLastButOne(item.getUrl()));
                tv_downloaded_item_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TileGridBean bean = DownloadTileManager.getInstance().DownloadEntity2TileGridBean(item);
                        if(bean != null){
                            DownloadTileManager.getInstance().addPolyTile(bean);
                            mContext.finish();
                        } else {
                            Toast.makeText(getActivity(), "该文件不支持查看功能!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tv_downloaded_item_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        DownLoadInfoEntity.delete(data.get(position));
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
//                for(DownLoadInfoEntity entity : list){
//                    if(entity.getState() == -1 || entity.getState() == 1 || entity.getState() == 7){
//
//                    } else {
//                        data.add(entity);
//                    }
//                }
//            }
            List<DownloadEntity> list = Aria.download(getActivity()).getAllCompleteTask();
            if(list != null && list.size() > 0)
                data.addAll(list);
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
        }
        @Override
        public void taskStart(DownloadTask task) {
        }
        @Override
        public void taskResume(DownloadTask task) {
        }
        @Override
        public void taskStop(DownloadTask task) {
        }
        @Override
        public void taskCancel(DownloadTask task) {
        }
        @Override
        public void taskFail(DownloadTask task) {
        }
        @Override
        public void taskComplete(DownloadTask task) {
            disposeStatu(task, 1);
        }
        @Override
        public void taskRunning(DownloadTask task) {
        }
    };

    public void disposeStatu(DownloadTask task, int i){
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
