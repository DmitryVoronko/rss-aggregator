package com.dmitryvoronko.news.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
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
import com.dmitryvoronko.news.ui.util.SnackbarHelper;
import com.dmitryvoronko.news.util.log.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;
import lombok.Data;

public final class AddNewActivity extends ActivityBase
{
    private static final String TAG = "AddNewActivity";
    public final static int ACTION_SHOW_NEW_ITEM_ACTIVITY = 41;

    private final LastUserInput lastUserInput = new LastUserInput("", Status.NOTHING);

    @BindView(R.id.input_link)
    protected EditText inputLink;
    @BindView(R.id.input_layout_link)
    protected TextInputLayout inputLayoutLink;

    private final ServiceConnection addNewServiceConnection = createServiceConnection();

    private AddNewService addNewService;

    private ProgressDialog progressDialog;
    private final BroadcastReceiver statusReceiver = new BroadcastReceiver()
    {
        @Override public void onReceive(final Context context, final Intent intent)
        {
            if (intent.getAction().equals(AddNewService.ACTION_ADD_NEW_CHANNEL_STATUS))
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

    public static void startAddNewItemActivity(final FragmentActivity fragmentActivity)
    {
        final Intent intent = new Intent(fragmentActivity, AddNewActivity.class);
        fragmentActivity.startActivityForResult(intent, ACTION_SHOW_NEW_ITEM_ACTIVITY);
    }

    @Override protected void doOnCreate(final Bundle savedInstanceState)
    {
        try
        {
            setContentView(R.layout.activity_add_new);

            ButterKnife.bind(this);

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
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override public void onCancel(final DialogInterface dialogInterface)
            {
                cancelAddingChannel();
            }
        });
        progressDialog.setTitle(R.string.adding_dialog_title);
        final String message = getResources().getString(R.string.adding_dialog_message);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    private void cancelAddingChannel()
    {
        addNewService.cancelAddNewChannel();
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
                say(R.string.status_can_not_connect_to_address);
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
        SnackbarHelper.showSnackbar(this, resId);
    }

    @Override protected void doOnPause()
    {
        unbindService(addNewServiceConnection);
        unregisterReceiver(statusReceiver);
    }

    @Override protected void doOnResume()
    {
        initStatusReceiver();
        final Intent intent = new Intent(this, AddNewService.class);
        bindService(intent, addNewServiceConnection, Context.BIND_AUTO_CREATE);
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

    private ServiceConnection createServiceConnection()
    {
        return new ServiceConnection()
        {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service)
            {
                final AddNewService.Binder binder = (AddNewService.Binder) service;
                addNewService = (binder.getService());
            }

            @Override public void onServiceDisconnected(final ComponentName name)
            {
                addNewService = null;
            }
        };
    }

    @Data
    @AllArgsConstructor
    private static final class LastUserInput
    {
        private String inputString;
        private Status status;
    }

    private final class LinkTextWatcher implements TextWatcher
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
