package jp.obanet.gpsreminder;

import jp.obanet.gpsreminder.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class InputPlaceDialogFragment extends DialogFragment {
	
	public interface InputPlaceDialogListener{
		public void onInputPlaceDialogPositiveClick(Dialog dialog);
		public void onInputPlaceDialogNegativeClick(Dialog dialog);
	}

	InputPlaceDialogListener listener;
	AlertDialog inputPlaceDialog;
	private CheckedPlace checkedPlace;
	public static final int SEEK_BAR_RATIO = 50;
	public static final int SEEK_BAR_ADDITIONAL = 100;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// AlertDoalog.Builderインスタンス生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 
        // LayoutInflaterインスタンス生成
        LayoutInflater inflater = getActivity().getLayoutInflater();
 
        // InfalterメソッドでViewインスタンス(dialog_signin.xml)生成
        // Dialogに生成したViewインスタンスをカスタムViewとして加える
        builder.setView(inflater.inflate(R.layout.input_place_info_dialog, null))
 
                // PositiveButton（OKボタン）追加
                .setPositiveButton(R.string.regist, null)
                // NegativeButton（Cancelボタン）追加
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                listener.onInputPlaceDialogNegativeClick(inputPlaceDialog);
                            }
                        });
        
        inputPlaceDialog = builder.show();
        Button positiveButton = inputPlaceDialog.getButton( DialogInterface.BUTTON_POSITIVE );
        positiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = ((EditText)inputPlaceDialog.findViewById(R.id.placeName)).getText().toString();
				if(name == null || name.trim().length() == 0){
					Toast.makeText(InputPlaceDialogFragment.this.getActivity(), getText(R.string.name_not_blank), Toast.LENGTH_SHORT).show();
					return;
				}
				
				inputPlaceDialog.dismiss();
                listener.onInputPlaceDialogPositiveClick(inputPlaceDialog);
			}
        });
        
        SeekBar distanceBar = (SeekBar)inputPlaceDialog.findViewById(id.distance);
        final TextView distanceValue = (TextView)inputPlaceDialog.findViewById(id.distanceValue);
        distanceBar.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						distanceValue.setText((progress * SEEK_BAR_RATIO + SEEK_BAR_ADDITIONAL) + " m");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
                	
                }
        );
        
        if(checkedPlace != null){
        	setParameters();
        }
        
        return inputPlaceDialog;
	}
	
	public void setCheckedPlace(CheckedPlace checkedPlace){
		this.checkedPlace = checkedPlace;
	}
	
	private void setParameters(){
		if(checkedPlace != null){
			((EditText)inputPlaceDialog.findViewById(id.placeName)).setText(checkedPlace.getName());
			((EditText)inputPlaceDialog.findViewById(id.placeMemo)).setText(checkedPlace.getMemo());
			((SeekBar)inputPlaceDialog.findViewById(id.distance)).setProgress((checkedPlace.getDistance() - SEEK_BAR_ADDITIONAL) / SEEK_BAR_RATIO);
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InputPlaceDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewPlaceDialogListener");
        }
    }
}
