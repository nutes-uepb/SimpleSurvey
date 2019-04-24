package br.edu.uepb.nutes.simplesurvey.question;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.edu.uepb.nutes.simplesurvey.R;
import br.edu.uepb.nutes.simplesurvey.base.BaseConfigQuestion;
import br.edu.uepb.nutes.simplesurvey.base.BaseQuestion;
import br.edu.uepb.nutes.simplesurvey.base.OnQuestionListener;

import static br.edu.uepb.nutes.simplesurvey.question.Single.ARG_CONFIGS_PAGE;

public class Time extends BaseQuestion<Time.Config> implements ISlideBackgroundColorHolder, View.OnClickListener {

    private EditText editTime;
    private int mHour, mMinute;
    private Config configPage;
    private OnTimeBoxListener mListener;

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

                            setAnswer(timeFormat(hourOfDay, minute));
                        }
                    }, mHour, mMinute, configPage.enable24Hours);
            timePickerDialog.show();
        }
    }


    @Override
    public void initView(View v) {
        // Initialize components
        this.editTime = v.findViewById(R.id.answer_text_box);

        this.editTime.setOnClickListener(this);

        if (editTime != null) {
            editTime.setImeOptions(EditorInfo.IME_ACTION_DONE);

            if (configPage.hintStr != null && !configPage.hintStr.isEmpty()) {
                editTime.setHint(configPage.hintStr);
            } else if (configPage.hint != 0) {
                editTime.setHint(configPage.hint);
            }

            if (configPage.inputType != 0)
                editTime.setInputType(configPage.inputType);

            if (configPage.answerInit != null && !configPage.answerInit.isEmpty())
                editTime.setText(configPage.answerInit);

            if (configPage.background != 0)
                editTime.setBackgroundResource(configPage.background);

            if (configPage.colorBackgroundTint != 0) {
                ViewCompat.setBackgroundTintList(editTime,
                        ColorStateList.valueOf(configPage.colorBackgroundTint));
            }

            if (configPage.colorText != 0) {
                editTime.setTextColor(configPage.colorText);
                editTime.setHintTextColor(configPage.colorText);
            }
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
                        mListener.onAnswerTime(configPage.getPageNumber(),
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
    public Time.Config getConfigsQuestion() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return editTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Time.OnTimeBoxListener) {
            mListener = (Time.OnTimeBoxListener) context;
            super.setListener(mListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return configPage.getColorBackground();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (getView() != null) getView().setBackgroundColor(configPage.getColorBackground());
    }

    @Override
    public void clearAnswer() {
        editTime.setText("");
        editTime.setHint(configPage.hint);
        // Block page
        super.blockQuestion();
    }

    /**
     * Set Answer.
     *
     * @param value {@link String}
     */
    private void setAnswer(String value) {
        super.unlockQuestion();
        if (value != null && !value.isEmpty()) editTime.setText(value);
    }

    /**
     * sets the time format.
     */
    private String timeFormat(int hour, int minutes) {
        String format_hour = configPage.formatHour;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(0, 0, 0, hour, minutes, 0);

        return new SimpleDateFormat(format_hour, Locale.getDefault()).format(calendar.getTime());
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
        private boolean enable24Hours;
        private String formatHour;

        public Config() {
            super.layout(R.layout.question_time_layout);
            this.background = 0;
            this.colorBackgroundTint = 0;
            this.colorBackgroundTint = 0;
            this.hint = R.string.select_time;
            this.answerInit = null;
            this.inputType = InputType.TYPE_CLASS_TEXT;
            this.enable24Hours = false;
            this.formatHour = "HH:mm:ss";

        }

        protected Config(Parcel in) {
            background = in.readInt();
            colorText = in.readInt();
            colorBackgroundTint = in.readInt();
            hint = in.readInt();
            answerInit = in.readString();
            inputType = in.readInt();
            hintStr = in.readString();
            enable24Hours = Boolean.parseBoolean(in.readString());
            formatHour = in.readString();
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
            dest.writeString(String.valueOf(enable24Hours));
            dest.writeString(formatHour);
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
         * Enable 24 hours format.
         *
         * @return Config
         */
        public Config enable24Hours() {
            this.enable24Hours = true;
            return this;
        }

        /**
         * Enable 24 hours format.
         *
         * @return Config
         */
        public Config setTimeFormat(String format) {
            this.formatHour = format;
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
     * Interface OnTimeBoxListener.
     */
    public interface OnTimeBoxListener extends OnQuestionListener {
        void onAnswerTime(int page, String value);
    }
}
