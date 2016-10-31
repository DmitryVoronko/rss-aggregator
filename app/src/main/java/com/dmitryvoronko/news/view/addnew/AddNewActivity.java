package com.dmitryvoronko.news.view.addnew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dmitryvoronko.news.R;

public final class AddNewActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        final EditText newItemEditText = (EditText) findViewById(R.id.new_item_edit_text);
        final Button addButton = (Button) findViewById(R.id.add_new_item_button);

        addButton.setOnClickListener(getAddNewItemListener(newItemEditText));
    }

    @NonNull private View.OnClickListener getAddNewItemListener(final EditText newItemEditText)
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                final String newItem = newItemEditText.getText().toString();
                final Intent result = new Intent();
//                result.putExtra(Channel.KEY_CHANNEL_URL_COLUMN, newItem);
                setResult(RESULT_OK, result);
                finish();
            }
        };
    }
}
