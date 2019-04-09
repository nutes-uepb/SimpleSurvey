package br.edu.uepb.nutes.simplesurvey.question;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.util.Calendar;

import br.edu.uepb.nutes.simplesurvey.R;
import br.edu.uepb.nutes.simplesurvey.base.BaseConfigQuestion;
import br.edu.uepb.nutes.simplesurvey.base.BaseQuestion;
import br.edu.uepb.nutes.simplesurvey.base.OnQuestionListener;

import static br.edu.uepb.nutes.simplesurvey.question.Single.ARG_CONFIGS_PAGE;

public class Time extends BaseQuestion<Time.Config> implements ISlideBackgroundColorHolder, View.OnClickListener {

    private EditText editTime;
    private TextView txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Config configPage;
    private OnTextBoxListener mListener;

    public Time() {
    }

    /**
     * New Time instance.
     *
     * @param configPage {@link Config}
     * @return Time
     */
    private static Time builder(Config configPage) {
        Time pageFragment = new Time();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONFIGS_PAGE, configPage);

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.blockQuestion();

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = getArguments().getParcelable(ARG_CONFIGS_PAGE);
            if (configPage == null) return;
            super.setPageNumber(configPage.getPageNumber());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        if (id == R.id.answer_text_box) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            editTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


    @Override
    public void clearAnswer() {

    }

    @Override
    public void initView(View v) {
        // Initialize components
        this.editTime = v.findViewById(R.id.answer_text_box);
        this.txtTime = v.findViewById(R.id.question_description);

        this.editTime.setOnClickListener(this);

        if (this.configPage.background != 0) {
            this.editTime.setBackgroundResource(this.configPage.background);
        }

        if (configPage.colorText != 0) {
            this.editTime.setTextColor(this.configPage.colorText);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (editTime == null) return;

        editTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (String.valueOf(editTime.getText()).isEmpty()) {
                        blockQuestion();
                        return true;
                    }

                    if (mListener != null) {
                        mListener.onAnswerTextBox(configPage.getPageNumber(),
                                String.valueOf(editTime.getText()));
                    }
                    if (configPage.isNextQuestionAuto()) nextQuestion();
                    else unlockQuestion();

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getLayout() {
        return configPage.getLayout();
    }

    @Override
    public Config getConfigsQuestion() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return (configPage.getColorBackground() != 0) ? configPage.getColorBackground() : Color.GRAY;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.getColorBackground() != 0 && getView() != null) {
            getView().setBackgroundColor(configPage.getColorBackground());
        }
    }

    /**
     * Class config page.
     */
    public static class Config extends BaseConfigQuestion<Config> implements Parcelable {
        @DrawableRes
        private int background;
        @ColorInt
        int colorText, colorBackgroundTint;
        @StringRes
        private int hint;
        private String answerInit, hintStr;
        private int inputType;

        public Config() {
            super.layout(R.layout.question_time_layout);
            this.background = 0;
            this.colorBackgroundTint = 0;
            this.colorBackgroundTint = 0;
            this.hint = R.string.select_time;
            this.answerInit = null;
            this.inputType = InputType.TYPE_CLASS_TEXT;
        }

        protected Config(Parcel in) {
            background = in.readInt();
            colorText = in.readInt();
            colorBackgroundTint = in.readInt();
            hint = in.readInt();
            answerInit = in.readString();
            inputType = in.readInt();
            hintStr = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(background);
            dest.writeInt(colorText);
            dest.writeInt(colorBackgroundTint);
            dest.writeInt(hint);
            dest.writeString(answerInit);
            dest.writeInt(inputType);
            dest.writeString(hintStr);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Config> CREATOR = new Parcelable.Creator<Config>() {
            @Override
            public Config createFromParcel(Parcel in) {
                return new Config(in);
            }

            @Override
            public Config[] newArray(int size) {
                return new Config[size];
            }
        };

        /**
         * Set background style.
         *
         * @param drawable {@link DrawableRes} resource background.
         * @return Config
         */
        public Config inputBackground(@DrawableRes int drawable) {
            this.background = drawable;
            return this;
        }

        /**
         * Set color text.
         *
         * @param color {@link ColorInt} color text.
         * @return Config
         */
        public Config inputColorText(@ColorInt int color) {
            this.colorText = color;
            return this;
        }

        /**
         * Set color background tint.
         * Corresponds to the bottom horizontal line.
         *
         * @param color {@link ColorInt} resource color.
         * @return Config
         */
        public Config inputColorBackgroundTint(@ColorInt int color) {
            this.colorBackgroundTint = color;
            return this;
        }

        /**
         * Set inputHint message.
         *
         * @param message {@link StringRes} resource color.
         * @return Config
         */
        public Config inputHint(@StringRes int message) {
            this.hint = message;
            return this;
        }

        /**
         * Set hint message.
         *
         * @param message {@String}
         * @return Config
         */
        public Config inputHint(String message) {
            this.hintStr = message;
            return this;
        }

        /**
         * Set input type.
         * {@link InputType}
         *
         * @param type Input type.
         * @return Config
         */
        public Config inputType(int type) {
            this.inputType = type;
            return this;
        }

        /**
         * Set answer init.
         *
         * @param answer {@link String} answer.
         * @return Config
         */
        public Config answerInit(String answer) {
            this.answerInit = answer;
            return this;
        }

        @Override
        public Time build() {
            return Time.builder(this);
        }
    }

    /**
     * Interface OnTextBoxListener.
     */
    public interface OnTextBoxListener extends OnQuestionListener {
        void onAnswerTextBox(int page, String value);
    }
}
