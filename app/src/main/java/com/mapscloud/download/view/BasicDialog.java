package com.mapscloud.download.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapscloud.download.R;


public class BasicDialog extends Dialog {

    private BasicDialog(Context context) {
        super(context, R.style.BasicDialog);
    }

    public static class Builder {
        private Context context;
        private CharSequence title;
        private CharSequence message;
        private CharSequence negativeButtonText;
        private CharSequence neutralButtonText;
        private CharSequence positiveButtonText;
        private OnClickListener negativeButtonListener;
        private OnClickListener neutralButtonListener;
        private OnClickListener positiveButtonListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int titleId) {
            this.title = context.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            this.message = context.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setNegativeButton(int textId, OnClickListener listener) {
            this.negativeButtonText = context.getText(textId);
            this.negativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text,
                OnClickListener listener) {
            this.negativeButtonText = text;
            this.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, OnClickListener listener) {
            this.neutralButtonText = context.getText(textId);
            this.neutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text,
                OnClickListener listener) {
            this.neutralButtonText = text;
            this.neutralButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(int textId, OnClickListener listener) {
            this.positiveButtonText = context.getText(textId);
            this.positiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text,
                OnClickListener listener) {
            this.positiveButtonText = text;
            this.positiveButtonListener = listener;
            return this;
        }

        public BasicDialog create() {
            final BasicDialog dialog = new BasicDialog(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.basic_dialog_layout, null);

            if (!TextUtils.isEmpty(title)) {
                ((TextView) view.findViewById(android.R.id.title))
                        .setText(title);
            }

            if (!TextUtils.isEmpty(message)) {
                ((TextView) view.findViewById(android.R.id.message))
                        .setText(message);
            }

            View.OnClickListener onClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                    case android.R.id.button1:
                        if (null != negativeButtonListener) {
                            negativeButtonListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                        break;
                    case android.R.id.button2:
                        if (null != neutralButtonListener) {
                            neutralButtonListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEUTRAL);
                        }
                        break;
                    case android.R.id.button3:
                        if (null != positiveButtonListener) {
                            positiveButtonListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                        break;
                    default:
                        break;
                    }
                    dialog.dismiss();
                }

            };

            if (!TextUtils.isEmpty(negativeButtonText)) {
                Button button = (Button) view
                        .findViewById(android.R.id.button1);
                button.setText(negativeButtonText);
                button.setOnClickListener(onClickListener);
            } else {
                view.findViewById(android.R.id.button1)
                        .setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(neutralButtonText)) {
                Button button = (Button) view
                        .findViewById(android.R.id.button2);
                button.setText(neutralButtonText);
                button.setOnClickListener(onClickListener);
            } else {
                view.findViewById(android.R.id.button2)
                        .setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(positiveButtonText)) {
                Button button = (Button) view
                        .findViewById(android.R.id.button3);
                button.setText(positiveButtonText);
                button.setOnClickListener(onClickListener);
            } else {
                view.findViewById(android.R.id.button3)
                        .setVisibility(View.GONE);
            }

            dialog.setContentView(view);
            return dialog;
        }
    }

}
