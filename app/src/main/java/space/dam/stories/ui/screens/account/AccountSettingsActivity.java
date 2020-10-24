package space.dam.stories.ui.screens.account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;
import space.dam.stories.R;
import space.dam.stories.models.User;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.utils.Validator;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.AccountSettingsViewModel;

public class AccountSettingsActivity extends BaseActivity {

    private static int PICK_IMAGE_REQUEST = 0;
    private Uri photoUri;

    /**
     * ViewModel
     */
    private AccountSettingsViewModel viewModel;

    /**
     * Views
     */
    private CircleImageView profilePhoto;
    private TextView uploadPhoto;
    private View.OnClickListener onClickUploadPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openImageChooser();
        }
    };
    private ImageButton deletePhoto;
    private ImageButton.OnClickListener onClickDeletePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmDeletePhotoPopup();
        }
    };
    private TextInputEditText name;
    private TextWatcher nameListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            viewModel.nameChanged();
            updateSaveState(s.toString());
        }
    };

    private Button buttonCancel;
    private View.OnClickListener onClickCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };
    private Button buttonSave;
    private View.OnClickListener onClickSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (canSave) viewModel.save(name.getText().toString(), photoUri);
        }
    };
    private boolean canSave = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        setDarkSystemBar();
        initAllViews();
        initViewModel();
    }

    /**
     * Инициализация всех view
     */
    private void initAllViews() {
        uploadPhoto = findViewById(R.id.uploadPhoto);
        uploadPhoto.setOnClickListener(onClickUploadPhotoListener);

        deletePhoto = findViewById(R.id.deletePhoto);
        deletePhoto.setOnClickListener(onClickDeletePhotoListener);

        profilePhoto = findViewById(R.id.photo);
        uploadPhoto = findViewById(R.id.uploadPhoto);
        name = findViewById(R.id.name);
        name.addTextChangedListener(nameListener);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(onClickCancelListener);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(onClickSaveListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.setCancelable(false);
    }

    /**
     * Инициализация ViewModel
     */
    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(AccountSettingsViewModel.class);
        viewModel.init();
        viewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    name.setText(user.getName());
                    if (!TextUtils.isEmpty(user.getPhoto())) {
                        ViewUtils.updateProfilePhoto(user.getPhoto(), profilePhoto);
                        deletePhoto.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        viewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status == AccountSettingsViewModel.STATUS_SAVING) {
                    progressDialog.show();
                }
                else progressDialog.dismiss();

                if (status == AccountSettingsViewModel.STATUS_SAVED) {
                    onBackPressed();
                    return;
                }

                if (status == AccountSettingsViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(AccountSettingsActivity.this, R.string.error);
                }
            }
        });
    }

    /**
     * Проверяет, можно ли сохранить текущие данные
     */
    private void updateSaveState(String name) {
        int status = Validator.validateName(name);

        if (status == Validator.SUCCESS) {
            ViewUtils.Button.setAccent(getBaseContext(), buttonSave);
            canSave = true;
        }
        else {
            ViewUtils.Button.setDisabled(getBaseContext(), buttonSave);
            canSave = false;
        }
    }

    private void showConfirmDeletePhotoPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete_photo)
                .setPositiveButton(R.string.cancel, null)
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtils.updateProfilePhoto(null, profilePhoto);
                        photoUri = null;
                        deletePhoto.setVisibility(View.INVISIBLE);
                        viewModel.profilePhotoChanged();
                    }
                });
        builder.show()
                .getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this, R.color.colorDanger)
        );
    }

    /**
     * Image chooser
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data.getData() != null) {
            photoUri = data.getData();
            profilePhoto.setImageURI(photoUri);
            deletePhoto.setVisibility(View.VISIBLE);
            viewModel.profilePhotoChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
