package jp.obanet.gpsreminder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {
	
	private String message;
	private boolean usePositiveButton = false;
	private boolean useNegativeButton = false;
	private String positiveButtonLabel;
	private String negativeButtonLabel;
	private DialogInterface.OnClickListener positiveOnclickListener;
	private DialogInterface.OnClickListener negativeOnclickListener;
	
	public MessageDialogFragment setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public MessageDialogFragment setPositiveButton(String val, DialogInterface.OnClickListener listener){
		usePositiveButton = true;
		positiveButtonLabel = val;
		positiveOnclickListener = listener;
		return this;
	}
	
	public MessageDialogFragment setNegativeButton(String val, DialogInterface.OnClickListener listener){
		useNegativeButton = true;
		negativeButtonLabel = val;
		negativeOnclickListener = listener;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 
        builder.setMessage(message);
        
        if(usePositiveButton){
        	builder.setPositiveButton(positiveButtonLabel, positiveOnclickListener);
        }else{
        	builder.setPositiveButton(R.string.YES, null);
        }
        
        if(useNegativeButton){
        	builder.setNegativeButton(negativeButtonLabel, negativeOnclickListener);
        }
        
        return builder.create();
	}
}
