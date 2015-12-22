package com.ubi.jason.sensorcollect.helper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.ubi.jason.sensorcollect.R;
import com.ubi.jason.sensorcollect.interfaces.DialogListener;

/**
 * Created by jasoncosta on 12/8/2015.
 */
public class CustomDialog extends DialogFragment {

    static final int DIALOG_OK = -1;
    static final int DIALOG_CANCEL = -2;
    private static final String TAG = "DIALOG";
    private EditText input;
    private NumberPicker picker;
    private Spinner spinner;
    private String spinnerValue;
    private int position;

    public CustomDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        position = args.getInt("position") - 1;
        String value = args.getString("value");
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Set the dialog title
        String title = context.getResources().getStringArray(R.array.drawer_items)[position];
        if (position == 0) {
            input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            if (!value.equals("")) {
                input.setText(value);
            }
            builder.setView(input);
        } else if (position == 1) {
            picker = new NumberPicker(getActivity());
            picker.setMinValue(20);
            picker.setMaxValue(200);
            if (!value.equals("")) {
                picker.setValue(Integer.parseInt(value));
            }
            builder.setView(picker);
        } else if (position == 2) {
            picker = new NumberPicker(getActivity());
            picker.setMinValue(50);
            picker.setMaxValue(300);
            if (!value.equals("")) {
                picker.setValue(Integer.parseInt(value));
            }
            builder.setView(picker);
        } else if (position == 3) {
            picker = new NumberPicker(getActivity());
            picker.setMinValue(0);
            picker.setMaxValue(100);
            if (!value.equals("")) {
                picker.setValue(Integer.parseInt(value));
            }
            builder.setView(picker);
        } else if (position == 4) {
            spinner = new Spinner(getActivity());
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.sexo, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerValue = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            builder.setView(spinner);
        }
        builder.setTitle(title)
                .setPositiveButton(R.string.ok, new DialogClickListener())
                .setNegativeButton(R.string.cancel, new DialogClickListener());
        return builder.create();
    }

    private class DialogClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(android.content.DialogInterface dialog, int id) {
            if (id == DIALOG_OK) {
                DialogListener dl = (DialogListener) getActivity();
                if (input!=null) {
                    dl.onDimiss(id, position, input.getText().toString());
                } else if (picker!=null) {
                    dl.onDimiss(id, position, String.valueOf(picker.getValue()));
                } else if (spinner!=null) {
                    dl.onDimiss(id, position, spinnerValue);
                }
            } else {
                dialog.dismiss();
            }
        }
    }
}
