package com.dmitryvoronko.news.view.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 *
 * Created by Dmitry on 20/11/2016.
 */

public final class TimePreference extends DialogPreference
{
    private static final String DEFAULT_RETURN_VALUE = "00:00";
    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;

    public TimePreference(final Context context,
                          final AttributeSet attrs)
    {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView()
    {
        picker = new TimePicker(getContext());

        return (picker);
    }

    @Override
    protected void onBindDialogView(final View v)
    {
        super.onBindDialogView(v);

        //noinspection deprecation
        picker.setCurrentHour(lastHour);
        //noinspection deprecation
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (positiveResult)
        {
            //noinspection deprecation
            lastHour = picker.getCurrentHour();
            //noinspection deprecation
            lastMinute = picker.getCurrentMinute();

            final String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);

            if (callChangeListener(time))
            {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index)
    {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue,
                                     final Object defaultValue)
    {
        final String time;
        if (restoreValue)
        {
            if (defaultValue == null)
            {
                time = getPersistedString(DEFAULT_RETURN_VALUE);
            } else
            {
                time = getPersistedString(defaultValue.toString());
            }
        } else
        {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }

    public static int getHour(final String time)
    {
        final String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(final String time)
    {
        final String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }
}
