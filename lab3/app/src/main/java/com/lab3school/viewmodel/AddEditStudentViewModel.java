package com.lab3school.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lab3school.data.entity.School;
import com.lab3school.data.entity.Student;
import com.lab3school.data.entity.Subject;
import com.lab3school.data.relation.*;
import com.lab3school.data.repository.SchoolRepository;
import com.lab3school.data.repository.StudentRepository;
import com.lab3school.data.repository.SubjectRepository;

import java.time.LocalDate;
import java.util.List;

import android.util.Log;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddEditStudentViewModel extends AndroidViewModel {
    private static final String TAG = "AddEditStudentVM";

    private StudentRepository studentRepository;
    private SchoolRepository schoolRepository;
    private SubjectRepository subjectRepository;

    private LiveData<List<School>> allSchools;
    private LiveData<List<Subject>> allSubjects;

    private MutableLiveData<StudentWithDetails> studentToEdit = new MutableLiveData<>();

    private LiveData<StudentWithDetails> studentDetailsFromRepo;
    private LiveData<List<RegistrationWithDetails>> studentRegistrationsFromRepo;

    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;


    public AddEditStudentViewModel(@NonNull Application application) {
        super(application);
        studentRepository = new StudentRepository(application);
        schoolRepository = new SchoolRepository(application);
        subjectRepository = new SubjectRepository(application);

        allSchools = schoolRepository.getAllSchools();
        allSubjects = subjectRepository.getAllSubjects();
    }

    public LiveData<List<School>> getAllSchools() {
        return allSchools;
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
    }

    public LiveData<StudentWithDetails> getStudentToEdit() {
        return studentToEdit;
    }

    public LiveData<StudentWithDetails> getStudentDetailsSubscription(int studentId) {
        if (studentDetailsFromRepo == null || (studentDetailsFromRepo.getValue() != null &&
                studentDetailsFromRepo.getValue().student != null &&
                studentDetailsFromRepo.getValue().student.getId() != studentId)) {
            studentDetailsFromRepo = studentRepository.getStudentWithDetailsById(studentId);
        }
        return studentDetailsFromRepo;
    }

    public LiveData<List<RegistrationWithDetails>> getStudentRegistrationsSubscription(int studentId) {
        if (studentRegistrationsFromRepo == null) {
            studentRegistrationsFromRepo = studentRepository.getRegistrationsForStudent(studentId);
        }
        return studentRegistrationsFromRepo;
    }

    public void setCurrentlyEditingStudent(StudentWithDetails details) {
        studentToEdit.setValue(details);
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void triggerLoadStudentData(int studentId) {
        Log.d(TAG, "Triggering load for student ID: " + studentId);
        getStudentDetailsSubscription(studentId);
        getStudentRegistrationsSubscription(studentId);
    }


    public void saveStudent(Integer studentId, String firstName, String lastName, String dateOfBirthStr,
                            School selectedSchool, String classYearStr, String classLetter,
                            List<Subject> selectedSubjects) {


        if (!validateStudentData(firstName, lastName, dateOfBirthStr, selectedSchool, classYearStr, classLetter)) {
            saveResult.setValue(false);
            return;
        }
        String processedFirstName = capitalizeWords(firstName);
        String processedLastName = capitalizeWords(lastName);
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr.trim(), dateFormatter);
        int classYear = Integer.parseInt(classYearStr.trim());

        Student student = new Student();
        student.setFirstName(processedFirstName);
        student.setLastName(processedLastName);
        student.setDateOfBirth(dateOfBirth);
        student.setSchoolId(selectedSchool.getId());
        student.setClassYear(classYear);
        student.setClassLetter(classLetter.trim().toUpperCase());

        if (studentId == null) {
            Log.d(TAG, "Inserting new student: " + student.getFirstName());
            studentRepository.insertStudentWithRegistrations(student, selectedSubjects);
            saveResult.setValue(true);
        } else {
            student.setId(studentId);
            Log.d(TAG, "Updating student ID: " + studentId);
            studentRepository.updateStudentWithRegistrations(student, selectedSubjects);
            saveResult.setValue(true);
        }
    }

    private boolean validateStudentData(String firstName, String lastName, String dateOfBirthStr,
                                        School selectedSchool, String classYearStr, String classLetter) {

        if (!isValidNamePart(firstName, "Ім'я")) {
            return false;
        }
        if (!isValidNamePart(lastName, "Прізвище")) {
            return false;
        }

        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            errorMessage.setValue("Дата народження не може бути порожньою");
            return false;
        }
        try {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr.trim(), dateFormatter);
            if (dateOfBirth.isAfter(LocalDate.now())) {
                errorMessage.setValue("Дата народження не може бути у майбутньому");
                return false;
            }
        } catch (DateTimeParseException e) {
            errorMessage.setValue("Некоректний формат дати народження (РРРР-ММ-ДД)");
            return false;
        }

        if (selectedSchool == null) {
            errorMessage.setValue("Необхідно обрати школу");
            return false;
        }

        if (classYearStr == null || classYearStr.trim().isEmpty()) {
            errorMessage.setValue("Рік класу не може бути порожнім");
            return false;
        }
        try {
            int classYear = Integer.parseInt(classYearStr.trim());
            if (classYear <= 0 || classYear > 12) {
                errorMessage.setValue("Некоректний номер класу (1-12)");
                return false;
            }
        } catch (NumberFormatException e) {
            errorMessage.setValue("Рік класу має бути числом");
            return false;
        }

        if (classLetter == null || classLetter.trim().isEmpty()) {
            errorMessage.setValue("Літера класу не може бути порожньою");
            return false;
        }

        String trimmedClassLetter = classLetter.trim();
        if (trimmedClassLetter.length() > 1) {
            errorMessage.setValue("Літера класу має складатися лише з одного символу");
            return false;
        }

        if (!Character.isLetter(trimmedClassLetter.charAt(0))) {
            errorMessage.setValue("Літера класу має бути буквою (наприклад, 'А', 'Б')");
            return false;
        }

        return true;
    }

    public void clearSaveResult() {
        saveResult.setValue(null);
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    private String capitalizePart(String part) {
        if (part == null || part.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(part.charAt(0)) + part.substring(1);
    }

    private String capitalizeWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String lowercasedText = text.trim().toLowerCase();
        String[] mainWords = lowercasedText.split("\\s+");

        for (int i = 0; i < mainWords.length; i++) {
            String mainWord = mainWords[i];
            if (mainWord.isEmpty()) {
                continue;
            }

            String[] hyphenParts = mainWord.split("-");
            for (int j = 0; j < hyphenParts.length; j++) {
                hyphenParts[j] = capitalizePart(hyphenParts[j]);
            }

            mainWords[i] = String.join("-", hyphenParts);
        }
        return String.join(" ", mainWords);
    }

    private boolean isValidNamePart(String namePart, String fieldName) {
        if (namePart == null || namePart.trim().isEmpty()) {
            errorMessage.setValue(fieldName + " не може бути порожнім");
            return false;
        }

        String trimmedName = namePart.trim();

        char firstChar = trimmedName.charAt(0);
        char lastChar = trimmedName.charAt(trimmedName.length() - 1);
        if (firstChar == '-' || firstChar == '\'' || lastChar == '-' || lastChar == '\'') {
            errorMessage.setValue(fieldName + " не може починатися або закінчуватися дефісом чи апострофом.");
            return false;
        }

        boolean hasLetter = false;
        boolean prevCharWasSpecial = false;

        for (int i = 0; i < trimmedName.length(); i++) {
            char c = trimmedName.charAt(i);

            if (Character.isLetter(c)) {
                hasLetter = true;
                prevCharWasSpecial = false;
            } else if (c == '-' || c == '\'') {
                if (prevCharWasSpecial) {
                    errorMessage.setValue(fieldName + " містить послідовні дефіси/апострофи.");
                    return false;
                }
                prevCharWasSpecial = true;
            } else if (Character.isWhitespace(c)) {

                prevCharWasSpecial = false;
            } else {
                errorMessage.setValue(fieldName + " містить недопустимі символи. Дозволені лише букви, пробіли, дефіси та апострофи.");
                return false;
            }
        }

        if (!hasLetter) {
            errorMessage.setValue(fieldName + " має містити хоча б одну букву.");
            return false;
        }

        return true;
    }
}