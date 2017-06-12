package cj_server.com.itmonitor.Pojo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import cj_server.com.itmonitor.R;

/**
 * Created by cj-sever on 4/12/17.
 */

public class RegistrationDialog extends DialogFragment {
    private LayoutInflater inflater;
    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.registration_dialog_layout,null);
        AlertDialog.Builder dialog  = new AlertDialog.Builder(getActivity());
        dialog.setView(view);

        dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return dialog.create();
    }
}
