package org.aaa.chain;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import com.igexin.sdk.message.GTNotificationMessage;
import java.util.List;
import org.aaa.chain.activity.ReceiveMsgListener;
import org.aaa.chain.entities.KeyInfoEntity;
import org.aaa.chain.entities.SearchResponseEntity;
import org.aaa.chain.utils.PreferenceUtils;

public class ChainApplication extends Application {

    private static ChainApplication instance;

    private SearchResponseEntity searchResponseEntity;

    private List<KeyInfoEntity> keyInfoEntityList;

    private static GetuiHandler handler;

    public static ChainApplication getInstance() {
        return instance;
    }

    public static String cid;
    public static GTNotificationMessage message;

    @Override public void onCreate() {
        super.onCreate();

        instance = this;

        PreferenceUtils.init(this);

        if (handler == null) {
            handler = new GetuiHandler();
        }
    }

    public SearchResponseEntity getBaseInfo() {
        return searchResponseEntity;
    }

    public void setBaseInfo(SearchResponseEntity entity) {
        searchResponseEntity = entity;
    }

    public void setKeyInfoEntity(List<KeyInfoEntity> entityList) {
        this.keyInfoEntityList = entityList;
    }

    public List<KeyInfoEntity> getKeyInfoEntity() {
        return keyInfoEntityList;
    }

    public void addKeyInfoEntity(KeyInfoEntity entity) {
        keyInfoEntityList.add(entity);
    }


    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static class GetuiHandler extends Handler {

        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    cid = (String) msg.obj;
                    break;
                case 1:
                    message = (GTNotificationMessage) msg.getData().getSerializable("msg");
                    listener.receiveMsg(message);
                    break;
            }
        }
    }

    static ReceiveMsgListener listener;
    public void setReceiveMsgListener(ReceiveMsgListener listener){
        ChainApplication.listener = listener;
    }
    public void removeReceiveMsgListener(){
        listener = null;
    }

}
