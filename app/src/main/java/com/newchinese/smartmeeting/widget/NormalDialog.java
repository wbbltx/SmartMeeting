package com.newchinese.smartmeeting.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;

/**
 * Created by Administrator on 2017/11/21 0021.
 * 无输入功能的提示弹框
 */

public class NormalDialog extends Dialog {
    public NormalDialog(@NonNull Context context) {
        super(context);
    }

    public NormalDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {


        private Context context;
        private boolean isCancelable;
        private String title;
        private View contentView;
        private String positiveButtonText;
        private OnClickListener positiveButtonClickListener;
        private String negativeButtonText;
        private OnClickListener negativeButtonClickListener;
        private String content;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelableMethod(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(int content) {
            this.content = (String) context.getText(content);
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
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

        public NormalDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NormalDialog dialog = new NormalDialog(context, R.style.Dialog);
            View inflate = inflater.inflate(R.layout.layout_hisinfo_dialog, null);
            dialog.addContentView(inflate, new ViewGroup.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (title != null)
                ((TextView) inflate.findViewById(R.id.tv_title)).setText(title);

            if (content != null)
                ((TextView) inflate.findViewById(R.id.etInput)).setText(content);

            if (negativeButtonText != null) {
                ((TextView) inflate.findViewById(R.id.negativeButton)).setText(negativeButtonText);
            }
            if (negativeButtonClickListener != null) {
                inflate.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            if (positiveButtonText != null) {
                ((TextView) inflate.findViewById(R.id.positiveButton)).setText(positiveButtonText);
            }

            if (positiveButtonClickListener != null) {
                inflate.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            dialog.setContentView(inflate);
            dialog.setCancelable(isCancelable);
            return dialog;
        }
    }
}
