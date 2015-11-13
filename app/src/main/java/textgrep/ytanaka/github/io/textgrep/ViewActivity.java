package textgrep.ytanaka.github.io.textgrep;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
    private Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mConfig = Config.read(this);
        mTextView = (TextView) findViewById(R.id.textView);
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
            }
        });
        mTextData = new TextData(mConfig.getCharset(), mConfig.ignoreCase, new TextData.StatusChangeNotifier() {
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
        try {
            mTextData.load(this, getIntent().getData());
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString(), e);
            Util.showOKCancelMsgBox(this, getResources().getString(R.string.file_not_found), new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, false);
            finish();
        }
    }

    private void refresh() {
        List<String> all = mTextData.getAllLines();
        List<String> search = mTextData.getSearchLInes();

        if (search == null) {
            mAdapter.setData(all);
            if (mTextData.isProgress()) {
                mTextView.setText(getResources().getString(R.string.status_xx_line_reading, all.size()));
            } else {
                mTextView.setText(getResources().getString(R.string.status_xx_line_read, all.size()));
            }
        } else {
            mAdapter.setData(search);
            if (mTextData.isProgress()) {
                mTextView.setText(getResources().getString(R.string.status_xx_line_seaching, search.size()));
            } else {
                mTextView.setText(getResources().getString(R.string.status_xx_line_seached, search.size()));
            }
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
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTextData.search(newText.trim());
                return true;
            }
        });
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
                convertView.setMinimumWidth(parent.getMeasuredWidth());
                ViewHolder vh = new ViewHolder();
                vh.tv = (TextView) convertView.findViewById(R.id.textView);
                vh.tv.setTypeface(mConfig.getFont());
                vh.tv.setMaxLines(mConfig.wrapLine ? 999 : 1);
                convertView.setTag(vh);
            }
            ViewHolder vh = (ViewHolder) convertView.getTag();
            String text = getItem(position);
            if (mConfig.showLineNumber) text = "" + (position + 1) + ": " + text;
            vh.tv.setText(text);
            return convertView;
        }
    }
    private static class ViewHolder {
        TextView tv;
    }
}
