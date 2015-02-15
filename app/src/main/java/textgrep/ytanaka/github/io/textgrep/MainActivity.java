package textgrep.ytanaka.github.io.textgrep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GET_CONTENT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked_open(View v) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult: " + data);
        if (requestCode != REQUEST_CODE_GET_CONTENT) return;
        if (resultCode != RESULT_OK) return;
        Intent intent = new Intent(this, ViewActivity.class);
        intent.setData(data.getData());
        startActivity(intent);
    }

    public void onButtonClicked_setting(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void onButtonClicked_about(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
