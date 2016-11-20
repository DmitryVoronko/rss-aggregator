package com.dmitryvoronko.news.view.addnew;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.userinput.Status;
import com.dmitryvoronko.news.services.AddNewService;
import com.dmitryvoronko.news.view.util.SnackbarHelper;
import com.dmitryvoronko.news.util.log.Logger;

import lombok.AllArgsConstructor;
import lombok.Data;

public final class AddNewActivity extends AppCompatActivity
{
    private static final String TAG = "AddNewActivity";

    private final LastUserInput lastUserInput = new LastUserInput("", Status.NOTHING);

    private EditText inputLink;
    private TextInputLayout inputLayoutLink;

    private ProgressDialog progressDialog;
    private final BroadcastReceiver statusReceiver = new BroadcastReceiver()
    {
        @Override public void onReceive(final Context context, final Intent intent)
        {
            if (intent.getAction().equalsIgnoreCase(AddNewService.ACTION_ADD_NEW_CHANNEL_STATUS))
            {
                final String stringStatus =
                        intent.getStringExtra(AddNewService.EXTRA_ADD_NEW_CHANNEL_STATUS);
                final Status status = Status.valueOf(stringStatus);
                progressDialog.dismiss();
                lastUserInput.setStatus(status);
                handleStatus(status);
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_new);

            initViewComponents();

            initStatusReceiver();

            readIntentData();
        } catch (final Exception e)
        {
            Logger.e(TAG, "onCreate", e);
        }
    }

    private void initViewComponents()
    {
        inputLink = (EditText) findViewById(R.id.input_link);
        inputLayoutLink = (TextInputLayout) findViewById(R.id.input_layout_link);
        inputLink.addTextChangedListener(new LinkTextWatcher());
        final Button addButton = (Button) findViewById(R.id.add_new_item_button);
        progressDialog = getProgressDialogWithTitle();
        addButton.setOnClickListener(getAddNewItemListener());
    }

    private void initStatusReceiver()
    {
        final IntentFilter intentFilter =
                new IntentFilter(AddNewService.ACTION_ADD_NEW_CHANNEL_STATUS);
        registerReceiver(statusReceiver, intentFilter);
    }

    private void readIntentData()
    {
        final Intent intent = getIntent();
        final Uri data = intent.getData();

        if (data != null)
        {
            inputLink.setText(data.toString());
        }
    }

    private ProgressDialog getProgressDialogWithTitle()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.adding_dialog_title);
        final String message = getResources().getString(R.string.adding_dialog_message);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    @NonNull private View.OnClickListener getAddNewItemListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                requestAddNewChannelToService();
            }
        };
    }

    private void requestAddNewChannelToService()
    {
        final String userInput = inputLink.getText().toString();
        if (!userInput.equals(""))
        {
            if (!userInput.equalsIgnoreCase(lastUserInput.getInputString()))
            {
                lastUserInput.setInputString(userInput);
                hideKeyboard();
                progressDialog.show();
                AddNewService.startActionAddNewChannel(this, userInput);
            } else
            {
                handleStatus(lastUserInput.getStatus());
            }
        }
    }

    private void hideKeyboard()
    {
        final View view = this.getCurrentFocus();
        if (view != null)
        {
            final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void handleStatus(final Status status)
    {
        switch (status)
        {
            case ADDED:
                handleChannelAdded();
                break;
            case NO_INTERNET_CONNECTION:
                SnackbarHelper.showNoInternetConnectionSnackBar(this);
                break;
            case NOT_URL:
                say(R.string.input_string_is_not_link);
                break;
            case NOT_XML:
                say(R.string.input_link_are_not_xml);
                break;
            case ALREADY_EXISTS:
                say(R.string.already_exists);
                break;
            case TOTAL_ERROR:
                SnackbarHelper.showNoInternetConnectionSnackBar(this);
                break;
            case UNSUPPORTED_FORMAT:
                say(R.string.unsupported_channel_format);
                break;
            default:
                SnackbarHelper.showNoInternetConnectionSnackBar(this);
                break;
        }
    }

    private void handleChannelAdded()
    {
        final Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    private void say(final int resId)
    {
        SnackbarHelper.showSnackbar(this, resId, SnackbarHelper.NULL_ACTION_RES_ID,
                                    SnackbarHelper.NULL_ON_CLICK_LISTENER,
                                    SnackbarHelper.NULL_CALLBACK);
    }

    @Override protected void onPause()
    {
        super.onPause();
        unregisterReceiver(statusReceiver);
    }

    @Override protected void onResume()
    {
        super.onResume();
        initStatusReceiver();
    }

    private void validateLink()
    {
        final String channelLink = inputLink.getText().toString().trim();
        if (channelLink.isEmpty() || !isValidLink(channelLink))
        {
            inputLayoutLink.setError(getString(R.string.enter_valid_channel_link));
            requestFocus(inputLink);
        } else
        {
            inputLayoutLink.setErrorEnabled(false);
        }
    }

    private boolean isValidLink(final String channelLink)
    {
        return !TextUtils.isEmpty(channelLink) &&
                Patterns.WEB_URL.matcher(channelLink).matches();
    }

    private void requestFocus(final View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Data
    @AllArgsConstructor
    private final class LastUserInput
    {
        private String inputString;
        private Status status;
    }

    private class LinkTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                      final int after)
        {

        }

        @Override public void onTextChanged(final CharSequence s, final int start, final int before,
                                            final int count)
        {

        }

        @Override public void afterTextChanged(final Editable s)
        {
            validateLink();
        }
    }
}
