package textgrep.ytanaka.github.io.textgrep;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingActivity extends Activity {
    private Button mButtonCharset;
    private Button mButtonFont;
    private CheckBox mCheckShowLineNumber;
    private CheckBox mCheckWrapLine;
    private CheckBox mIgnoreCase;
    private Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        mButtonCharset = (Button) findViewById(R.id.button_encoding);
        mButtonFont = (Button) findViewById(R.id.button_font);
        mCheckShowLineNumber = (CheckBox) findViewById(R.id.checkBox_showLineNumber);
        mCheckWrapLine = (CheckBox) findViewById(R.id.checkBox_wrapLine);
        mIgnoreCase = (CheckBox) findViewById(R.id.checkBox_ignoreCase);

        mConfig = Config.read(this);
        mButtonCharset.setText(mConfig.charset);
        mButtonFont.setText(mConfig.font);
        mCheckShowLineNumber.setChecked(mConfig.showLineNumber);
        mCheckWrapLine.setChecked(mConfig.wrapLine);
        mIgnoreCase.setChecked(mConfig.ignoreCase);

        mButtonFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelection(mButtonFont, Config.fontNameList);
            }
        });
        mButtonCharset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelection(mButtonCharset, Config.charsetNameList);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mConfig.charset = mButtonCharset.getText().toString();
        mConfig.font = mButtonFont.getText().toString();
        mConfig.showLineNumber = mCheckShowLineNumber.isChecked();
        mConfig.wrapLine = mCheckWrapLine.isChecked();
        mConfig.ignoreCase = mIgnoreCase.isChecked();
        Config.write(this, mConfig);
    }

    private void showSelection(final Button button, final String[] list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button.setText(list[which]);
            }
        });
        builder.setCancelable(true);
        builder.show();
    }
}
