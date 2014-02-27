package de.dala.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import de.dala.R;

/**
 * based on mobile4d
 * @author Daniel Langerenken
 *
 */
public class DialogUtilities {
	/**
	 * Shows a Dialog with a title, a message, a EditText and two Buttons for OK
	 * and Cancel. The user can't click OK when the EditText is empty.
	 * 
	 * @param context
	 *            The Context of the calling Activity
	 * @param title
	 *            Title of the Dialog
	 * @param message
	 *            Message of the Dialog
	 * @param inputEditText
	 *            The EditText used for this Dialog. You can modify it for
	 *            example by setting the input type before passing it to this
	 *            method. You can also read the text from the calling method.
	 * @param onOkClickListener
	 *            The Listener for clicking on the OK button.
	 */
	public static void showDialog(Context context, String title,
			String message, EditText inputEditText,
			DialogInterface.OnClickListener onOkClickListener) {

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setView(inputEditText);

		/*
		 * OK button
		 */
		alert.setPositiveButton(context.getString(R.string.ok),
				onOkClickListener);
		/*
		 * Cancel button
		 */
		alert.setNegativeButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/*
						 * Canceled.
						 */
					}
				});

		final AlertDialog dialog = alert.show();

		/*
		 * Disable ok button if no default value is inserted
		 */
		if (inputEditText.getText().length() == 0) {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		}
		/*
		 * Dis- and enable the text
		 */
		inputEditText.addTextChangedListener(new TextWatcher() {
			/**
			 * Enable OK button if text entered, disable otherwise.
			 */
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
						!s.toString().equals(""));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}
}
