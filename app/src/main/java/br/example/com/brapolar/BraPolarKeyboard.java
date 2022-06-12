package br.example.com.brapolar;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.example.com.brapolar.Entities.EntityInput;

public class BraPolarKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean isCaps = true;
    private String charEspecial = "~`|•√π÷×¶∆£¥$¢^°={}\\%©®™✓[]ñÑ";

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        keyboard.setShifted(isCaps);
        kv.invalidateAllKeys();
        return kv;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {
        String topPackageName = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            long currentTime = System.currentTimeMillis();
            // get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 86400000, currentTime);
            // search for app with most recent last used time
            if (stats != null) {
                long lastUsedAppTime = 0;
                for (UsageStats usageStats : stats) {
                    if (usageStats.getLastTimeUsed() > lastUsedAppTime && !usageStats.getPackageName().equals("android")) {
                        topPackageName = usageStats.getPackageName();
                        lastUsedAppTime = usageStats.getLastTimeUsed();
                    }
                }
            }
        }
        MySqliteHandler mySqliteHandler = new MySqliteHandler(getBaseContext());
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String dateString = formater.format(new Date());
        if (primaryCode < 0) {//extra code
            if (primaryCode <= -100) {
                EntityInput x = new EntityInput(dateString, getString(R.string.input_key), topPackageName);
                mySqliteHandler.addInput(x);
            } else if (primaryCode == -5) {
                EntityInput x = new EntityInput(dateString, getString(R.string.input_erase), topPackageName);
                mySqliteHandler.addInput(x);
            }
        } else {
            EntityInput x = new EntityInput(dateString, getString(R.string.input_key), topPackageName);
            mySqliteHandler.addInput(x);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (primaryCode < -99) {
            int code = Math.abs(primaryCode + 100);
            if (code == 27 && isCaps) {
                code++;
            }
            ic.commitText(String.valueOf(charEspecial.charAt(code)), 1);
        } else {
            switch (primaryCode) {
                case -6:
                    keyboard = new Keyboard(this, R.xml.extra);
                    kv.setKeyboard(keyboard);
                    kv.setOnKeyboardActionListener(this);
                    break;
                case -10:
                    keyboard = new Keyboard(this, R.xml.numbers);
                    kv.setKeyboard(keyboard);
                    kv.setOnKeyboardActionListener(this);
                    break;
                case -11:
                    keyboard = new Keyboard(this, R.xml.extra);
                    kv.setKeyboard(keyboard);
                    kv.setOnKeyboardActionListener(this);
                    break;
                case -8:
                    keyboard = new Keyboard(this, R.xml.especial);
                    kv.setKeyboard(keyboard);
                    kv.setOnKeyboardActionListener(this);
                    break;
                case -9:
                    keyboard = new Keyboard(this, R.xml.qwerty);
                    kv.setKeyboard(keyboard);
                    kv.setOnKeyboardActionListener(this);
                    break;
                case Keyboard.KEYCODE_DELETE:
                    CharSequence selectedText = ic.getSelectedText(0);
                    if (TextUtils.isEmpty(selectedText)) {
                        ic.deleteSurroundingText(1, 0);
                    } else {
                        ic.commitText("", 1);
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    isCaps = !isCaps;
                    keyboard.setShifted(isCaps);
                    kv.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    break;
                default:
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && isCaps) {
                        code = Character.toUpperCase(code);
                    }
                    isCaps = false;
                    ic.commitText(String.valueOf(code), 1);
                    keyboard.setShifted(isCaps);
                    kv.invalidateAllKeys();
            }
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
