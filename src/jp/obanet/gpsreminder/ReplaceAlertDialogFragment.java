package jp.obanet.gpsreminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ReplaceAlertDialogFragment extends DialogFragment {

	public interface RepalaceAlertDialogLister{
		public void onReplaceAlertDialogPositiveClick(Dialog dialog);
		public void onReplaceAlertDialogNegativeClick(Dialog dialog);
	}

	RepalaceAlertDialogLister listener;
	AlertDialog replaceAlertDialog;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 
        builder.setMessage(R.string.replace_alert)
        	.setTitle(R.string.confirm)
            .setPositiveButton(R.string.YES,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onReplaceAlertDialogPositiveClick(replaceAlertDialog);
                        }
                    })
            .setNegativeButton(R.string.NO,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onReplaceAlertDialogNegativeClick(replaceAlertDialog);
                        }
                    });
        replaceAlertDialog = builder.create();
        return replaceAlertDialog;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (RepalaceAlertDialogLister) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RepalaceAlertDialogLister");
        }
    }
}
