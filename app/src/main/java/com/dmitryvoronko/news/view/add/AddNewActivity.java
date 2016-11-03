package com.dmitryvoronko.news.view.add;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.userinput.Status;

public final class AddNewActivity extends AppCompatActivity
{
    public static final String CHANNEL_LINK = "CHANNEL_LINK";
    private final NewsFacade newsFacade;
    private EditText newItemEditText;

    public AddNewActivity()
    {
        super();
        newsFacade = new NewsFacade(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        newItemEditText = (EditText) findViewById(R.id.new_item_edit_text);
        final Button addButton = (Button) findViewById(R.id.add_new_item_button);

        addButton.setOnClickListener(getAddNewItemListener(newItemEditText));

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        if (data != null)
        {
            newItemEditText.setText(data.toString());
        }

    }

    @NonNull private View.OnClickListener getAddNewItemListener(final EditText newItemEditText)
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                hideKeyboard();
                final String userInput = newItemEditText.getText().toString();
                if (!userInput.equals(""))
                {

                    final ProgressDialog progressDialog = getProgressDialogWithTitle();
                    progressDialog.show();

                    final Thread thread = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            final Status status = newsFacade.requestAddNewChannel(userInput);
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    progressDialog.dismiss();
                                    handleStatus(status);
                                }
                            });
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        };
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

    private void handleStatus(final Status status)
    {
        switch (status)
        {
            case ADDED:
                showSnackbar(R.string.channel_successfully_added);
                handleChannelAdded();
                break;
            case NO_INTERNET_CONNECTION:
                break;
            case NOT_URL:
                showSnackbar(R.string.input_string_is_not_link);
                break;
            case NOT_XML:
                showSnackbar(R.string.input_link_are_not_xml);
                break;
            case HAS_ALREADY:
                showSnackbar(R.string.already_exists);
                break;
            case TOTAL_ERROR:
                break;
            default:
                break;
        }
    }

    private void showSnackbar(final int resId)
    {
        final View currentFocus = getCurrentFocus();
        if (currentFocus != null)
        {
            Snackbar.make(currentFocus, resId, Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleChannelAdded()
    {
        final String newItem = newItemEditText.getText().toString();
        final Intent result = new Intent();
        result.putExtra(CHANNEL_LINK, newItem);
        setResult(RESULT_OK, result);
        finish();
    }
}
