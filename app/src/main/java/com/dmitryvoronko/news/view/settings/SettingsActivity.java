package com.dmitryvoronko.news.view.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.view.util.ThemeHelper;

import java.util.List;

public final class SettingsActivity extends AppCompatPreferenceActivity
{

    private final static Preference.OnPreferenceChangeListener
            sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object value)
                {
                    final String stringValue = value.toString();

                    if (preference instanceof ListPreference)
                    {
                        final ListPreference listPreference = (ListPreference) preference;
                        final int index = listPreference.findIndexOfValue(stringValue);

                        preference.setSummary(
                                index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                        final String themeKey =
                                preference.getContext().getString(R.string.pref_key_choose_theme);
                        if (preference.getKey().equalsIgnoreCase(themeKey))
                        {
                            ThemeHelper.setTheme(preference.getContext());
                        }

                    } else if (preference instanceof RingtonePreference)
                    {
                        if (TextUtils.isEmpty(stringValue))
                        {
                            preference.setSummary(R.string.pref_ringtone_silent);

                        } else
                        {
                            final Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null)
                            {
                                preference.setSummary(null);
                            } else
                            {
                                final String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else
                    {
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    private static void bindPreferenceSummaryToValue(final Preference preference)
    {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        final Context context = preference.getContext();
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final String newValue = preferences.getString(preference.getKey(), "");
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        ThemeHelper.setTheme(this);
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar()
    {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item)
    {
        final int id = item.getItemId();
        if (id == android.R.id.home)
        {
            if (!super.onMenuItemSelected(featureId, item))
            {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(final Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public void onBuildHeaders(final List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(final String fragmentName)
    {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || UpdatesPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            final String themeKey = getString(R.string.pref_key_choose_theme);
            bindPreferenceSummaryToValue(findPreference(themeKey));
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item)
        {
            final int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class NotificationPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            final String ringtoneKey = getString(R.string.pref_key_ringtone);
            bindPreferenceSummaryToValue(findPreference(ringtoneKey));
            final String lEDColorKey = getString(R.string.pref_key_notifications_led_color);
            bindPreferenceSummaryToValue(findPreference(lEDColorKey));
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item)
        {
            final int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class UpdatesPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_updates);
            setHasOptionsMenu(true);


            final String timePickerKey = getString(R.string.pref_key_daily_update_time_picker);
            bindPreferenceSummaryToValue(findPreference(timePickerKey));
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item)
        {
            final int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onApplyThemeResource(final Resources.Theme theme, final int resId,
                                                  final boolean first)
    {
        theme.applyStyle(resId, true);
    }
}
