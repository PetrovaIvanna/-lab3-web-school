package com.lab3school.ui.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lab3school.R;
import com.lab3school.data.entity.School;
import com.lab3school.data.entity.Student;
import com.lab3school.data.entity.Subject;
import com.lab3school.data.relation.StudentWithDetails;
import com.lab3school.viewmodel.AddEditStudentViewModel;

import android.widget.ImageView;
import android.widget.LinearLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AddEditStudentActivity extends AppCompatActivity {

    private static final String TAG = "AddEditStudentActivity";
    public static final String EXTRA_STUDENT_ID = "com.lab3school.ui.student.EXTRA_STUDENT_ID";

    private AddEditStudentViewModel addEditStudentViewModel;

    private TextInputLayout layoutFirstName, layoutLastName, layoutDateOfBirth, layoutSchool, layoutClassYear, layoutClassLetter;
    private TextInputEditText editTextFirstName, editTextLastName, editTextDateOfBirth, editTextClassYear, editTextClassLetter;
    private AutoCompleteTextView autoCompleteSchool;
    private RecyclerView recyclerViewSubjectsSelection;
    private Button buttonSaveStudent;

    private LinearLayout subjectSectionHeader;
    private ImageView imageViewToggleSubjects;

    private SubjectSelectionAdapter subjectSelectionAdapter;
    private ArrayAdapter<School> schoolArrayAdapter;
    private List<School> schoolList = new ArrayList<>();
    private School selectedSchool = null;
    private Integer currentStudentId = null;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_student);

        addEditStudentViewModel = new ViewModelProvider(this).get(AddEditStudentViewModel.class);

        initViews();
        setupAdapters();
        setupListeners();
        setupObservers();

        if (getIntent().hasExtra(EXTRA_STUDENT_ID)) {
            currentStudentId = getIntent().getIntExtra(EXTRA_STUDENT_ID, -1);
            if (currentStudentId != -1) {
                setTitle(getString(R.string.edit_student));
                addEditStudentViewModel.triggerLoadStudentData(currentStudentId);
                observeStudentDataForEdit();
            } else {
                currentStudentId = null;
                setTitle(getString(R.string.add_student));
                addEditStudentViewModel.setCurrentlyEditingStudent(null);
            }
        } else {
            setTitle(getString(R.string.add_student));
            addEditStudentViewModel.setCurrentlyEditingStudent(null);
        }

        observeSchoolsAndSubjects();
    }

    private void initViews() {
        layoutFirstName = findViewById(R.id.layoutFirstName);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        layoutLastName = findViewById(R.id.layoutLastName);
        editTextLastName = findViewById(R.id.editTextLastName);
        layoutDateOfBirth = findViewById(R.id.layoutDateOfBirth);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        layoutSchool = findViewById(R.id.layoutSchool);
        autoCompleteSchool = findViewById(R.id.autoCompleteSchool);
        layoutClassYear = findViewById(R.id.layoutClassYear);
        editTextClassYear = findViewById(R.id.editTextClassYear);
        layoutClassLetter = findViewById(R.id.layoutClassLetter);
        editTextClassLetter = findViewById(R.id.editTextClassLetter);
        recyclerViewSubjectsSelection = findViewById(R.id.recyclerViewSubjectsSelection);
        buttonSaveStudent = findViewById(R.id.buttonSaveStudent);
        subjectSectionHeader = findViewById(R.id.subjectSectionHeader);
        imageViewToggleSubjects = findViewById(R.id.imageViewToggleSubjects);
    }

    private void setupAdapters() {
        schoolArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, schoolList);
        autoCompleteSchool.setAdapter(schoolArrayAdapter);
        autoCompleteSchool.setOnItemClickListener((parent, view, position, id) -> {
            selectedSchool = (School) parent.getItemAtPosition(position);
        });

        subjectSelectionAdapter = new SubjectSelectionAdapter();
        recyclerViewSubjectsSelection.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubjectsSelection.setAdapter(subjectSelectionAdapter);
    }

    private void setupListeners() {
        editTextDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
        buttonSaveStudent.setOnClickListener(v -> saveStudentData());
        if (subjectSectionHeader != null) {
            subjectSectionHeader.setOnClickListener(v -> toggleSubjectsSection());
        } else {
            Log.e(TAG, "subjectSectionHeader is null! Check XML ID.");
        }
    }

    private void toggleSubjectsSection() {
        if (recyclerViewSubjectsSelection.getVisibility() == View.GONE) {
            recyclerViewSubjectsSelection.setVisibility(View.VISIBLE);
            imageViewToggleSubjects.setImageResource(R.drawable.ic_keyboard_arrow_up);
        } else {
            recyclerViewSubjectsSelection.setVisibility(View.GONE);
            imageViewToggleSubjects.setImageResource(R.drawable.ic_keyboard_arrow_down);
        }
    }

    private void setupObservers() {
        addEditStudentViewModel.getSaveResult().observe(this, success -> {
            if (success == null) return;
            if (success) {
                Toast.makeText(this, getString(R.string.data_saved_successfully), Toast.LENGTH_SHORT).show();
                finish();
            }
            addEditStudentViewModel.clearSaveResult();
        });

        addEditStudentViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                addEditStudentViewModel.clearErrorMessage();
            }
        });
    }

    private void observeSchoolsAndSubjects() {
        addEditStudentViewModel.getAllSchools().observe(this, schools -> {
            if (schools != null) {
                schoolList.clear();
                schoolList.addAll(schools);
                schoolArrayAdapter.notifyDataSetChanged();
                checkAndSetSchoolForEditing();
            }
        });

        addEditStudentViewModel.getAllSubjects().observe(this, subjects -> {
            if (subjects != null) {
                subjectSelectionAdapter.submitList(subjects);
            }
        });
    }

    private void observeStudentDataForEdit() {
        if (currentStudentId == null) return;

        addEditStudentViewModel.getStudentDetailsSubscription(currentStudentId).observe(this, studentDetails -> {
            if (studentDetails != null && studentDetails.student != null) {
                Log.d(TAG, "Editing student details loaded: " + studentDetails.student.getFirstName());
                addEditStudentViewModel.setCurrentlyEditingStudent(studentDetails);
                populateFields(studentDetails);
                checkAndSetSchoolForEditing();
            }
        });

        addEditStudentViewModel.getStudentRegistrationsSubscription(currentStudentId).observe(this, registrationDetailsList -> {
            if (registrationDetailsList != null) {
                Log.d(TAG, "Editing student registrations loaded: " + registrationDetailsList.size());
                List<Integer> subjectIds = registrationDetailsList.stream()
                        .filter(rd -> rd.registration != null)
                        .map(rd -> rd.registration.getSubjectId())
                        .collect(Collectors.toList());
                subjectSelectionAdapter.setSelectedSubjectIds(subjectIds);
            }
        });
    }


    private void checkAndSetSchoolForEditing() {
        StudentWithDetails currentStudentData = addEditStudentViewModel.getStudentToEdit().getValue();
        if (currentStudentId != null && currentStudentData != null &&
                currentStudentData.student != null && !schoolList.isEmpty()) {

            Student student = currentStudentData.student;
            for (int i = 0; i < schoolList.size(); i++) {
                if (schoolList.get(i).getId() == student.getSchoolId()) {
                    selectedSchool = schoolList.get(i);
                    autoCompleteSchool.setText(selectedSchool.getName(), false);
                    break;
                }
            }
        }
    }


    private void populateFields(StudentWithDetails studentDetails) {
        if (studentDetails == null || studentDetails.student == null) return;
        Student student = studentDetails.student;
        editTextFirstName.setText(student.getFirstName());
        editTextLastName.setText(student.getLastName());
        if (student.getDateOfBirth() != null) {
            editTextDateOfBirth.setText(student.getDateOfBirth().format(dateFormatter));
        }
        editTextClassYear.setText(String.valueOf(student.getClassYear()));
        editTextClassLetter.setText(student.getClassLetter());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        try {
            if (!TextUtils.isEmpty(editTextDateOfBirth.getText())) {
                LocalDate existingDate = LocalDate.parse(editTextDateOfBirth.getText().toString(), dateFormatter);
                year = existingDate.getYear();
                month = existingDate.getMonthValue() - 1;
                day = existingDate.getDayOfMonth();
            }
        } catch (DateTimeParseException e) {
            Log.w(TAG, "Could not parse date from EditText: " + editTextDateOfBirth.getText().toString(), e);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    editTextDateOfBirth.setText(selectedDate.format(dateFormatter));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveStudentData() {
        String firstName = editTextFirstName.getText() != null ? editTextFirstName.getText().toString().trim() : "";
        String lastName = editTextLastName.getText() != null ? editTextLastName.getText().toString().trim() : "";
        String dateOfBirthStr = editTextDateOfBirth.getText() != null ? editTextDateOfBirth.getText().toString().trim() : "";
        String classYearStr = editTextClassYear.getText() != null ? editTextClassYear.getText().toString().trim() : "";
        String classLetter = editTextClassLetter.getText() != null ? editTextClassLetter.getText().toString().trim() : "";

        List<Subject> selectedSubjectsList = subjectSelectionAdapter.getSelectedSubjects();

        addEditStudentViewModel.saveStudent(
                currentStudentId,
                firstName,
                lastName,
                dateOfBirthStr,
                selectedSchool,
                classYearStr,
                classLetter,
                selectedSubjectsList
        );
    }

}