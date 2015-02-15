package textgrep.ytanaka.github.io.textgrep;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class ViewActivity extends Activity {
    private static final String TAG = ViewActivity.class.getSimpleName();

    private TextData mTextData;
    private TextView mTextView;
    private ListView mListView;
    private MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mTextView = (TextView) findViewById(R.id.textView);
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mTextData = new TextData(new TextData.StatusChangeNotifier() {
            @Override
            public void changed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            mTextData.load(this, getIntent().getData());
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString(), e);
            Util.showOKCancelMsgBox(this, "ファイルが見つかりません", new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, false);
        }
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        Log.d(TAG, "m = " + am.getMemoryClass());
        Log.d(TAG, "lm = " + am.getLargeMemoryClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTextData.dispose();
    }

    private void refresh() {
        List<String> all = mTextData.getAllLines();
        List<String> search = mTextData.getSearchLInes();

        if (search == null) {
            mAdapter.setData(all);
            mTextView.setText(all.size() + "行 " + (mTextData.isProgress() ? "読み込み中・・・" : "読み込み完了"));
        } else {
            mAdapter.setData(search);
            mTextView.setText(search.size() + "行 " + (mTextData.isProgress() ? "検索中・・・" : "検索完了"));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sv.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.trim();
                android.util.Log.e("", "xxxxxxxxxxx「" + newText + "」");
                mTextData.search(newText);
                android.util.Log.e("", "xxxxxxxxxxx");
                return true;
            }
        });
        return true;
    }

    private class MyAdapter extends ArrayAdapter<String> {
        private List<String> list = new ArrayList<>();
        private int listSize; // 画面表示中にデータ数が増えるとエラーになるので、データ数をキャッシュしておく

        public MyAdapter() {
            super(ViewActivity.this, R.layout.list_item);
        }

        public void setData(List<String> list) {
            this.list = list;
            this.listSize = this.list.size();
        }

        @Override
        public int getCount() {
            return listSize;
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, mListView, false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.textView);
            String text = getItem(position);
            tv.setText(text);
            return convertView;
        }
    }
}
