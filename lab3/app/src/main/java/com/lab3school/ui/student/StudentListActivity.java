package com.lab3school.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lab3school.R;
import com.lab3school.data.relation.StudentWithDetails;
import com.lab3school.viewmodel.StudentListViewModel;


public class StudentListActivity extends AppCompatActivity implements StudentAdapter.OnItemInteractionListener {

    private static final String TAG = "StudentListActivity";
    public static final int ADD_STUDENT_REQUEST = 1;
    public static final int EDIT_STUDENT_REQUEST = 2;

    private StudentListViewModel studentListViewModel;
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private FloatingActionButton fabAddStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        fabAddStudent = findViewById(R.id.fabAddStudent);

        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(new StudentAdapter.StudentDiff(), this); // 'this' як слухач
        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void setupObservers() {
        studentListViewModel.getAllStudentsWithDetails().observe(this, studentsWithDetails -> {
            if (studentsWithDetails != null) {
                Log.d(TAG, "Observed " + studentsWithDetails.size() + " students.");
                studentAdapter.submitList(studentsWithDetails);
            }
        });
    }

    private void setupListeners() {
        fabAddStudent.setOnClickListener(view -> {
            Intent intent = new Intent(StudentListActivity.this, AddEditStudentActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(StudentWithDetails studentDetails) {
        Log.d(TAG, "Clicked on student: " + studentDetails.student.getFirstName());
        Intent intent = new Intent(StudentListActivity.this, AddEditStudentActivity.class);
        intent.putExtra(AddEditStudentActivity.EXTRA_STUDENT_ID, studentDetails.student.getId());
        startActivity(intent);
    }

    @Override
    public void onMenuClick(View view, StudentWithDetails studentDetails) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.student_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit_student) {
                onItemClick(studentDetails);
                return true;
            } else if (itemId == R.id.action_delete_student) {
                confirmDeleteStudent(studentDetails);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void confirmDeleteStudent(StudentWithDetails studentDetails) {
        new AlertDialog.Builder(this)
                .setTitle("Видалити студента")
                .setMessage("Ви впевнені, що хочете видалити студента " + studentDetails.student.getFirstName() + " " + studentDetails.student.getLastName() + "?")
                .setPositiveButton("Видалити", (dialog, which) -> {
                    studentListViewModel.deleteStudent(studentDetails);
                    Log.d(TAG, "Deletion confirmed for student: " + studentDetails.student.getFirstName());
                })
                .setNegativeButton("Скасувати", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
