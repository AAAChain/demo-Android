package org.aaa.chain.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeger.library.StatusBarUtil;
import org.aaa.chain.R;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView back;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.view_title_bar_bg), 0);
        getViewById();
        back = $(R.id.tv_title_bar_back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }

    public void setTitleName(String titleName) {
        ((TextView) $(R.id.tv_title_bar_title)).setText(titleName);
    }

    public Drawable getResourceDrawable(@DrawableRes int resID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resID, null);
        } else {
            return getResources().getDrawable(resID);
        }
    }

    protected void startActivity(Class<?> targetActivityClass, Bundle bundle) {
        Intent intent = new Intent(this, targetActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(Class<?> targetActivityClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, targetActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    protected void showToast(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public abstract int initLayout();

    public abstract void getViewById();
}
