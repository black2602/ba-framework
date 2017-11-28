package com.blackangel.baframework.ui.dialog.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;

import java.io.Serializable;

/**
 *
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 *
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 *
 * @author antoine vianey
 *
 */
public class BaseCustomDialog extends Dialog implements Serializable {
	public BaseCustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public BaseCustomDialog(Context context) {
		super(context);
	}

	public enum DisplayOrientation {
		CENTER, BOTTOM, FULL;

		public static DisplayOrientation valueOf(int i) {
			if(i == 0) {
				return CENTER;
			} else if(i == 1) {
				return BOTTOM;
			} else if(i == 2) {
				return FULL;
			}

			return CENTER;
		}
	}


	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {
		private Context context;
		private int titleIconResId;
		private String title;
		private boolean titleBarHidden = false;
		private boolean showCloseBtn;
		private CharSequence message;
		private String positiveButtonText;
		private String neutralButtonText;
		private String negativeButtonText;
		private View contentView;
		private int contentViewHeight;
		private DisplayOrientation displayOrientation;

		private boolean mbCancelable = true;
		private boolean dismissOnButtonClick = true;

		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener,
				neutralButtonClickListener;

		private DialogInterface.OnClickListener mCloseClickListener;


		public Builder(Context context)
		{
			this.context = context;
		}

		public Builder(Activity context)
		{
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 *
		 * @param message
		 * @return
		 */
		public Builder setMessage(CharSequence message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

//		public Builder setMessage(Spanned msgHtml) {
//			this.msgHtml = msgHtml;
//			return this;
//		}

		public Builder setCancelable(boolean cancel) {
			mbCancelable = cancel;
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			if(title > 0) {
				this.title = (String) context.getText(title);
			}
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(String title, int titleIconResId) {
			this.titleIconResId = titleIconResId;
			return setTitle(title);
		}

		public Builder setTitleBarHidden() {
			this.titleBarHidden = true;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 *
		 * @param v
		 * @param viewHeight
		 * @return
		 */
		public Builder setView(View v, int viewHeight) {
			this.contentViewHeight = viewHeight;
			return setView(v);
		}

		public Builder setView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setDisplayOrientation(DisplayOrientation displayOrientation) {
			this.displayOrientation = displayOrientation;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 *
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 *
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 *
		 * @param neutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(int neutralButtonText, DialogInterface.OnClickListener listener) {
			this.neutralButtonText = (String) context.getText(neutralButtonText);
			this.neutralButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 *
		 * @param neutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener) {
			this.neutralButtonText = neutralButtonText;
			this.neutralButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 *
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 *
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setDismissOnButtonClick(boolean dismissOnButtonClick) {
			this.dismissOnButtonClick = dismissOnButtonClick;
			return this;
		}

		public Builder setCloseButton(boolean show, DialogInterface.OnClickListener listener) {
			this.showCloseBtn = show;
			this.mCloseClickListener = listener;

			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public BaseCustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme

			BaseCustomDialog dialog = null;

			View layout = null;
			if(displayOrientation == DisplayOrientation.FULL) {
				dialog = new BaseCustomDialog(context, R.style.CustomDialog_FullScreen);
				layout = inflater.inflate(R.layout.base_custom_dialog_full, null);

			} else if (displayOrientation == DisplayOrientation.BOTTOM) {
				dialog = new BaseCustomDialog(context, R.style.CustomDialog_Bottom);
				layout = inflater.inflate(R.layout.base_custom_dialog_bottom, null);

				Window window = dialog.getWindow();
				WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.BOTTOM;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);

			} else {
				dialog = new BaseCustomDialog(context, R.style.CustomDialog);
				layout = inflater.inflate(R.layout.base_custom_dialog, null);
			}

//			dialog.getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation;

			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			// set the dialog title
			if (title != null) {
				((TextView) layout.findViewById(R.id.title)).setText(title);
			}
			else if(titleBarHidden) {
				layout.findViewById(R.id.layout_title).setVisibility(View.GONE);
			}
			else {
				ImageView titleIcon = ((ImageView) layout.findViewById(R.id.title_img));
				titleIcon.setVisibility(View.VISIBLE);
			}

			// 닫기 버튼 설정 되어있으면
			if(showCloseBtn) {
				ViewGroup btnClose = (ViewGroup) layout.findViewById(R.id.btn_close).getParent();
				btnClose.setVisibility(View.VISIBLE);
				final BaseCustomDialog finalDialog3 = dialog;
				btnClose.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (mCloseClickListener != null) {
							mCloseClickListener.onClick(finalDialog3, DialogInterface.BUTTON_POSITIVE);
						}

						finalDialog3.dismiss();
					}
				});
			}

			ViewGroup sb_dialog_button_layout = (ViewGroup) layout.findViewById(R.id.layout_btns1);

			if (positiveButtonText == null && neutralButtonText == null && negativeButtonText == null) {
				// 버튼 없을 때 버튼 레이아웃 숨김
				sb_dialog_button_layout.setVisibility(View.GONE);

			} else {
				if(positiveButtonText != null && neutralButtonText != null && negativeButtonText != null) {
					// 세개 버튼이 모두 있을 때는 layout2 를 씀
					sb_dialog_button_layout.setVisibility(View.GONE);
				} else {
					// 세개 버튼 중 하나라도 없으면 layout1 을 씀
					sb_dialog_button_layout.setVisibility(View.VISIBLE);
				}
			}

			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.dialog_button3)).setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					final BaseCustomDialog finalDialog = dialog;
					((Button) layout.findViewById(R.id.dialog_button3))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(finalDialog, DialogInterface.BUTTON_POSITIVE);
									MyLog.i("dismissOnButtonClick = " + dismissOnButtonClick);
									if(dismissOnButtonClick)
										finalDialog.dismiss();
								}
							});
				}

			}
			else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.dialog_button3).setVisibility(View.GONE);
			}

			// set the confirm button
			if (neutralButtonText != null) {
				((Button) layout.findViewById(R.id.dialog_button2)).setText(neutralButtonText);
				if (neutralButtonClickListener != null) {
					final BaseCustomDialog finalDialog1 = dialog;
					((Button) layout.findViewById(R.id.dialog_button2))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									neutralButtonClickListener.onClick(finalDialog1, DialogInterface.BUTTON_NEUTRAL);
									MyLog.i("dismissOnButtonClick = " + dismissOnButtonClick);
									if(dismissOnButtonClick)
										finalDialog1.dismiss();
								}
							});
				}
			}
			else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.dialog_button2).setVisibility(View.GONE);
			}

			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.dialog_button1)).setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					final BaseCustomDialog finalDialog2 = dialog;
					((Button) layout.findViewById(R.id.dialog_button1))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(finalDialog2, DialogInterface.BUTTON_NEGATIVE);
									MyLog.i("dismissOnButtonClick = " + dismissOnButtonClick);
									if(dismissOnButtonClick)
										finalDialog2.dismiss();
								}
							});
				}
			}
			else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.dialog_button1).setVisibility(View.GONE);
			}

			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.content)).setText(message);

			}

			else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.layout_content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.layout_content)).addView(contentView, new LayoutParams(
						LayoutParams.MATCH_PARENT, contentViewHeight > 0 ? contentViewHeight : LayoutParams.WRAP_CONTENT));
			}

			dialog.setCancelable(mbCancelable);
			dialog.setCanceledOnTouchOutside(mbCancelable);

			return dialog;
		}
	}
}