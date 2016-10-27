package com.jimei.k3wise_mobile.Component;

import android.support.v4.app.Fragment;
import android.os.Bundle;

/**
 * Created by lee on 2016/9/27.
 */

public abstract  class HandledFragment extends Fragment {

    public boolean onBackPressed(){
        return true;
    }

    public void popFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
