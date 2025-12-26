package com.optimalsolutions.fadfed.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by ninetynine99 on 27/05/16.
 */
public class FadfedAlertDialog extends AlertDialog {

    public FadfedAlertDialog(Context context){
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    //        return new android.app.AlertDialog.Builder(getActivity())
    //                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener(){
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //
    //                        dialog.dismiss();
    //
    //
    //                    }
    //                }).create();
    }
}
