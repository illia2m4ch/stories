package space.dam.stories.ui.screens.start;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import space.dam.stories.R;
import space.dam.stories.ui.screens.account.AccountSettingsActivity;
import space.dam.stories.viewmodels.SignUpViewModel;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.ui.screens.main.MainActivity;
import space.dam.stories.utils.ViewUtils;

public class SignUpActivity extends BaseActivity {

    /**
     * View models
     */
    private SignUpViewModel signUpViewModel;

    /**
     * Кнопки назад и вперед
     */
    private Button buttonBack;
    private View.OnClickListener buttonBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signUpViewModel.goBack();
        }
    };
    private Button buttonNext;
    private View.OnClickListener buttonNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signUpViewModel.goNext();
        }
    };

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setDarkSystemBar();
        initViewModels();
        initViews();
    }

    /**
     * Инициализация всех views
     */
    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(buttonBackClickListener);
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(buttonNextClickListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    /**
     * Инициализация всех viewModel
     */
    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        signUpViewModel = provider.get(SignUpViewModel.class);
        initSignUpViewModel();
    }

    /**
     * Инициализация signUpViewModel
     */
    private void initSignUpViewModel() {
        signUpViewModel.init(0);
        signUpViewModel.getCanGoNext().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean canGoNext) {
                if (signUpViewModel.isLastStep()) {
                    buttonNext.setText(getResources().getString(R.string.complete));
                }
                else {
                    buttonNext.setText(getResources().getString(R.string.next));
                }

                if (canGoNext) {
                    ViewUtils.Button.setAccent(SignUpActivity.this, buttonNext);
                }
                else ViewUtils.Button.setDisabled(SignUpActivity.this, buttonNext);
            }
        });

        signUpViewModel.setCallback(new SignUpViewModel.Callback() {
            @Override
            public void onClickSignUp() {
                progressDialog.show();
            }

            @Override
            public void completeSignUp(boolean isSuccessful, String errorMessage) {
                progressDialog.dismiss();
                if (isSuccessful) openMainActivity();
                else {
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStepChanged(int step, boolean isNext) {
                openStep(step, isNext);
            }

            @Override
            public void closeSignUp() {
                openLogInActivity();
            }
        });
    }

    /**
     * Переход на loginActivity
     * Метод вызывается при нажатии кнопки назад на первом шаге
     */
    private void openLogInActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    /**
     * Переход на предыдущий / следующий шаг
     * Вызывается при нажатии кнопки назад / вперед
     */
    private void openStep(int step, boolean isNext) {
        FragmentManager manager = getSupportFragmentManager();
        if (isNext) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_left,
                    R.anim.slide_in_right, R.anim.slide_out_right);
            SignUpFragment fragment = SignUpFragment.newInstance(step);
            fragment.setInputListener(new SignUpFragment.InputListener() {
                @Override
                public void onChanged(String input) { signUpViewModel.updateCanGoNext(input); }
            });
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        else manager.popBackStack();
    }

    /**
     * Переход на mainActivity
     * Метод вызывается при усппешной регистрации
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        signUpViewModel.goBack();
    }
}
