package space.dam.stories.ui.screens.createStory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import space.dam.stories.R;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.utils.StoryUtils;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.CreateStoryViewModel;

public class CreateStoryActivity extends BaseActivity implements View.OnClickListener {

    /**
     * View model
     */
    private CreateStoryViewModel viewModel;

    /**
     * Views
     */
    private View[] steps;
    private int currentStep;
    private final int numberOfSteps = 3;
    private Button buttonNext;
    private View.OnClickListener buttonNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentStep <= viewModel.getLastValidStep().getValue()) goNext();
        }
    };
    private Button buttonBack;
    private ProgressDialog progressDialog;

    /**
     * Inputs
     */
    private TextInputEditText inputName;
    private EditText inputDescription;
    private TextWatcher inputListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            viewModel.updateLastValidStep(
                    inputName.getText().toString(),
                    inputDescription.getText().toString()
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        setDarkSystemBar();
        initViewModel();
        initSteps();
        initAllViews();
    }

    /**
     * Инициализация viewModel
     */
    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(CreateStoryViewModel.class); viewModel.init();
        viewModel.getLastValidStep().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer lastValidStep) {
                if (currentStep > lastValidStep) {
                    ViewUtils.Button.setDisabled(CreateStoryActivity.this, buttonNext);
                }
                else ViewUtils.Button.setAccent(CreateStoryActivity.this, buttonNext);
            }
        });
        viewModel.setCallback(new CreateStoryViewModel.Callback() {
            @Override
            public void onComplete(boolean storyCreated) {
                progressDialog.dismiss();
                if (storyCreated) {
                    ViewUtils.showToast(getBaseContext(), R.string.story_added_successfully);
                    setResult(RESULT_OK);
                    finish();
                }
                else ViewUtils.showToast(getBaseContext(), R.string.error);
            }
        });
    }

    /**
     * Инициализация шагов
     */
    private void initSteps() {
        steps = new View[3];
        steps[0] = findViewById(R.id.step1);
        steps[1] = findViewById(R.id.step2);
        steps[2] = findViewById(R.id.step3);
        currentStep = 0;
    }

    /**
     * Инициализация всех views
     */
    private void initAllViews() {
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(buttonNextListener);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        inputName = findViewById(R.id.inputName);
        inputName.addTextChangedListener(inputListener);
        inputDescription = findViewById(R.id.inputDescription);
        inputDescription.addTextChangedListener(inputListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    /**
     * Открыть следующий шаг
     */
    private void goNext() {
        if (currentStep == numberOfSteps - 1) {
            progressDialog.show();
            viewModel.createStory(
                    inputName.getText().toString(),
                    inputDescription.getText().toString()
            );
            return;
        }

        View current = steps[currentStep++];
        View next = steps[currentStep];

        Animation animIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation animOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        current.startAnimation(animOut);
        next.startAnimation(animIn);

        current.setVisibility(View.INVISIBLE);
        next.setVisibility(View.VISIBLE);

        onStepChanged();
    }

    /**
     * Открыть предыдущий шаг
     */
    private void goBack() {
        if (currentStep == 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        View current = steps[currentStep--];
        View previous = steps[currentStep];

        Animation animIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation animOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        current.startAnimation(animOut);
        previous.startAnimation(animIn);

        current.setVisibility(View.INVISIBLE);
        previous.setVisibility(View.VISIBLE);

        onStepChanged();
    }

    /**
     * Функция, вызываемая в goNext и goBack
     */
    private void onStepChanged() {
        if (currentStep == 0) buttonNext.setVisibility(View.INVISIBLE);
        else buttonNext.setVisibility(View.VISIBLE);

        if (currentStep == numberOfSteps - 1) {
            buttonNext.setText(R.string.complete);
        }
        else {
            buttonNext.setText(getResources().getString(R.string.next));
        }

        if (currentStep > viewModel.getLastValidStep().getValue()) {
            ViewUtils.Button.setDisabled(CreateStoryActivity.this, buttonNext);
        }
        else ViewUtils.Button.setAccent(CreateStoryActivity.this, buttonNext);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    /**
     * Обработка нажатий для выбора типа истории
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.historicalType:
                viewModel.setStoryType(StoryUtils.STORY_TYPE_HISTORICAL);
                goNext();
                break;
            case R.id.funnyType:
                viewModel.setStoryType(StoryUtils.STORY_TYPE_FUNNY);
                goNext();
                break;
            case R.id.scaryType:
                viewModel.setStoryType(StoryUtils.STORY_TYPE_SCARY);
                goNext();
                break;
            case R.id.lifeType:
                viewModel.setStoryType(StoryUtils.STORY_TYPE_LIFE);
                goNext();
                break;
            case R.id.taleType:
                viewModel.setStoryType(StoryUtils.STORY_TYPE_TALE);
                goNext();
                break;
            default:
                break;
        }
    }
}
