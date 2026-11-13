package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Remote-friendly settings owned by the launcher; system settings stay one row away. */
public final class LauncherSettingsActivity extends Activity {
    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LauncherLocale.wrap(base));
    }

    static final String EXTRA_REORDER = "com.gathin.tvhome.extra.REORDER_FROM_SETTINGS";
    static final String EXTRA_DOCK = "com.gathin.tvhome.extra.DOCK_FROM_SETTINGS";
    static final String EXTRA_LIBRARY = "com.gathin.tvhome.extra.LIBRARY_FROM_SETTINGS";
    private SharedPreferences choices;
    private final TextView[] columnChoices = new TextView[4];
    private final TextView[] rowChoices = new TextView[4];
    private static final int[] DOCK_OPACITIES = {100, 70, 50, 30};
    private final TextView[] dockChoices = new TextView[DOCK_OPACITIES.length];
    private static final String[] CLOCK_FORMATS = {"system", "12", "24"};
    private final TextView[] clockChoices = new TextView[CLOCK_FORMATS.length];
    private static final String[] GLASS_STYLES = {"glass", "rim", "halo"};
    private final TextView[] glassStyleChoices = new TextView[GLASS_STYLES.length];
    private static final String[] GLOW_STYLES = {"radial", "horizon"};
    private final TextView[] glowStyleChoices = new TextView[GLOW_STYLES.length];
    private static final int[] CINEMATIC_ART_VALUES = {0, 25, 50, 75, 100};
    private final TextView[] cinematicArtChoices = new TextView[CINEMATIC_ART_VALUES.length];
    private static final int[] CINEMATIC_SCRIM_VALUES = {0, 25, 50, 75, 100};
    private final TextView[] cinematicScrimChoices = new TextView[CINEMATIC_SCRIM_VALUES.length];
    private static final int[] ICON_SIZE_VALUES = {80, 90, 100, 110};
    private final TextView[] iconSizeChoices = new TextView[ICON_SIZE_VALUES.length];
    private TextView wallpaperChoice;
    private TextView animationsChoice;
    private TextView advancedChoice;
    private TextView focusOvershootChoice;
    private TextView reducedMotionChoice;
    private TextView followThemeChoice;
    private TextView openOnStartChoice;
    private TextView fillDockChoice;
    private TextView dockTitleChoice;
    private TextView dockGlassChoice;
    private TextView dockBandChoice;
    private TextView dockEvenPaddingChoice;
    private TextView singleRowChoice;
    private TextView dockBandAllChoice;
    private TextView alwaysShowNamesChoice;
    private TextView cinematicHomeChoice;
    private TextView cinematicDotsChoice;
    private TextView cinematicWhiteFocusChoice;
    private TextView ambientGlowChoice;
    private TextView appGlowChoice;
    private int columns;
    private int rowsBelowDock;
    private int dockOpacity;
    private String clockFormat;
    private String glassStyle;
    private String glowStyle;
    private int cinematicArt;
    private int cinematicScrim;
    private int iconScale;
    private boolean animations;
    private boolean advancedEffects;
    private boolean focusOvershoot;
    private boolean reducedMotion;
    private boolean fillDock;
    private boolean dockTitle;
    private boolean dockGlass;
    private boolean dockBand;
    private boolean dockEvenPadding;
    private boolean singleRow;
    private boolean dockBandAll;
    private boolean alwaysShowNames;
    private boolean cinematicHome;
    private boolean cinematicDots;
    private boolean cinematicWhiteFocus;
    private boolean ambientGlow;
    private boolean appGlow;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_launcher_settings);
        choices = getSharedPreferences(Config.SETTINGS_NAME, MODE_PRIVATE);
        Config config = Config.load(this);
        View reorderChoice = findViewById(R.id.reorderChoice);
        reorderChoice.setVisibility(config.launcherSettingsButton ? View.VISIBLE : View.GONE);
        reorderChoice.setOnClickListener(v -> finishWith(EXTRA_REORDER));
        findViewById(R.id.installedAppsChoice).setOnClickListener(v -> finishWith(EXTRA_LIBRARY));
        View dockChoice = findViewById(R.id.dockChoice);
        dockChoice.setVisibility(config.dockRow ? View.VISIBLE : View.GONE);
        dockChoice.setOnClickListener(v -> finishWith(EXTRA_DOCK));
        findViewById(R.id.wifiChoice).setOnClickListener(v ->
                open(SystemSettings.intent(this, Settings.ACTION_WIFI_SETTINGS)));
        findViewById(R.id.deviceInfoChoice).setOnClickListener(v ->
                open(SystemSettings.intent(this, Settings.ACTION_DEVICE_INFO_SETTINGS)));
        columns = config.columns;
        rowsBelowDock = config.rowsBelowDock;
        dockOpacity = config.dockOpacity;
        clockFormat = config.clockFormat;
        animations = config.animations;
        advancedEffects = config.animations && config.advancedEffects;
        focusOvershoot = config.focusOvershoot;
        reducedMotion = config.reducedMotion;
        fillDock = config.fillDock;
        dockTitle = config.dockTitle;
        dockGlass = config.dockGlass;
        dockBand = config.dockBand;
        dockEvenPadding = config.dockEvenPadding;
        singleRow = config.singleRow;
        dockBandAll = config.dockBandAll;
        alwaysShowNames = config.alwaysShowNames;
        cinematicHome = config.cinematicHome;
        cinematicDots = config.cinematicDots;
        cinematicWhiteFocus = config.cinematicWhiteFocus;
        ambientGlow = config.ambientGlow;
        appGlow = config.appGlow;
        glassStyle = config.glassStyle;
        glowStyle = config.glowStyle;
        cinematicArt = config.cinematicArt;
        cinematicScrim = config.cinematicScrim;
        iconScale = config.iconScale;

        int[] ids = {R.id.columns4, R.id.columns5, R.id.columns6, R.id.columns7};
        for (int i = 0; i < ids.length; i++) {
            final int value = i + 4;
            columnChoices[i] = findViewById(ids[i]);
            columnChoices[i].setOnClickListener(v -> {
                if (columns == value) return;
                columns = value;
                choices.edit().putInt("columns", value).apply();
                changed();
                refresh();
            });
        }
        int[] rowIds = {R.id.rows0, R.id.rows1, R.id.rows2, R.id.rows3};
        for (int i = 0; i < rowIds.length; i++) {
            final int value = i;
            rowChoices[i] = findViewById(rowIds[i]);
            rowChoices[i].setOnClickListener(v -> {
                if (rowsBelowDock == value) return;
                rowsBelowDock = value;
                choices.edit().putInt(Config.ROWS_BELOW_DOCK_KEY, value).apply();
                changed();
                refresh();
            });
        }
        int[] dockIds = {R.id.dock100, R.id.dock70, R.id.dock50, R.id.dock30};
        for (int i = 0; i < dockIds.length; i++) {
            final int value = DOCK_OPACITIES[i];
            dockChoices[i] = findViewById(dockIds[i]);
            dockChoices[i].setOnClickListener(v -> {
                if (dockOpacity == value) return;
                dockOpacity = value;
                choices.edit().putInt(Config.DOCK_OPACITY_KEY, value).apply();
                changed();
                refresh();
            });
        }
        int[] clockIds = {R.id.clockSystem, R.id.clock12, R.id.clock24};
        for (int i = 0; i < clockIds.length; i++) {
            final String value = CLOCK_FORMATS[i];
            clockChoices[i] = findViewById(clockIds[i]);
            clockChoices[i].setOnClickListener(v -> {
                if (value.equals(clockFormat)) return;
                clockFormat = value;
                choices.edit().putString(Config.CLOCK_FORMAT_KEY, value).apply();
                changed();
                refresh();
            });
        }
        int[] glassStyleIds = {R.id.glassStyleGlass, R.id.glassStyleRim, R.id.glassStyleHalo};
        for (int i = 0; i < glassStyleIds.length; i++) {
            final String value = GLASS_STYLES[i];
            glassStyleChoices[i] = findViewById(glassStyleIds[i]);
            glassStyleChoices[i].setOnClickListener(v -> {
                if (value.equals(glassStyle)) return;
                glassStyle = value;
                choices.edit().putString("glass_style", value).apply();
                changed();
                refresh();
            });
        }
        int[] glowStyleIds = {R.id.glowStyleRadial, R.id.glowStyleHorizon};
        for (int i = 0; i < glowStyleIds.length; i++) {
            final String value = GLOW_STYLES[i];
            glowStyleChoices[i] = findViewById(glowStyleIds[i]);
            glowStyleChoices[i].setOnClickListener(v -> {
                if (value.equals(glowStyle)) return;
                glowStyle = value;
                choices.edit().putString("glow_style", value).apply();
                changed();
                refresh();
            });
        }
        int[] cinematicArtIds = {R.id.cinematicArt0, R.id.cinematicArt25, R.id.cinematicArt50, R.id.cinematicArt75, R.id.cinematicArt100};
        for (int i = 0; i < cinematicArtIds.length; i++) {
            final int value = CINEMATIC_ART_VALUES[i];
            cinematicArtChoices[i] = findViewById(cinematicArtIds[i]);
            cinematicArtChoices[i].setOnClickListener(v -> {
                if (cinematicArt == value) return;
                cinematicArt = value;
                choices.edit().putInt("cinematic_art", value).apply();
                changed();
                refresh();
            });
        }
        int[] cinematicScrimIds = {R.id.cinematicScrim0, R.id.cinematicScrim25, R.id.cinematicScrim50, R.id.cinematicScrim75, R.id.cinematicScrim100};
        for (int i = 0; i < cinematicScrimIds.length; i++) {
            final int value = CINEMATIC_SCRIM_VALUES[i];
            cinematicScrimChoices[i] = findViewById(cinematicScrimIds[i]);
            cinematicScrimChoices[i].setOnClickListener(v -> {
                if (cinematicScrim == value) return;
                cinematicScrim = value;
                choices.edit().putInt("cinematic_scrim", value).apply();
                changed();
                refresh();
            });
        }
        int[] iconSizeIds = {R.id.iconSize80, R.id.iconSize90, R.id.iconSize100, R.id.iconSize110};
        for (int i = 0; i < iconSizeIds.length; i++) {
            final int value = ICON_SIZE_VALUES[i];
            iconSizeChoices[i] = findViewById(iconSizeIds[i]);
            iconSizeChoices[i].setOnClickListener(v -> {
                if (iconScale == value) return;
                iconScale = value;
                choices.edit().putInt("icon_scale", value).apply();
                changed();
                refresh();
            });
        }
        // Mirror text alignment without globally opting the launcher into RTL. Numeric rows
        // still need explicit ordering in Android's no-supportsRtl compatibility mode.
        if (getResources().getBoolean(R.bool.rtl_menu_bar))
            findViewById(android.R.id.content).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        if (getResources().getBoolean(R.bool.rtl_app_grid)) {
            reverseRow(columnChoices);
            reverseRow(rowChoices);
            reverseRow(dockChoices);
            reverseRow(clockChoices);
            reverseRow(glassStyleChoices);
            reverseRow(glowStyleChoices);
            reverseRow(cinematicArtChoices);
            reverseRow(cinematicScrimChoices);
            reverseRow(iconSizeChoices);
        }
        wallpaperChoice = findViewById(R.id.wallpaperChoice);
        wallpaperChoice.setOnClickListener(v -> showWallpaperPicker());
        animationsChoice = findViewById(R.id.animationsChoice);
        animationsChoice.setOnClickListener(v -> {
            animations = !animations;
            if (!animations) advancedEffects = false;
            choices.edit().putBoolean("animations", animations)
                    .putBoolean("advanced_effects", advancedEffects).apply();
            changed();
            refresh();
        });
        advancedChoice = findViewById(R.id.advancedChoice);
        advancedChoice.setOnClickListener(v -> {
            if (advancedEffects) {
                setAdvanced(false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.advanced_effects)
                        .setMessage(R.string.advanced_effects_warning)
                        .setPositiveButton(R.string.enable, (dialog, which) -> setAdvanced(true))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        focusOvershootChoice = findViewById(R.id.focusOvershootChoice);
        focusOvershootChoice.setOnClickListener(v -> {
            focusOvershoot = !focusOvershoot;
            choices.edit().putBoolean("focus_overshoot", focusOvershoot).apply();
            changed();
            refresh();
        });
        reducedMotionChoice = findViewById(R.id.reducedMotionChoice);
        reducedMotionChoice.setOnClickListener(v -> {
            reducedMotion = !reducedMotion;
            choices.edit().putBoolean("reduced_motion", reducedMotion).apply();
            changed();
            refresh();
        });
        fillDockChoice = findViewById(R.id.fillDockChoice);
        fillDockChoice.setOnClickListener(v -> {
            fillDock = !fillDock;
            choices.edit().putBoolean("fill_dock", fillDock).apply();
            changed();
            refresh();
        });
        dockTitleChoice = findViewById(R.id.dockTitleChoice);
        dockTitleChoice.setOnClickListener(v -> {
            dockTitle = !dockTitle;
            choices.edit().putBoolean("dock_title", dockTitle).apply();
            changed();
            refresh();
        });
        dockGlassChoice = findViewById(R.id.dockGlassChoice);
        dockGlassChoice.setOnClickListener(v -> {
            dockGlass = !dockGlass;
            choices.edit().putBoolean("dock_glass", dockGlass).apply();
            changed();
            refresh();
        });
        dockBandChoice = findViewById(R.id.dockBandChoice);
        dockBandChoice.setOnClickListener(v -> {
            dockBand = !dockBand;
            choices.edit().putBoolean("dock_band", dockBand).apply();
            changed();
            refresh();
        });
        dockEvenPaddingChoice = findViewById(R.id.dockEvenPaddingChoice);
        dockEvenPaddingChoice.setOnClickListener(v -> {
            dockEvenPadding = !dockEvenPadding;
            choices.edit().putBoolean("dock_even_padding", dockEvenPadding).apply();
            changed();
            refresh();
        });
        singleRowChoice = findViewById(R.id.singleRowChoice);
        singleRowChoice.setOnClickListener(v -> {
            singleRow = !singleRow;
            choices.edit().putBoolean("single_row", singleRow).apply();
            changed();
            refresh();
        });
        dockBandAllChoice = findViewById(R.id.dockBandAllChoice);
        dockBandAllChoice.setOnClickListener(v -> {
            dockBandAll = !dockBandAll;
            choices.edit().putBoolean("dock_band_all", dockBandAll).apply();
            changed();
            refresh();
        });
        alwaysShowNamesChoice = findViewById(R.id.alwaysShowNamesChoice);
        alwaysShowNamesChoice.setOnClickListener(v -> {
            alwaysShowNames = !alwaysShowNames;
            choices.edit().putBoolean("always_show_names", alwaysShowNames).apply();
            changed();
            refresh();
        });
        cinematicHomeChoice = findViewById(R.id.cinematicHomeChoice);
        cinematicHomeChoice.setOnClickListener(v -> {
            cinematicHome = !cinematicHome;
            choices.edit().putBoolean("cinematic_home", cinematicHome).apply();
            changed();
            refresh();
        });
        cinematicDotsChoice = findViewById(R.id.cinematicDotsChoice);
        cinematicDotsChoice.setOnClickListener(v -> {
            cinematicDots = !cinematicDots;
            choices.edit().putBoolean("cinematic_dots", cinematicDots).apply();
            changed();
            refresh();
        });
        cinematicWhiteFocusChoice = findViewById(R.id.cinematicWhiteFocusChoice);
        cinematicWhiteFocusChoice.setOnClickListener(v -> {
            cinematicWhiteFocus = !cinematicWhiteFocus;
            choices.edit().putBoolean("cinematic_white_focus", cinematicWhiteFocus).apply();
            changed();
            refresh();
        });
        ambientGlowChoice = findViewById(R.id.ambientGlowChoice);
        ambientGlowChoice.setOnClickListener(v -> {
            ambientGlow = !ambientGlow;
            choices.edit().putBoolean("ambient_glow", ambientGlow).apply();
            changed();
            refresh();
        });
        appGlowChoice = findViewById(R.id.appGlowChoice);
        appGlowChoice.setOnClickListener(v -> {
            appGlow = !appGlow;
            choices.edit().putBoolean("app_glow", appGlow).apply();
            changed();
            refresh();
        });
        openOnStartChoice = findViewById(R.id.openOnStartChoice);
        if (config.openOnStartChoice) {
            openOnStartChoice.setVisibility(View.VISIBLE);
            openOnStartChoice.setOnClickListener(v -> showOpenOnStartPicker());
        }
        followThemeChoice = findViewById(R.id.followThemeChoice);
        followThemeChoice.setOnClickListener(v -> {
            if (!hasOnTvChoices()) return;
            choices.edit().remove("columns").remove(Config.ROWS_BELOW_DOCK_KEY).remove("animations")
                    .remove("advanced_effects").remove(Config.WALLPAPER_CHOICE_KEY)
                    .remove(Config.DOCK_OPACITY_KEY).remove(Config.CLOCK_FORMAT_KEY).remove(OpenOnStart.KEY)
                    .remove("fill_dock").remove("dock_title").remove("dock_glass").remove("dock_band")
                    .remove("dock_even_padding").remove("single_row").remove("dock_band_all")
                    .remove("always_show_names").remove("cinematic_home").remove("cinematic_dots")
                    .remove("cinematic_white_focus").remove("ambient_glow").remove("app_glow")
                    .remove("focus_overshoot").remove("reduced_motion").remove("glass_style")
                    .remove("glow_style").remove("cinematic_art").remove("cinematic_scrim").remove("icon_scale").commit();
            Config current = Config.load(this);
            columns = current.columns;
            rowsBelowDock = current.rowsBelowDock;
            dockOpacity = current.dockOpacity;
            clockFormat = current.clockFormat;
            animations = current.animations;
            advancedEffects = current.animations && current.advancedEffects;
            focusOvershoot = current.focusOvershoot;
            reducedMotion = current.reducedMotion;
            fillDock = current.fillDock;
            dockTitle = current.dockTitle;
            dockGlass = current.dockGlass;
            dockBand = current.dockBand;
            dockEvenPadding = current.dockEvenPadding;
            singleRow = current.singleRow;
            dockBandAll = current.dockBandAll;
            alwaysShowNames = current.alwaysShowNames;
            cinematicHome = current.cinematicHome;
            cinematicDots = current.cinematicDots;
            cinematicWhiteFocus = current.cinematicWhiteFocus;
            ambientGlow = current.ambientGlow;
            appGlow = current.appGlow;
            glassStyle = current.glassStyle;
            glowStyle = current.glowStyle;
            cinematicArt = current.cinematicArt;
            cinematicScrim = current.cinematicScrim;
            iconScale = current.iconScale;
            changed();
            refresh();
        });
        findViewById(R.id.defaultHomeChoice).setOnClickListener(v ->
                open(new Intent(this, HomeSettingsActivity.class)));
        findViewById(R.id.tvSettingsChoice).setOnClickListener(v ->
                open(SystemSettings.intent(this, Settings.ACTION_SETTINGS)));
        refresh();
        if (config.launcherSettingsButton) reorderChoice.requestFocus();
        else columnChoices[Math.max(0, Math.min(columns - 4, 3))].requestFocus();
    }

    private void finishWith(String extra) {
        setResult(RESULT_OK, new Intent().putExtra(extra, true));
        finish();
    }

    private void reverseRow(TextView[] views) {
        LinearLayout row = (LinearLayout) views[0].getParent();
        row.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--) row.addView(views[i]);
    }

    private void showWallpaperPicker() {
        File dir = Config.dir(this);
        List<File> images = new java.util.ArrayList<>(WallpaperRotation.images(
                dir == null ? null : new File(dir, "wallpapers")));
        File single = dir == null ? null : new File(dir, Config.WALLPAPER_NAME);
        if (single != null && single.isFile()) images.add(0, single);
        String[] labels = new String[images.size() + 1];
        for (int i = 0; i < images.size(); i++) labels[i] = images.get(i).getName();
        labels[images.size()] = getString(R.string.wallpaper_none);
        String current = choices.getString(Config.WALLPAPER_CHOICE_KEY, null);
        int checked = "none".equals(current) ? images.size()
                : indexOf(images, current);
        new AlertDialog.Builder(this)
                .setTitle(R.string.wallpaper_label)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    if (which == images.size()) {
                        choices.edit().putString(Config.WALLPAPER_CHOICE_KEY, "none").apply();
                    } else {
                        choices.edit().putString(Config.WALLPAPER_CHOICE_KEY,
                                images.get(which).getName()).apply();
                    }
                    changed();
                    Toast.makeText(this, R.string.wallpaper_applied, Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNeutralButton(R.string.wallpaper_theme_default, (dialog, which) -> {
                    choices.edit().remove(Config.WALLPAPER_CHOICE_KEY).apply();
                    changed();
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Off, then every app with a TV entry point by name. Saved at once; it is read on the next boot. */
    private void showOpenOnStartPicker() {
        PackageManager pm = getPackageManager();
        List<String[]> apps = new ArrayList<>();
        Intent tv = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        for (ResolveInfo r : pm.queryIntentActivities(tv, 0)) {
            String pkg = r.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            boolean seen = false;
            for (String[] a : apps) seen |= a[1].equals(pkg);
            if (!seen) apps.add(new String[]{r.loadLabel(pm).toString(), pkg});
        }
        Collections.sort(apps, (a, b) -> a[0].compareToIgnoreCase(b[0]));
        String[] labels = new String[apps.size() + 1];
        labels[0] = getString(R.string.off);
        String current = choices.getString(OpenOnStart.KEY, "");
        int checked = 0;
        for (int i = 0; i < apps.size(); i++) {
            labels[i + 1] = apps.get(i)[0];
            if (apps.get(i)[1].equals(current)) checked = i + 1;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.open_on_start_label)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    if (which == 0) choices.edit().remove(OpenOnStart.KEY).apply();
                    else {
                        OpenOnStart.skipThisBoot(this);
                        choices.edit().putString(OpenOnStart.KEY, apps.get(which - 1)[1]).apply();
                    }
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** The picked app's name, its package when it is gone, or Off. */
    private String openOnStartLabel() {
        String pkg = choices.getString(OpenOnStart.KEY, "");
        if (pkg.isEmpty()) return getString(R.string.off);
        try {
            PackageManager pm = getPackageManager();
            return pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return pkg;
        }
    }

    private static int indexOf(List<File> images, String name) {
        if (name == null) return -1;
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    /**
     * A choice was saved. Home rebuilds when it next resumes because the change count moved, whether these
     * settings are left with Back or with the remote's Home button (which cancels the activity result).
     */
    private void changed() {
        setResult(RESULT_OK);
        choices.edit().putLong(Config.SETTINGS_REVISION_KEY,
                choices.getLong(Config.SETTINGS_REVISION_KEY, 0) + 1).apply();
    }

    private void setAdvanced(boolean enabled) {
        advancedEffects = enabled;
        choices.edit().putBoolean("advanced_effects", enabled).apply();
        changed();
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < columnChoices.length; i++) {
            columnChoices[i].setSelected(columns == i + 4);
        }
        for (int i = 0; i < rowChoices.length; i++) {
            rowChoices[i].setSelected(rowsBelowDock == i);
        }
        for (int i = 0; i < dockChoices.length; i++) {
            dockChoices[i].setSelected(dockOpacity == DOCK_OPACITIES[i]);
        }
        for (int i = 0; i < clockChoices.length; i++) {
            clockChoices[i].setSelected(CLOCK_FORMATS[i].equals(clockFormat));
        }
        for (int i = 0; i < glassStyleChoices.length; i++) {
            glassStyleChoices[i].setSelected(GLASS_STYLES[i].equals(glassStyle));
        }
        for (int i = 0; i < glowStyleChoices.length; i++) {
            glowStyleChoices[i].setSelected(GLOW_STYLES[i].equals(glowStyle));
        }
        for (int i = 0; i < cinematicArtChoices.length; i++) {
            cinematicArtChoices[i].setSelected(cinematicArt == CINEMATIC_ART_VALUES[i]);
        }
        for (int i = 0; i < cinematicScrimChoices.length; i++) {
            cinematicScrimChoices[i].setSelected(cinematicScrim == CINEMATIC_SCRIM_VALUES[i]);
        }
        for (int i = 0; i < iconSizeChoices.length; i++) {
            iconSizeChoices[i].setSelected(iconScale == ICON_SIZE_VALUES[i]);
        }
        animationsChoice.setText(getString(R.string.animations_state,
                getString(animations ? R.string.on : R.string.off)));
        advancedChoice.setText(getString(R.string.advanced_effects_state,
                getString(advancedEffects ? R.string.on : R.string.off)));
        advancedChoice.setEnabled(animations);
        advancedChoice.setAlpha(animations ? 1f : 0.5f);
        focusOvershootChoice.setText(getString(R.string.focus_overshoot_label, getString(focusOvershoot ? R.string.on : R.string.off)));
        reducedMotionChoice.setText(getString(R.string.reduced_motion_label, getString(reducedMotion ? R.string.on : R.string.off)));
        fillDockChoice.setText(getString(R.string.fill_dock_label, getString(fillDock ? R.string.on : R.string.off)));
        dockTitleChoice.setText(getString(R.string.dock_title_label, getString(dockTitle ? R.string.on : R.string.off)));
        dockGlassChoice.setText(getString(R.string.dock_glass_label, getString(dockGlass ? R.string.on : R.string.off)));
        dockBandChoice.setText(getString(R.string.dock_band_label, getString(dockBand ? R.string.on : R.string.off)));
        dockEvenPaddingChoice.setText(getString(R.string.dock_even_padding_label, getString(dockEvenPadding ? R.string.on : R.string.off)));
        singleRowChoice.setText(getString(R.string.single_row_label, getString(singleRow ? R.string.on : R.string.off)));
        dockBandAllChoice.setText(getString(R.string.dock_band_all_label, getString(dockBandAll ? R.string.on : R.string.off)));
        alwaysShowNamesChoice.setText(getString(R.string.always_show_names_label, getString(alwaysShowNames ? R.string.on : R.string.off)));
        cinematicHomeChoice.setText(getString(R.string.cinematic_home_label, getString(cinematicHome ? R.string.on : R.string.off)));
        cinematicDotsChoice.setText(getString(R.string.cinematic_dots_label, getString(cinematicDots ? R.string.on : R.string.off)));
        cinematicWhiteFocusChoice.setText(getString(R.string.cinematic_white_focus_label, getString(cinematicWhiteFocus ? R.string.on : R.string.off)));
        ambientGlowChoice.setText(getString(R.string.ambient_glow_label, getString(ambientGlow ? R.string.on : R.string.off)));
        appGlowChoice.setText(getString(R.string.app_glow_label, getString(appGlow ? R.string.on : R.string.off)));
        String wallpaperCurrent = choices.getString(Config.WALLPAPER_CHOICE_KEY, null);
        wallpaperChoice.setText(getString(R.string.wallpaper_state,
                "none".equals(wallpaperCurrent) ? getString(R.string.wallpaper_none)
                        : wallpaperCurrent != null ? wallpaperCurrent
                        : getString(R.string.wallpaper_theme_default)));
        openOnStartChoice.setText(getString(R.string.open_on_start_state, openOnStartLabel()));
        followThemeChoice.setSelected(!hasOnTvChoices());
    }

    private boolean hasOnTvChoices() {
        return choices.contains("columns") || choices.contains(Config.ROWS_BELOW_DOCK_KEY)
                || choices.contains("animations") || choices.contains("advanced_effects")
                || choices.contains(Config.WALLPAPER_CHOICE_KEY) || choices.contains(Config.DOCK_OPACITY_KEY)
                || choices.contains(Config.CLOCK_FORMAT_KEY) || choices.contains(OpenOnStart.KEY)
                || choices.contains("fill_dock") || choices.contains("dock_title")
                || choices.contains("dock_glass") || choices.contains("dock_band")
                || choices.contains("dock_even_padding") || choices.contains("single_row")
                || choices.contains("dock_band_all") || choices.contains("always_show_names")
                || choices.contains("cinematic_home") || choices.contains("cinematic_dots")
                || choices.contains("cinematic_white_focus") || choices.contains("ambient_glow")
                || choices.contains("app_glow") || choices.contains("focus_overshoot")
                || choices.contains("reduced_motion") || choices.contains("glass_style")
                || choices.contains("glow_style") || choices.contains("cinematic_art")
                || choices.contains("cinematic_scrim") || choices.contains("icon_scale");
    }

    private void open(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
        }
    }
}