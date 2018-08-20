package com.xdja.anymore.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by anymore on 2018/8/18.
 */
public class VoiceInputDialog extends Dialog {

    public VoiceInputDialog(@NonNull Context context) {
        super(context);
    }

    public VoiceInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VoiceInputDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

}
