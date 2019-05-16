package com.shizhenbao.interfaceCall;

import android.support.annotation.NonNull;

/**
 * Created by dell on 2017/9/4.
 */

public interface DirectoryChooserInteraction {

    void onSelectDirectorys(@NonNull String path);

    void onCancelChoosers();

}
