package com.newchinese.smartmeeting.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;

/**
 * Description:
 * author         xulei
 * Date           2017/8/24 17:02
 */
public class FirstTimeHintDialog extends Dialog {

    private static View layout;

    public FirstTimeHintDialog(Context context) {
        super(context);
    }

    public FirstTimeHintDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String inputText;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private EditText etInput;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private boolean isCancelable = true;
        private boolean isShowTitle = true;

        public Builder(Context context) {
            this.context = context;
        }

        public void setInputText(String inputText) {
            this.inputText = inputText;
        }

        public String getInputText() {
            return etInput.getText().toString();
        }

        public Builder setCancelableMethod(Boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setIsShowTitle(Boolean isShowTitle) {
            this.isShowTitle = isShowTitle;
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
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

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public FirstTimeHintDialog createDoubleButton() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final FirstTimeHintDialog dialog = new FirstTimeHintDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.first_time_hint, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    GridLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

//            Button titleText = ((Button) layout.findViewById(R.id.));
//            etInput = (EditText) layout.findViewById(R.id.etInput);
//            etInput.setText(inputText);
//            titleText.setText(title);

//            if (!isShowTitle) {
//                titleText.setVisibility(View.GONE);
//            } else {
//                titleText.setVisibility(View.VISIBLE);
//            }
//            LinearLayout double_button = (LinearLayout) layout.findViewById(R.id.double_button);
//            double_button.setVisibility(View.VISIBLE);

//            if (positiveButtonText != null) {
//                ((Button) layout.findViewById(R.id.bt_iknow))
//                        .setText(positiveButtonText);
            if (positiveButtonClickListener != null) {
                layout.findViewById(R.id.bt_iknow)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                positiveButtonClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
                        });
            }
//            } else {
//                layout.findViewById(R.id.positiveButton).setVisibility(
//                        View.GONE);
//            }
            // set the cancel button
//            if (negativeButtonText != null) {
//                ((Button) layout.findViewById(R.id.negativeButton))
//                        .setText(negativeButtonText);
//                if (negativeButtonClickListener != null) {
//                    layout.findViewById(R.id.negativeButton)
//                            .setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//                                    negativeButtonClickListener.onClick(dialog,
//                                            DialogInterface.BUTTON_NEGATIVE);
//                                }
//                            });
//                }
//            } else {
//                layout.findViewById(R.id.negativeButton).setVisibility(
//                        View.GONE);
//            }
            if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);
            dialog.setCancelable(isCancelable);//设置点击空白处消失不消失
            return dialog;
        }
    }
}
