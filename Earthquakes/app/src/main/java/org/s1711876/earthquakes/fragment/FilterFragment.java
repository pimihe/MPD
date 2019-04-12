// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.fragment;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.s1711876.earthquakes.MainActivity;
import org.s1711876.earthquakes.R;
import org.s1711876.earthquakes.filter.EarthquakeDateTimeFilter;
import org.s1711876.earthquakes.filter.EarthquakeDepthFilter;
import org.s1711876.earthquakes.filter.EarthquakeMagnitudeFilter;
import org.s1711876.earthquakes.filter.EarthquakeSortOrder;
import org.s1711876.earthquakes.viewmodel.FilterViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    // use butter knife to get references to all ui objects
    @BindView(R.id.frameLayout) FrameLayout frameLayout;

    @BindView(R.id.applyBtn) Button applyBtn;
    @BindView(R.id.clearBtn) Button clearBtn;
    @BindView(R.id.dfCheckBox) CheckBox dfCheckBox;
    @BindView(R.id.dfSpinner) Spinner dfSpinner;
    @BindView(R.id.dfEditText1) EditText dfEditText1;
    @BindView(R.id.dfAndTextView) TextView dfAndTextView;
    @BindView(R.id.dfEditText2) EditText dfEditText2;
    @BindView(R.id.dfRangeLayout) LinearLayout dfRangeLayout;

    @BindView(R.id.mfCheckBox) CheckBox mfCheckBox;
    @BindView(R.id.mfSpinner)Spinner mfSpinner;
    @BindView(R.id.mfEditText1) EditText mfEditText1;
    @BindView(R.id.mfAndTextView) TextView mfAndTextView;
    @BindView(R.id.mfEditText2) EditText mfEditText2;
    @BindView(R.id.mfRangeLayout) LinearLayout mfRangeLayout;

    @BindView(R.id.tfCheckBox)CheckBox tfCheckBox;
    @BindView(R.id.tfSpinner) Spinner tfSpinner;
    @BindView(R.id.tfDateEditText1) EditText tfDateEditText1;
    @BindView(R.id.dateBtn1) ImageView dateBtn1;
    @BindView(R.id.tfAndTextView) TextView tfAndTextView;
    @BindView(R.id.tfDateEditText2) EditText tfDateEditText2;
    @BindView(R.id.dateBtn2) ImageView dateBtn2;
    @BindView(R.id.tfRangeLayout) ConstraintLayout tfRangeLayout;

    @BindView(R.id.sortBySpinner) Spinner sortBySpinner;
    @BindView(R.id.sortOrderSpinner) Spinner sortOrderSpinner;
    @BindView(R.id.sortByTextEdit) EditText sortByTextEdit;

    private FilterViewModel viewModel;

    private MainActivity main;

    public FilterFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // get reference to parent activity to allow for toggling filter display from this fragment
        main = (MainActivity)getActivity();

        ButterKnife.bind(this, view);

        // click listeners here function for handling actual click at bottom of file
        applyBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        dateBtn1.setOnClickListener(this);
        dateBtn2.setOnClickListener(this);
        frameLayout.setOnClickListener(this);

        initViewModel();

        // below are spinner listeners which can change parts of the view which are visible depending on selected option within spinner
        dfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spinnerText = dfSpinner.getSelectedItem().toString();
                if(spinnerText.equals("between")) {
                    dfAndTextView.setVisibility(View.VISIBLE);
                    dfRangeLayout.setVisibility(View.VISIBLE);
                    return;
                }
                dfAndTextView.setVisibility(View.GONE);
                dfRangeLayout.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        });

        mfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spinnerText = mfSpinner.getSelectedItem().toString();
                if(spinnerText.equals("between")) {
                    mfAndTextView.setVisibility(View.VISIBLE);
                    mfRangeLayout.setVisibility(View.VISIBLE);
                    return;
                }
                mfAndTextView.setVisibility(View.GONE);
                mfRangeLayout.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        });

        tfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spinnerText = tfSpinner.getSelectedItem().toString();
                if(spinnerText.equals("between")) {
                    tfAndTextView.setVisibility(View.VISIBLE);
                    tfRangeLayout.setVisibility(View.VISIBLE);
                    return;
                }
                tfAndTextView.setVisibility(View.GONE);
                tfRangeLayout.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        });

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spinnerText = sortBySpinner.getSelectedItem().toString();
                if(spinnerText.equals("distance from")) {
                    sortByTextEdit.setVisibility(View.VISIBLE);
                    return;
                }
                sortByTextEdit.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        });

        return view;
    }

    // function to initialise the view model for this fragment
    private void initViewModel() {
        final Observer<String> filterFeedback = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String result) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        };
        viewModel = ViewModelProviders.of(this).get(FilterViewModel.class);
        viewModel.getFilterFeedbackLiveData().observe(this, filterFeedback);
    }

    // displays androids datepicker when run
    private void displayDatePicker() {
        final Calendar calendar2 = Calendar.getInstance();
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        int year2 = calendar2.get(Calendar.YEAR);

        DatePickerDialog datePicker2 = new DatePickerDialog(getActivity(), this, year2, month2, day2);
        datePicker2.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateBtn1: { // allows input for first date field
                dateTimeSubmittedInFirstRow = true;
                displayDatePicker();
                break;
            }
            case R.id.dateBtn2: { // allows input for second date field
                dateTimeSubmittedInFirstRow = false;
                displayDatePicker();
                break;
            }
            case R.id.frameLayout: { // detects clicks outside the filter display and closes the filter display
                main.toggleFilterDisplay();
                break;
            }
            case R.id.clearBtn: { // resets filter fields
                main.resetFilterFragment();
                break;
            }
            case R.id.applyBtn: { // function which checks each checkbox and if is ticked then create filter object for that section
                EarthquakeDepthFilter df = null;
                if( dfCheckBox.isChecked() && !TextUtils.isEmpty(dfEditText1.getText().toString()) )
                {
                    int dfValue1 = Integer.parseInt(dfEditText1.getText().toString());
                    int dfValue2 = 0;
                    if(!TextUtils.isEmpty(dfEditText2.getText().toString())) dfValue2 = Integer.parseInt(dfEditText2.getText().toString());

                    String spinnerText = dfSpinner.getSelectedItem().toString();
                    EarthquakeDepthFilter.DepthComparator dfComparator = EarthquakeDepthFilter.DepthComparator.EQUAL;
                    if(spinnerText.equals("greater than")) dfComparator = EarthquakeDepthFilter.DepthComparator.GREATER_THAN;
                    if(spinnerText.equals("less than")) dfComparator = EarthquakeDepthFilter.DepthComparator.LESS_THAN;
                    if(spinnerText.equals("between")) dfComparator = EarthquakeDepthFilter.DepthComparator.BETWEEN;

                    df = new EarthquakeDepthFilter();
                    df.setComparator(dfComparator);
                    df.setDepth1(dfValue1);
                    df.setDepth2(dfValue2);
                }

                EarthquakeMagnitudeFilter mf = null;
                if(mfCheckBox.isChecked() && !TextUtils.isEmpty(mfEditText1.getText().toString()) )
                {
                    double mfValue1 = Double.parseDouble(mfEditText1.getText().toString());
                    double mfValue2 = 0;
                    if(!TextUtils.isEmpty(mfEditText2.getText().toString())) mfValue2 = Double.parseDouble(mfEditText2.getText().toString());

                    String spinnerText = mfSpinner.getSelectedItem().toString();
                    EarthquakeMagnitudeFilter.MagnitudeComparator mfComparator = EarthquakeMagnitudeFilter.MagnitudeComparator.EQUAL;
                    if(spinnerText.equals("greater than")) mfComparator = EarthquakeMagnitudeFilter.MagnitudeComparator.GREATER_THAN;
                    if(spinnerText.equals("less than")) mfComparator = EarthquakeMagnitudeFilter.MagnitudeComparator.LESS_THAN;
                    if(spinnerText.equals("between")) mfComparator = EarthquakeMagnitudeFilter.MagnitudeComparator.BETWEEN;

                    mf = new EarthquakeMagnitudeFilter();
                    mf.setComparator(mfComparator);
                    mf.settMagnitude1(mfValue1);
                    mf.settMagnitude2(mfValue2);
                }

                EarthquakeDateTimeFilter tf = null;
                if(tfCheckBox.isChecked() && !TextUtils.isEmpty(tfDateEditText1.getText().toString()) )
                {
                    String value1String = tfDateEditText1.getText().toString();

                    SimpleDateFormat dateTimeParser = new SimpleDateFormat("d-MM-yyyy");
                    Date parseDateTime = null;
                    try {
                        parseDateTime = dateTimeParser.parse(value1String);
                    } catch (ParseException e) { e.printStackTrace(); }

                    long tfValue1 = parseDateTime.getTime()/1000;
                    long tfValue2 = 0;
                    if(!TextUtils.isEmpty(tfDateEditText2.getText().toString()) ) {
                        value1String = tfDateEditText2.getText().toString();
                        try {
                            parseDateTime = dateTimeParser.parse(value1String);
                        } catch (ParseException e) { e.printStackTrace(); }
                        tfValue2 = parseDateTime.getTime()/1000;
                    }

                    String spinnerText = tfSpinner.getSelectedItem().toString();
                    EarthquakeDateTimeFilter.DateTimeComparator tfComparator = EarthquakeDateTimeFilter.DateTimeComparator.EQUAL;
                    if(spinnerText.equals("after")) tfComparator = EarthquakeDateTimeFilter.DateTimeComparator.GREATER_THAN;
                    if(spinnerText.equals("before")) tfComparator = EarthquakeDateTimeFilter.DateTimeComparator.LESS_THAN;
                    if(spinnerText.equals("between")) tfComparator = EarthquakeDateTimeFilter.DateTimeComparator.BETWEEN;

                    tf = new EarthquakeDateTimeFilter();
                    tf.setComparator(tfComparator);
                    tf.settDateTime1(tfValue1);
                    tf.settDateTime2(tfValue2);
                }


                // onxce all sections have been checked get the sort order
                String sortByString = sortBySpinner.getSelectedItem().toString();
                EarthquakeSortOrder.EarthquakeSortByOption sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.DATE;
                if(sortByString.equals("depth")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.DEPTH;
                if(sortByString.equals("magnitude")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.MAGNITUDE;
                if(sortByString.equals("distance from")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.DISTANCE_FROM;
                if(sortByString.equals("distance from me")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.DISTANCE_FROM_ME;
                if(sortByString.equals("most northerly")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.MOST_NORTHERLY;
                if(sortByString.equals("most easterly")) sortByOption = EarthquakeSortOrder.EarthquakeSortByOption.MOST_EASTERLY;

                boolean sortAscending = false;
                if(sortOrderSpinner.getSelectedItem().toString().equals("ascending")) sortAscending = true;

                EarthquakeSortOrder so = new EarthquakeSortOrder(sortByTextEdit.getText().toString(), sortAscending, sortByOption);

                // apply the filter in the viewmodel which carries through to the repository affecting all views
                viewModel.applyFilter(df, mf, tf, so);
            }
        }
    }

    // var and function which controls and sets which date field recieves the input value
    private boolean dateTimeSubmittedInFirstRow;
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateString = dayOfMonth+"-"+(month+1)+"-"+year;
        if(dateTimeSubmittedInFirstRow) {
            tfDateEditText1.setText(dateString);
        } else {
            tfDateEditText2.setText(dateString);
        }
    }

}

