package com.erdinc.bekohome;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Android TV launcher prototype. Not registered as a system HOME handler.
 * Only shown as a launchable TV app using the LEANBACK_LAUNCHER category.
 */
public final class MainActivity extends Activity {
    private static final Locale TR = new Locale("tr", "TR");
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private TextView clockText;
    private TextView dateText;
    private TextView appsCount;
    private LinearLayout appsRow;

    private final Runnable refreshClock = new Runnable() {
        @Override public void run() {
            Date now = new Date();
            if (clockText != null) {
                clockText.setText(new SimpleDateFormat("HH:mm", TR).format(now));
            }
            if (dateText != null) {
                dateText.setText(new SimpleDateFormat("EEEE, d MMMM yyyy", TR).format(now));
            }
            clockHandler.postDelayed(this, 15000L);
        }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        clockText = findViewById(R.id.clockText);
        dateText = findViewById(R.id.dateText);
        appsCount = findViewById(R.id.appsCount);
        appsRow = findViewById(R.id.appsRow);
    }

    @Override protected void onResume() {
        super.onResume();
        clockHandler.removeCallbacks(refreshClock);
        refreshClock.run();
        showInstalledApps();
    }

    @Override protected void onPause() {
        clockHandler.removeCallbacks(refreshClock);
        super.onPause();
    }

    @Override protected void onDestroy() {
        clockHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void showInstalledApps() {
        List<AppEntry> apps = findLaunchableApps();
        appsRow.removeAllViews();
        appsCount.setText(apps.size() + " uygulama");
        for (AppEntry app : apps) {
            appsRow.addView(makeAppTile(app));
        }
        if (apps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Uygulama bulunamadı. TV'deki uygulamaları kontrol et.");
            empty.setTextColor(0xFFFFFFFF);
            empty.setTextSize(18f);
            appsRow.addView(empty);
        } else {
            appsRow.post(() -> {
                if (appsRow.getChildCount() > 0) {
                    appsRow.getChildAt(0).requestFocus();
                }
            });
        }
    }

    private List<AppEntry> findLaunchableApps() {
        PackageManager pm = getPackageManager();
        Map<String, AppEntry> discovered = new HashMap<>();
        collect(pm, Intent.CATEGORY_LEANBACK_LAUNCHER, discovered);
        collect(pm, Intent.CATEGORY_LAUNCHER, discovered);
        List<AppEntry> result = new ArrayList<>(discovered.values());
        Collator collator = Collator.getInstance(TR);
        Collections.sort(result, (a, b) -> {
            int rankDiff = Integer.compare(priority(a), priority(b));
            return rankDiff != 0 ? rankDiff : collator.compare(a.label, b.label);
        });
        return result;
    }

    private void collect(PackageManager pm, String category,
                         Map<String, AppEntry> result) {
        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(category);
        for (ResolveInfo info : pm.queryIntentActivities(query, 0)) {
            if (info.activityInfo == null) continue;
            String pkg = info.activityInfo.packageName;
            if (getPackageName().equals(pkg) || result.containsKey(pkg)) continue;
            Intent launch = pm.getLeanbackLaunchIntentForPackage(pkg);
            if (launch == null) launch = pm.getLaunchIntentForPackage(pkg);
            if (launch == null) {
                launch = new Intent(Intent.ACTION_MAIN);
                launch.addCategory(category);
                launch.setClassName(pkg, info.activityInfo.name);
            }
            CharSequence text = info.loadLabel(pm);
            String label = text == null || text.length() == 0 ? pkg : text.toString();
            Drawable icon;
            try {
                icon = info.loadIcon(pm);
            } catch (Exception ex) {
                icon = getDrawable(android.R.drawable.sym_def_app_icon);
            }
            result.put(pkg, new AppEntry(pkg, label, icon, launch));
        }
    }

    private int priority(AppEntry app) {
        String text = (app.pkg + " " + app.label).toLowerCase(Locale.ROOT);
        if (text.contains("youtube")) return 0;
        if (text.contains("netflix")) return 1;
        if (text.contains("prime video") || text.contains("amazon.avod")) return 2;
        if (text.contains("disney")) return 3;
        if (text.contains("max") || text.contains("wbd.stream")) return 4;
        if (text.contains("spotify")) return 5;
        if (text.contains("exxen")) return 6;
        return 20;
    }

    private View makeAppTile(AppEntry app) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(12), dp(15), dp(12), dp(13));
        tile.setBackgroundResource(R.drawable.card_selector);
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setContentDescription(app.label + " uygulamasını aç");

        LinearLayout.LayoutParams tileParams =
                new LinearLayout.LayoutParams(dp(168), dp(142));
        tileParams.setMargins(dp(2), 0, dp(17), 0);
        tile.setLayoutParams(tileParams);

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(app.icon);
        tile.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(58)));

        TextView name = new TextView(this);
        name.setText(app.label);
        name.setTextColor(0xFFF2F6FA);
        name.setGravity(Gravity.CENTER);
        name.setTextSize(15f);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(-1, -2);
        nameParams.topMargin = dp(14);
        tile.addView(name, nameParams);

        tile.setOnClickListener(v -> {
            try {
                startActivity(app.launchIntent);
            } catch (ActivityNotFoundException | SecurityException error) {
                Toast.makeText(this, app.label + " açılamadı", Toast.LENGTH_SHORT).show();
            }
        });
        tile.setOnFocusChangeListener((v, focused) -> {
            float scale = focused ? 1.055f : 1.0f;
            v.animate().scaleX(scale).scaleY(scale).setDuration(120).start();
        });
        return tile;
    }

    private int dp(float size) {
        return Math.round(getResources().getDisplayMetrics().density * size);
    }

    private static final class AppEntry {
        final String pkg;
        final String label;
        final Drawable icon;
        final Intent launchIntent;

        AppEntry(String pkg, String label, Drawable icon, Intent launchIntent) {
            this.pkg = pkg;
            this.label = label;
            this.icon = icon;
            this.launchIntent = launchIntent;
        }
    }
}
