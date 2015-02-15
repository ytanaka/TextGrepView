package textgrep.ytanaka.github.io.textgrep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static String readString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString(), e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void showMsgBox(Context context, String msg) {
        showOKCancelMsgBox(context, msg, null, false);
    }
    @SuppressWarnings("UnusedDeclaration")
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
