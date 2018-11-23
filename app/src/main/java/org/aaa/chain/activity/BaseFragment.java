package org.aaa.chain.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private View view;
    public ResourceManagementActivity activity;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(initLayout(), container, false);
        getViewById();
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) view.findViewById(id);
    }

    public abstract int initLayout();

    public abstract void getViewById();

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResourceManagementActivity) {
            activity = (ResourceManagementActivity) context;
        }
    }
}
