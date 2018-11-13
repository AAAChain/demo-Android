package org.aaa.chain;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import java.util.List;
import org.aaa.chain.entities.KeyInfoEntity;
import org.aaa.chain.entities.SearchResponseEntity;

public class ChainApplication extends Application {

    private RefWatcher refWatcher;
    private static ChainApplication instance;

    private SearchResponseEntity searchResponseEntity;

    private List<KeyInfoEntity> keyInfoEntityList;

    public static ChainApplication getInstance() {
        return instance;
    }

    @Override public void onCreate() {
        super.onCreate();

        instance = this;

        refWatcher = setupLeakCanary();
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

    public void addKeyInfoEntity(KeyInfoEntity entity){
        keyInfoEntityList.add(entity);
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        ChainApplication leakApplication = (ChainApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }
}
