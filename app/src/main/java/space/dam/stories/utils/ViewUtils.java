package space.dam.stories.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.transition.Fade;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import space.dam.stories.R;

/**
 * Класс, описывающий некоторые свойства view
 */

public class ViewUtils {

    public static void showToast(Context context, int id) {
        Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View v, int id) {
        Snackbar.make(v, id, Snackbar.LENGTH_SHORT).show();
    }

    public static class Button {

        private static final int DURATION = 300;

        public static void setDisabled(Context context, android.widget.Button button) {
            animate(context, button, R.drawable.button_neutral_bg, R.color.gray_cc);
        }

        public static void setDefault(Context context, android.widget.Button button) {
            animate(context, button, R.drawable.button_bg, R.color.colorAccent);
        }

        public static void setAccent(Context context, android.widget.Button button) {
            animate(context, button, R.drawable.button_accent_bg, R.color.white);
        }

        private static void animate(Context context, android.widget.Button button,
                                    int idDrawableTo, int idTextColorTo) {

            // Animate background
            Drawable[] layers = new Drawable[2];
            layers[0] = button.getBackground();
            layers[1] = context.getDrawable(idDrawableTo);

            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
            button.setBackground(transitionDrawable);

            // Animate text color
            int colorFrom = button.getCurrentTextColor();
            int colorTo = ContextCompat.getColor(context, idTextColorTo);
            ObjectAnimator colorAnim = ObjectAnimator.ofInt(button, "textColor",
                    colorFrom, colorTo);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(DURATION);

            // Start animation
            transitionDrawable.startTransition(DURATION);
            colorAnim.start();
        }
    }

    public static void animVisibility(View view, int newState) {
        Transition transition = new Fade();
        transition.setDuration(300);
        transition.addTarget(view);

        view.setVisibility(newState);
        if (newState == View.VISIBLE) {
            view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in));
        }
        else view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out));
    }

    public static void updateProfilePhoto(String url, ImageView into) {
        if (TextUtils.isEmpty(url)) {
            Drawable drawable = ContextCompat.getDrawable(into.getContext(), R.drawable.photo_default);
            into.setImageDrawable(drawable);
            return;
        }

        Glide.with(into).load(url)
                .centerCrop()
                .error(R.drawable.photo_default)
                .placeholder(R.color.gray_cc)
                .into(into);
    }

}
