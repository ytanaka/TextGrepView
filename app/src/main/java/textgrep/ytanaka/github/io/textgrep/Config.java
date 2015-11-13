package textgrep.ytanaka.github.io.textgrep;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import com.google.gson.Gson;

import java.nio.charset.Charset;

public class Config {
    private static final String FILENAME = Config.class.getCanonicalName();

    private static SharedPreferences pref(Context context) {
        return context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public static Config read(Context context) {
        String json = pref(context).getString("json", "{}");
        return new Gson().fromJson(json, Config.class);
    }

    public static void write(Context context, Config config) {
        pref(context).edit().putString("json", new Gson().toJson(config)).commit();
    }

    public String charset = "UTF-8";
    public String font = "MONOSPACE";
    boolean showLineNumber = false;
    boolean wrapLine = true;
    boolean ignoreCase = true;

    public Typeface getFont() {
        switch (font) {
            case "DEFAULT":
                return Typeface.DEFAULT;
            case "SANS_SERIF":
                return Typeface.SANS_SERIF;
            case "SERIF":
                return Typeface.SERIF;
            case "DEFAULT_BOLD":
                return Typeface.DEFAULT_BOLD;
            default:
                return Typeface.MONOSPACE;
        }
    }

    public static final String[] fontNameList = {
            "DEFAULT",
            "SANS_SERIF",
            "SERIF",
            "DEFAULT_BOLD",
            "MONOSPACE"
    };

    public Charset getCharset() {
        return Charset.forName(charset);
    }

    public static final String[] charsetNameList = {
            "UTF-8",
            "MS932",
            "EUC-JP",
            "JIS"
    };
}
