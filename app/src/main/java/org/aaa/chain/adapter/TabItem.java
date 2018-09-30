package org.aaa.chain.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.aaa.chain.R;

public class TabItem {

    private int imageNormal;
    private int imagePress;
    //tab的名字
    private int title;
    private String titleString;

    private Context context;

    //tab对应的fragment
    private Class<? extends Fragment> fragmentClass;

    public View view;
    private ImageView imageView;
    private TextView textView;

    public TabItem(Context context, int imageNormal, int imagePress, int title, Class<? extends Fragment> fragmentClass) {
        this.context = context;
        this.imageNormal = imageNormal;
        this.imagePress = imagePress;
        this.title = title;
        this.fragmentClass = fragmentClass;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public int getImageNormal() {
        return imageNormal;
    }

    public int getImagePress() {
        return imagePress;
    }

    public int getTitle() {
        return title;
    }

    public String getTitleString() {
        if (title == 0) {
            return "";
        }
        if (TextUtils.isEmpty(titleString)) {
            titleString = context.getString(title);
        }
        return titleString;
    }

    public View getView() {
        if (this.view == null) {
            this.view = LayoutInflater.from(context).inflate(R.layout.tab_view_indicator, null);
            this.imageView = (ImageView) this.view.findViewById(R.id.tab_iv_image);
            this.textView = (TextView) this.view.findViewById(R.id.tab_tv_text);
            if (this.title == 0) {
                this.textView.setVisibility(View.GONE);
            } else {
                this.textView.setVisibility(View.VISIBLE);
                this.textView.setText(getTitleString());
            }
            this.imageView.setImageResource(imageNormal);
        }
        return this.view;
    }

    //切换tab的方法
    public void setChecked(boolean isChecked) {
        if (imageView != null) {
            if (isChecked) {
                Drawable drawable = ContextCompat.getDrawable(context, imagePress);
                //Drawable wrappedDrawable =
                //        DrawableCompat.wrap(drawable.getConstantState() == null ? drawable : drawable.getConstantState().newDrawable().mutate());
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
                DrawableCompat.setTint(wrappedDrawable, context.getResources().getColor(R.color.main_bottom_text_select));
                imageView.setImageDrawable(wrappedDrawable);
            } else {
                imageView.setImageResource(imageNormal);
            }
        }
        if (textView != null && title != 0) {
            if (isChecked) {
                textView.setTextColor(context.getResources().getColor(R.color.main_bottom_text_select));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.tab_color));
            }
        }
    }
}
