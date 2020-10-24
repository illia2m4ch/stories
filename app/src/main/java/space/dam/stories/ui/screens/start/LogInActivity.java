package space.dam.stories.ui.screens.start;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import space.dam.stories.R;
import space.dam.stories.viewmodels.LogInViewModel;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.ui.screens.main.MainActivity;
import space.dam.stories.utils.ViewUtils;

/**
 * This activity is displayed when the user is not logged in
 */
public class LogInActivity extends BaseActivity {

    /**
     * View models
     */
    private LogInViewModel logInViewModel;
    private ProgressDialog progressDialog;

    /**
     * Поля onLogIn и password
     */
    private TextInputEditText login;
    private TextInputEditText password;
    private TextWatcher textListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            logInViewModel.updateCanLogin(
                    login.getText().toString(),
                    password.getText().toString()
            );
        }
    };

    /**
     * Кнопка onLogIn
     */
    private Button buttonLogin;
    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (logInViewModel.getCanLogin().getValue()) progressDialog.show();
            logInViewModel.login(login.getText().toString(), password.getText().toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setDarkSystemBar();
        initViews();
        initViewModels();
    }

    /**
     * Инициализация всех views
     */
    private void initViews() {
        login = findViewById(R.id.login); login.addTextChangedListener(textListener);
        password = findViewById(R.id.password); password.addTextChangedListener(textListener);
        buttonLogin = findViewById(R.id.buttonLogIn); buttonLogin.setOnClickListener(loginListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    /**
     * Инициализация всех viewModel
     */
    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        logInViewModel = provider.get(LogInViewModel.class);
        initLogInViewModel();
    }

    /**
     * Инициализация logInViewModel
     */
    private void initLogInViewModel() {
        logInViewModel.init();
        logInViewModel.getCanLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean canLogin) {
                if (canLogin) ViewUtils.Button.setDefault(LogInActivity.this, buttonLogin);
                else ViewUtils.Button.setDisabled(LogInActivity.this, buttonLogin);
            }
        });
        logInViewModel.setCallback(new LogInViewModel.Callback() {
            @Override
            public void onLogIn(boolean isLoggedIn) {
                progressDialog.dismiss();
                if (isLoggedIn) openMainActivity();
                else ViewUtils.showToast(LogInActivity.this, R.string.authorizationFailed);
            }
        });
    }

    /**
     * Переход на mainActivity
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Нажатие кнопки зарегестрироваться
     * Переход на SignUpActivity
     */
    public void onClickSignup(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
