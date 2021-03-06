package textgrep.ytanaka.github.io.textgrep;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextData {
    private static final String TAG = TextData.class.getSimpleName();

    volatile private List<String> allLines = null;
    volatile private List<String> searchLines = null;
    volatile private boolean isProgress = false;

    final private Charset charset;
    final private boolean ignoreCase;
    final private StatusChangeNotifier callback;
    public interface StatusChangeNotifier {
        void changed();
    }

    public TextData(Charset charset, boolean ignoreCase, StatusChangeNotifier callback) {
        this.charset = charset;
        this.ignoreCase = ignoreCase;
        this.callback = callback;
    }

    final static private long INTERVAL = 300;
    private long lastNotifyTime = 0;
    private void notifyToClient(boolean force) {
        if (!force && lastNotifyTime + INTERVAL > System.currentTimeMillis()) return;
        callback.changed();
        lastNotifyTime = System.currentTimeMillis();
    }
    synchronized public List<String> getAllLines() {
        return allLines;
    }
    synchronized public List<String> getSearchLInes() {
        return searchLines;
    }
    public boolean isProgress() {
        return isProgress;
    }

    synchronized public void load(Context context, Uri uri) throws FileNotFoundException {
        new LoadTask().execute(context.getContentResolver().openInputStream(uri));
    }

    synchronized public void search(String s) {
        new SearchTask(s).execute();
    }

    private class LoadTask extends AsyncTask<InputStream, Void, Void> {
        final List<String> result;
        LoadTask() {
            isProgress = true;
            allLines = mkList();
            searchLines = null;
            result = allLines;
        }
        @Override
        protected Void doInBackground(InputStream... params) {
            try {
                read(params[0]);
            } catch (IOException e) {
                Log.e(TAG, e.toString(), e);
            }
            return null;
        }
        private void read(InputStream istream) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(istream, charset));
            List<String> buff = new ArrayList<>();
            try {
                while (allLines == result) {
                    String line = in.readLine();
                    if (line == null) break;
                    buff.add(line);
                    if (buff.size() > 1000) {
                        moveAll(result, buff);
                        notifyToClient(false);
                    }
                }
            } finally {
                Util.closeQuietly(in);
            }
            isProgress = false;
            moveAll(result, buff);
            notifyToClient(true);
            Log.v(TAG, "load finished");
        }
        private void moveAll(List<String> to, List<String> from) {
            to.addAll(from);
            from.clear();
        }
    }
    private class SearchTask extends AsyncTask<Void, Void, Void> {
        String param;
        final List<String> from;
        final List<String> result;
        SearchTask(String param) {
            isProgress = true;
            this.param = param;
            if (ignoreCase) this.param = param.toLowerCase();
            from = allLines;
            searchLines = mkList();
            result = searchLines;
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (TextUtils.isEmpty(param)) {
                searchLines = null;
                notifyToClient(true);
                isProgress = false;
                return null;
            }
            for (int i = 0; i < from.size(); i++) {
                if (result != searchLines) break;
                String s = from.get(i);
                if (ignoreCase) s = s.toLowerCase();
                if (s.toLowerCase().contains(param)) result.add(from.get(i));
                notifyToClient(false);
            }
            if (result == searchLines) isProgress = false;
            notifyToClient(true);
            Log.v(TAG, "search finished: " + param);
            return null;
        }
    }

    private List<String> mkList() {
        return Collections.synchronizedList(new ArrayList<String>());
    }
}
