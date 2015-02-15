package textgrep.ytanaka.github.io.textgrep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString(), e);
        }
    }

    public static void showMsgBox(Context context, String msg) {
        showOKCancelMsgBox(context, msg, null, false);
    }
    public static void showOKCancelMsgBox(Context context, String msg, final Runnable okHandler) {
        showOKCancelMsgBox(context, msg, okHandler, true);
    }
    public static void showOKCancelMsgBox(Context context, String msg, final Runnable okHandler, boolean showCancelButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (okHandler != null) okHandler.run();
            }
        });
        if (showCancelButton) {
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                /* */
                }
            });
        }
        builder.setCancelable(true);
        builder.create().show();
    }
}
