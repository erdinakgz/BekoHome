package com.erdinc.bekohome;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Metadata for an app that has a launchable TV/Android activity. */
final class AppEntry {
    final String packageName;
    final String title;
    final Drawable icon;
    final Intent intent;
    final String suggestedCategory;

    AppEntry(String packageName, String title, Drawable icon, Intent intent) {
        this.packageName = packageName;
        this.title = title;
        this.icon = icon;
        this.intent = intent;
        this.suggestedCategory = suggestedCategory(packageName + " " + title);
    }

    static List<AppEntry> discover(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        Map<String, AppEntry> appsByPackage = new HashMap<>();
        collect(pm, activity.getPackageName(), Intent.CATEGORY_LEANBACK_LAUNCHER, appsByPackage);
        collect(pm, activity.getPackageName(), Intent.CATEGORY_LAUNCHER, appsByPackage);
        List<AppEntry> apps = new ArrayList<>(appsByPackage.values());
        Collator collator = Collator.getInstance(new Locale("tr", "TR"));
        Collections.sort(apps, (a, b) -> {
            int p = Integer.compare(priority(a), priority(b));
            return p == 0 ? collator.compare(a.title, b.title) : p;
        });
        return apps;
    }

    private static void collect(PackageManager pm, String ownPackage, String category,
                                Map<String, AppEntry> result) {
        Intent query = new Intent(Intent.ACTION_MAIN).addCategory(category);
        for (ResolveInfo info : pm.queryIntentActivities(query, 0)) {
            if (info.activityInfo == null) continue;
            String pkg = info.activityInfo.packageName;
            if (pkg.equals(ownPackage) || result.containsKey(pkg)) continue;

            Intent launch = pm.getLeanbackLaunchIntentForPackage(pkg);
            if (launch == null) launch = pm.getLaunchIntentForPackage(pkg);
            if (launch == null) {
                launch = new Intent(Intent.ACTION_MAIN);
                launch.addCategory(category);
                launch.setClassName(pkg, info.activityInfo.name);
            }

            CharSequence name = info.loadLabel(pm);
            String title = name == null || name.length() == 0 ? pkg : name.toString();
            Drawable icon;
            try {
                icon = info.loadIcon(pm);
            } catch (RuntimeException ignored) {
                icon = pm.getDefaultActivityIcon();
            }
            result.put(pkg, new AppEntry(pkg, title, icon, launch));
        }
    }

    private static int priority(AppEntry app) {
        String value = (app.packageName + " " + app.title).toLowerCase(Locale.ROOT);
        if (value.contains("youtube") && !value.contains("music")) return 0;
        if (value.contains("netflix")) return 1;
        if (value.contains("prime video") || value.contains("amazon.avod")) return 2;
        if (value.contains("disney")) return 3;
        if (value.contains("spotify")) return 4;
        if (value.contains("youtube music")) return 5;
        return 20;
    }

    private static String suggestedCategory(String value) {
        String v = value.toLowerCase(Locale.ROOT);
        if (v.contains("music") || v.contains("müzik") || v.contains("spotify")
                || v.contains("deezer") || v.contains("fizy") || v.contains("radio"))
            return AppStore.MUSIC;

        if (v.contains("netflix") || v.contains("youtube") || v.contains("prime video")
                || v.contains("amazon.avod") || v.contains("disney") || v.contains("exxen")
                || v.contains("bein") || v.contains("tod") || v.contains("tabii")
                || v.contains("gain") || v.contains("blutv") || v.contains("mubi")
                || v.contains("tv+") || v.contains("dsmart") || v.contains("ssport")
                || v.contains("wbd.stream") || v.contains("max"))
            return AppStore.WATCH;

        if (v.contains("game") || v.contains("oyun") || v.contains("steam")
                || v.contains("geforce now") || v.contains("retroarch"))
            return AppStore.GAMES;

        if (v.contains("file") || v.contains("explorer") || v.contains("browser")
                || v.contains("settings") || v.contains("ayarlar") || v.contains("market")
                || v.contains("play store") || v.contains("speedtest") || v.contains("remote"))
            return AppStore.TOOLS;

        return AppStore.OTHER;
    }
}
