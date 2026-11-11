package com.erdinc.bekohome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * BekoHome 0.2.0: a lightweight and optional Android TV launcher prototype.
 * Not registered as a HOME replacement; remote Back/Home keep system behavior.
 */
public final class MainActivity extends Activity {
    private static final Locale TR = new Locale("tr", "TR");

    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final Map<String, TextView> chips = new HashMap<>();
    private final List<AppEntry> allApps = new ArrayList<>();
    private final List<AppEntry> shownApps = new ArrayList<>();

    private AppStore store;
    private String currentSection = AppStore.ALL;
    private String lastFocusedPackage;

    private TextView clockText;
    private TextView dateText;
    private TextView sectionTitle;
    private TextView sectionSubtitle;
    private TextView appsCount;
    private TextView editButton;
    private LinearLayout categoryRow;
    private LinearLayout appsRow;
    private HorizontalScrollView appScroller;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            Date now = new Date();
            clockText.setText(new SimpleDateFormat("HH:mm", TR).format(now));
            dateText.setText(new SimpleDateFormat("EEEE, d MMMM yyyy", TR).format(now));
            clockHandler.postDelayed(this, 30000L);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        store = new AppStore(this);
        clockText = findViewById(R.id.clockText);
        dateText = findViewById(R.id.dateText);
        sectionTitle = findViewById(R.id.sectionTitle);
        sectionSubtitle = findViewById(R.id.sectionSubtitle);
        appsCount = findViewById(R.id.appsCount);
        categoryRow = findViewById(R.id.categoryRow);
        appsRow = findViewById(R.id.appsRow);
        appScroller = findViewById(R.id.appsScroll);
        editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(v -> {
            AppEntry selected = selectedApp();
            if (selected == null) {
                toast("Düzenlemek için bir uygulama seç.");
            } else {
                showEditMenu(selected);
            }
        });

        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getString("section", AppStore.ALL);
            lastFocusedPackage = savedInstanceState.getString("focusPackage");
        }
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putString("section", currentSection);
        out.putString("focusPackage", lastFocusedPackage);
        super.onSaveInstanceState(out);
    }

    @Override protected void onResume() {
        super.onResume();
        clockHandler.removeCallbacks(tick);
        tick.run();
        allApps.clear();
        allApps.addAll(AppEntry.discover(this));
        renderCategories();
        renderApps(lastFocusedPackage);
    }

    @Override protected void onPause() {
        clockHandler.removeCallbacks(tick);
        super.onPause();
    }

    @Override protected void onDestroy() {
        clockHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Optional MENU key for remotes that don't support long-press OK.
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            AppEntry entry = selectedApp();
            if (entry != null) showEditMenu(entry);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void renderCategories() {
        categoryRow.removeAllViews();
        chips.clear();

        for (int i = 0; i < AppStore.SECTIONS.length; i++) {
            final String section = AppStore.SECTIONS[i];
            TextView chip = new TextView(this);
            chip.setId(View.generateViewId());
            chip.setText(AppStore.SECTION_TITLES[i]);
            chip.setTextSize(14f);
            chip.setTextColor(0xFFECF2F9);
            chip.setSingleLine(true);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(19), 0, dp(19), 0);
            chip.setBackgroundResource(R.drawable.chip_selector);
            chip.setFocusable(true);
            chip.setClickable(true);
            chip.setSelected(section.equals(currentSection));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, dp(42));
            params.setMargins(0, 0, dp(9), 0);
            categoryRow.addView(chip, params);
            chips.put(section, chip);

            chip.setOnClickListener(v -> {
                currentSection = section;
                lastFocusedPackage = null;
                updateSelectedChips();
                renderApps(null);
            });
        }
    }

    private void updateSelectedChips() {
        for (Map.Entry<String, TextView> chip : chips.entrySet())
            chip.getValue().setSelected(chip.getKey().equals(currentSection));
    }

    private void renderApps(String preferredPackage) {
        shownApps.clear();
        shownApps.addAll(store.visibleApps(currentSection, allApps));

        sectionTitle.setText(AppStore.sectionTitle(currentSection));
        sectionSubtitle.setText(sectionDescription(currentSection));
        appsCount.setText(shownApps.size() + " uygulama");
        appsRow.removeAllViews();

        if (shownApps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(emptyMessage(currentSection));
            empty.setTextSize(16f);
            empty.setTextColor(0xFFA3B4C6);
            empty.setGravity(Gravity.CENTER_VERTICAL);
            appsRow.addView(empty, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, dp(115)));
            TextView chip = chips.get(currentSection);
            if (chip != null) chip.post(chip::requestFocus);
            return;
        }

        View focusTarget = null;
        for (AppEntry entry : shownApps) {
            View tile = createTile(entry);
            appsRow.addView(tile);
            if (entry.packageName.equals(preferredPackage)) focusTarget = tile;
        }
        if (focusTarget == null) focusTarget = appsRow.getChildAt(0);
        View target = focusTarget;
        appScroller.post(() -> {
            target.requestFocus();
            // Keep the focused tile on-screen, including its full outline.
            target.requestRectangleOnScreen(
                    new android.graphics.Rect(0, 0, target.getWidth(), target.getHeight()));
        });
    }

    private View createTile(AppEntry app) {
        FrameLayout tile = new FrameLayout(this);
        tile.setId(View.generateViewId());
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setLongClickable(true);
        tile.setBackgroundResource(R.drawable.card_selector);
        tile.setContentDescription(app.title + " uygulamasını aç veya uzun basarak düzenle");
        tile.setClipChildren(false);
        tile.setClipToPadding(false);
        LinearLayout.LayoutParams tileParams = new LinearLayout.LayoutParams(
                dimen(R.dimen.app_tile_width), dimen(R.dimen.app_tile_height));
        tileParams.setMargins(dp(5), dp(3), dimen(R.dimen.app_tile_gap), dp(3));
        tile.setLayoutParams(tileParams);

        LinearLayout center = new LinearLayout(this);
        center.setGravity(Gravity.CENTER);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setPadding(dp(10), dp(10), dp(10), dp(9));
        tile.addView(center, new FrameLayout.LayoutParams(-1, -1));

        ImageView logo = new ImageView(this);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Drawable icon = app.icon;
        logo.setImageDrawable(icon);
        center.addView(logo, new LinearLayout.LayoutParams(
                dimen(R.dimen.app_icon_size), dimen(R.dimen.app_icon_size)));

        TextView label = new TextView(this);
        label.setText(app.title);
        label.setTextColor(0xFFF5F7FA);
        label.setTextSize(14f);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = dp(10);
        center.addView(label, labelParams);

        if (store.isFavorite(app.packageName)) {
            TextView star = new TextView(this);
            star.setText("★");
            star.setTextSize(17f);
            star.setTextColor(0xFFF6D78C);
            star.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams starParams =
                    new FrameLayout.LayoutParams(dp(27), dp(27), Gravity.TOP | Gravity.END);
            starParams.topMargin = dp(4);
            starParams.rightMargin = dp(5);
            tile.addView(star, starParams);
        }

        tile.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) lastFocusedPackage = app.packageName;
            float scale = hasFocus ? 1.035f : 1f;
            v.animate().scaleX(scale).scaleY(scale).setDuration(110L).start();
        });

        tile.setOnClickListener(v -> {
            lastFocusedPackage = app.packageName;
            store.recordOpened(app.packageName);
            try {
                startActivity(app.intent);
            } catch (ActivityNotFoundException | SecurityException ex) {
                toast(app.title + " açılamadı.");
            }
        });
        tile.setOnLongClickListener(v -> {
            lastFocusedPackage = app.packageName;
            showEditMenu(app);
            return true;
        });
        return tile;
    }

    private AppEntry selectedApp() {
        for (AppEntry app : shownApps) {
            if (app.packageName.equals(lastFocusedPackage)) return app;
        }
        return shownApps.isEmpty() ? null : shownApps.get(0);
    }

    private void showEditMenu(AppEntry app) {
        boolean fav = store.isFavorite(app.packageName);
        String[] actions = {
                fav ? "★ Favorilerden çıkar" : "☆ Favorilere ekle",
                "Kategori değiştir",
                "← Sola taşı",
                "Sağa taşı →"
        };
        new AlertDialog.Builder(this)
                .setTitle(app.title + " · Düzenle")
                .setItems(actions, (dialog, option) -> {
                    switch (option) {
                        case 0:
                            boolean nowFavorite = store.toggleFavorite(app.packageName);
                            toast(nowFavorite ? "Favorilere eklendi." : "Favorilerden çıkarıldı.");
                            renderApps(app.packageName);
                            break;
                        case 1:
                            showCategoryDialog(app);
                            break;
                        case 2:
                            moveApp(app, -1);
                            break;
                        case 3:
                            moveApp(app, 1);
                            break;
                        default:
                            break;
                    }
                })
                .setNegativeButton("Vazgeç", null)
                .show();
    }

    private void showCategoryDialog(AppEntry app) {
        int checked = 0;
        String current = store.categoryOf(app);
        for (int i = 0; i < AppStore.CATEGORIES.length; i++) {
            if (AppStore.CATEGORIES[i].equals(current)) {
                checked = i;
                break;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(app.title + " · Kategori seç")
                .setSingleChoiceItems(AppStore.CATEGORY_TITLES, checked, (dialog, which) -> {
                    String target = AppStore.CATEGORIES[which];
                    store.setCategory(app.packageName, target);
                    // Show the destination so it is immediately clear where the app moved.
                    currentSection = target;
                    updateSelectedChips();
                    renderApps(app.packageName);
                    toast("Uygulama " + AppStore.CATEGORY_TITLES[which] + " kategorisine taşındı.");
                    dialog.dismiss();
                })
                .setNegativeButton("Vazgeç", null)
                .show();
    }

    private void moveApp(AppEntry app, int direction) {
        if (AppStore.RECENT.equals(currentSection)) {
            toast("Son Açılanlar sırası kullanım sırasına göre belirlenir.");
            return;
        }
        boolean moved = store.move(currentSection, app.packageName, direction, shownApps);
        if (!moved) {
            toast(direction < 0 ? "Zaten ilk sırada." : "Zaten son sırada.");
            return;
        }
        renderApps(app.packageName);
    }

    private static String sectionDescription(String section) {
        switch (section) {
            case AppStore.FAVORITES:
                return "Sık kullandıkların, tek yerde.";
            case AppStore.RECENT:
                return "Launcher üzerinden en son açtıkların.";
            case AppStore.WATCH:
                return "Film, dizi ve video uygulamaları.";
            case AppStore.MUSIC:
                return "Müzik ve ses uygulamaları.";
            case AppStore.GAMES:
                return "Oyunların.";
            case AppStore.TOOLS:
                return "Yardımcı uygulamalar.";
            case AppStore.OTHER:
                return "Diğer uygulamalar.";
            default:
                return "Tüm uygulamalar, senin istediğin sırada.";
        }
    }

    private static String emptyMessage(String section) {
        if (AppStore.FAVORITES.equals(section))
            return "Favori yok. Tümü'nde bir uygulamaya uzun OK basıp ★ Favorilere ekle.";
        if (AppStore.RECENT.equals(section))
            return "Henüz uygulama açılmadı. Beko Home üzerinden bir uygulama başlat.";
        return "Bu kategori boş. Tümü'nden bir uygulama seçip kategori değiştir.";
    }

    private int dimen(int resource) {
        return getResources().getDimensionPixelSize(resource);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
