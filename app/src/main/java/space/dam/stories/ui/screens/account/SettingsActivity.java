package space.dam.stories.ui.screens.account;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import space.dam.stories.R;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.ui.screens.start.LogInActivity;
import space.dam.stories.viewmodels.SettingsViewModel;

public class SettingsActivity extends BaseActivity {

    /**
     * View models
     */
    private SettingsViewModel settingsViewModel;

    /**
     * Views
     */
    private Button buttonLogOut;
    private View.OnClickListener buttonLogOutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmLogOutPopup();
        }
    };
    private Button account;
    private View.OnClickListener buttonAccountOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showAccountSettings();
        }
    };

    private Button buttonBack;
    private View.OnClickListener buttonBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * Обработка нажатия кнопок в ConfirmLogOutDialog
     */
    private DialogInterface.OnClickListener onLogOutClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            settingsViewModel.onClickLogOut();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setDarkSystemBar();
        initViewModels();
        initAllViews();
    }

    /**
     * Инициализация всех views
     */
    private void initAllViews() {
        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(buttonLogOutOnClickListener);

        account = findViewById(R.id.buttonAccount);
        account.setOnClickListener(buttonAccountOnClickListener);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(buttonBackOnClickListener);
    }

    /**
     * Инициализация всех viewModel
     */
    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        settingsViewModel = provider.get(SettingsViewModel.class);
        initAccountSettingsViewModel();
    }

    /**
     * Инициализация SettingsViewModel
     */
    private void initAccountSettingsViewModel() {
        settingsViewModel.setCallback(new SettingsViewModel.Callback() {
            @Override
            public void logOut() {
                Intent intent = new Intent(getBaseContext(), LogInActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    /**
     * Открытие окна с подтверждением выхода из аккаунта
     */
    private void showConfirmLogOutPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_log_out)
                .setPositiveButton(R.string.cancel, null)
                .setNegativeButton(R.string.log_out, onLogOutClickListener);
        builder.show()
                .getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(this, R.color.colorDanger)
        );
    }

    /**
     * Открытие окна с настройками аккаунта
     */
    private void showAccountSettings() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
