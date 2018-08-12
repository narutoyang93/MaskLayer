package com.example.naruto.masklayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/7/27 0027
 * @Note
 */
public class DialogUtils {
    public static final SparseArray<Dialog> progressDialogSparseArray = new SparseArray<>();

    /**
     * 显示消息弹窗
     *
     * @param context
     * @param title
     * @param message
     * @param isCancelable     //点击弹窗区域外是否自动消失
     * @param okButtonText
     * @param cancelButtonText
     * @param OnOkClick
     * @param onCancelClick
     */
    public static Dialog showDialog(Context context, String title, String message, boolean isCancelable,
                                    String okButtonText, String cancelButtonText,
                                    View.OnClickListener OnOkClick, View.OnClickListener onCancelClick) {

        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.general_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();

        dialog.setCancelable(isCancelable);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        LinearLayout titleLayout = (LinearLayout) layout.findViewById(R.id.title);
        TextView messageTextView = (TextView) layout.findViewById(R.id.message);
        Button okButton = (Button) layout.findViewById(R.id.okButton);
        Button cancelButton = (Button) layout.findViewById(R.id.cancelButton);
        View line = layout.findViewById(R.id.line2);

        if (TextUtils.isEmpty(title)) {// 没有title
            titleLayout.setVisibility(View.GONE);
        } else {
            TextView titleTextView = (TextView) titleLayout.getChildAt(0);
            titleTextView.setText(title);
        }

        messageTextView.setText(message);// 设置弹窗消息内容

        // 没有按钮
        if (TextUtils.isEmpty(cancelButtonText) && TextUtils.isEmpty(okButtonText)) {
            LinearLayout buttonLayout = (LinearLayout) layout.findViewById(R.id.button);
            buttonLayout.setVisibility(View.GONE);
        } else {
            if (TextUtils.isEmpty(cancelButtonText)) {// 没有取消按钮
                line.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            } else {
                cancelButton.setText(cancelButtonText);
                cancelButton.setOnClickListener(new DefaultClickListener(dialog, onCancelClick));
            }
            okButton.setText(okButtonText);
            okButton.setOnClickListener(new DefaultClickListener(dialog, OnOkClick));
        }

        return dialog;
    }

    /**
     * @param activity
     * @param isCancelable
     */
    public static void showMyProgressDialog(final Activity activity, final boolean isCancelable) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int activityHashCode = activity.hashCode();
                    Dialog dialog = progressDialogSparseArray.get(activityHashCode);
                    if (dialog == null) {
                        dialog = new AlertDialog.Builder(activity, R.style.transDialog).create();
                        RelativeLayout relativeLayout = new RelativeLayout(activity);
                        relativeLayout.setGravity(Gravity.CENTER);
                        relativeLayout.addView(new ProgressBar(activity));
                        dialog.setCancelable(isCancelable);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setContentView(relativeLayout);
                        progressDialogSparseArray.put(activityHashCode, dialog);
                    } else if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * 显示遮罩层
     *
     * @param activity
     * @return
     */
    public static void showProgressMasklayer(Activity activity) {
        showMyProgressDialog(activity, false);
    }

    /**
     * @param activity
     */
    public static void dismissDialog(Activity activity) {
        final Dialog dialog = progressDialogSparseArray.get(activity.hashCode());
        if (dialog != null && dialog.isShowing() && activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * 默认点击事件处理接口
     */
    public static class DefaultClickListener implements View.OnClickListener {
        private Dialog dialog;
        private View.OnClickListener onClickListener;

        public DefaultClickListener(Dialog dialog, View.OnClickListener onClickListener) {
            this.dialog = dialog;
            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }
}
