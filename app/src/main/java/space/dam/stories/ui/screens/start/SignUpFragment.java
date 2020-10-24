package space.dam.stories.ui.screens.start;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import space.dam.stories.R;
import space.dam.stories.viewmodels.SignUpViewModel;

public class SignUpFragment extends Fragment {

    public static final String EXTRA_STEP = "SignUpFragment.EXTRA_STEP";

    /**
     * Views
     */
    private TextView title;
    private TextView description;
    private TextInputEditText input;
    private InputListener inputListener;
    private TextWatcher textListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (inputListener != null) {
                inputListener.onChanged(s.toString());
            }
        }
    };

    private SignUpFragment() { }

    public static SignUpFragment newInstance(int step) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_STEP, step);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        initViews(v);
        setContent(getArguments().getInt(EXTRA_STEP));
        return v;
    }

    /**
     * Инициализация всех view
     */
    private void initViews(View v) {
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        input = v.findViewById(R.id.input); input.addTextChangedListener(textListener);
    }

    /**
     * Установка контента в соответствии с шагом
     */
    private void setContent(int step) {
        switch (step) {
            case SignUpViewModel.STEP_EMAIL:
                title.setText(R.string.email);
                description.setVisibility(View.GONE);
                input.setHint(R.string.enter_email);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case SignUpViewModel.STEP_PASSWORD:
                title.setText(R.string.password);
                description.setText(R.string.create_password_description);
                input.setHint(R.string.enter_password);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case SignUpViewModel.STEP_NAME:
                title.setText(R.string.what_is_your_name);
                description.setVisibility(View.GONE);
                input.setHint(R.string.enter_name);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                break;
        }
    }

    /**
     * Интерфейс коллбека
     */
    public interface InputListener {
        void onChanged(String input);
    }

    /**
     * Установка коллбека
     */
    public void setInputListener(InputListener listener) {
        inputListener = listener;
    }

}
