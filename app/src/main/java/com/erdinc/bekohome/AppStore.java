package com.erdinc.bekohome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All personalization is stored locally. No network requests or external storage needed.
 * Package names (not labels) are used so reordering survives locale / app-title changes.
 */
final class AppStore {
    static final String ALL = "all";
    static final String FAVORITES = "favorites";
    static final String RECENT = "recent";
    static final String WATCH = "watch";
    static final String MUSIC = "music";
    static final String GAMES = "games";
    static final String TOOLS = "tools";
    static final String OTHER = "other";

    static final String[] SECTIONS = {
            FAVORITES, RECENT, WATCH, MUSIC, GAMES, TOOLS, OTHER, ALL
    };
    static final String[] SECTION_TITLES = {
            "★ Favoriler", "Son Açılanlar", "Film & Dizi", "Müzik",
            "Oyun", "Araçlar", "Diğer", "Tümü"
    };
    static final String[] CATEGORIES = {WATCH, MUSIC, GAMES, TOOLS, OTHER};
    static final String[] CATEGORY_TITLES = {"Film & Dizi", "Müzik", "Oyun", "Araçlar", "Diğer"};

    private static final String LIST_SEPARATOR = "|";
    private static final int MAX_RECENTS = 8;
    private final SharedPreferences preferences;

    AppStore(Context context) {
        preferences = context.getSharedPreferences("bekohome_v02", Context.MODE_PRIVATE);
    }

    String categoryOf(AppEntry app) {
        return preferences.getString("category:" + app.packageName, app.suggestedCategory);
    }

    void setCategory(String packageName, String category) {
        if (!Arrays.asList(CATEGORIES).contains(category)) return;
        preferences.edit().putString("category:" + packageName, category).apply();
    }

    boolean isFavorite(String packageName) {
        return preferences.getBoolean("favorite:" + packageName, false);
    }

    boolean toggleFavorite(String packageName) {
        boolean next = !isFavorite(packageName);
        preferences.edit().putBoolean("favorite:" + packageName, next).apply();
        return next;
    }

    void recordOpened(String packageName) {
        List<String> last = readList("recent");
        last.remove(packageName);
        last.add(0, packageName);
        while (last.size() > MAX_RECENTS) last.remove(last.size() - 1);
        writeList("recent", last);
    }

    List<AppEntry> visibleApps(String section, List<AppEntry> allApps) {
        List<AppEntry> result = new ArrayList<>();
        Set<String> recent = new HashSet<>(readList("recent"));
        for (AppEntry app : allApps) {
            if (ALL.equals(section)
                    || (FAVORITES.equals(section) && isFavorite(app.packageName))
                    || (RECENT.equals(section) && recent.contains(app.packageName))
                    || section.equals(categoryOf(app))) {
                result.add(app);
            }
        }

        List<String> storedOrder = readList("order:" + section);
        if (RECENT.equals(section) && storedOrder.isEmpty())
            storedOrder = readList("recent");
        if (storedOrder.isEmpty()) return result;

        Map<String, Integer> positions = new HashMap<>();
        for (int i = 0; i < storedOrder.size(); i++)
            positions.put(storedOrder.get(i), i);
        // Stable sort retains AppEntry.discover() order for apps not manually moved.
        Collections.sort(result, (a, b) -> Integer.compare(
                positions.getOrDefault(a.packageName, Integer.MAX_VALUE),
                positions.getOrDefault(b.packageName, Integer.MAX_VALUE)));
        return result;
    }

    boolean move(String section, String packageName, int delta, List<AppEntry> displayed) {
        if (RECENT.equals(section)) return false;
        List<String> order = new ArrayList<>();
        for (AppEntry entry : displayed) order.add(entry.packageName);
        int from = order.indexOf(packageName);
        int to = from + delta;
        if (from < 0 || to < 0 || to >= order.size()) return false;
        Collections.swap(order, from, to);
        writeList("order:" + section, order);
        return true;
    }

    private List<String> readList(String key) {
        String value = preferences.getString(key, "");
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split("\\|")));
    }

    private void writeList(String key, List<String> values) {
        preferences.edit().putString(key, String.join(LIST_SEPARATOR, values)).apply();
    }

    static String sectionTitle(String section) {
        for (int i = 0; i < SECTIONS.length; i++)
            if (SECTIONS[i].equals(section)) return SECTION_TITLES[i].replace("★ ", "");
        return "Tümü";
    }
}
