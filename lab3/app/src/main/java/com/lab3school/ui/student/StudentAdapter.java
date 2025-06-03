package com.lab3school.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lab3school.R;
import com.lab3school.data.relation.StudentWithDetails;
import com.lab3school.data.relation.RegistrationWithDetails;

import java.util.List;
import java.util.stream.Collectors;

public class StudentAdapter extends ListAdapter<StudentWithDetails, StudentAdapter.StudentViewHolder> {

    private OnItemInteractionListener listener;

    public StudentAdapter(@NonNull DiffUtil.ItemCallback<StudentWithDetails> diffCallback, OnItemInteractionListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_student, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentWithDetails currentStudentDetails = getItem(position);
        holder.bind(currentStudentDetails, listener);
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewStudentName;
        private final TextView textViewStudentClass;
        private final TextView textViewStudentSchool;
        private final TextView textViewStudentSubjects;
        private final ImageButton buttonMenu;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentClass = itemView.findViewById(R.id.textViewStudentClass);
            textViewStudentSchool = itemView.findViewById(R.id.textViewStudentSchool);
            textViewStudentSubjects = itemView.findViewById(R.id.textViewStudentSubjects);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);
        }

        public void bind(final StudentWithDetails studentDetails, final OnItemInteractionListener listener) {
            if (studentDetails.student != null) {
                textViewStudentName.setText(studentDetails.student.getFirstName() + " " + studentDetails.student.getLastName());
                textViewStudentClass.setText(studentDetails.student.getClassYear() + "-" + studentDetails.student.getClassLetter() + " клас");
            } else {
                textViewStudentName.setText("N/A");
                textViewStudentClass.setText("N/A");
            }

            if (studentDetails.school != null) {
                textViewStudentSchool.setText(studentDetails.school.getName());
            } else {
                textViewStudentSchool.setText("Школа: N/A");
            }

            if (studentDetails.registrationDetails != null && !studentDetails.registrationDetails.isEmpty()) {
                String subjectsString = studentDetails.registrationDetails.stream()
                        .filter(rd -> rd != null && rd.subject != null)
                        .map(rd -> rd.subject.getName())
                        .sorted()
                        .collect(Collectors.joining(", "));
                if (subjectsString.isEmpty()) {
                    textViewStudentSubjects.setText("Предмети: не призначено");
                } else {
                    textViewStudentSubjects.setText("Предмети: " + subjectsString);
                }
            } else {
                textViewStudentSubjects.setText("Предмети: не призначено");
            }


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(studentDetails);
                }
            });

            buttonMenu.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMenuClick(v, studentDetails);
                }
            });
        }
    }

    public interface OnItemInteractionListener {
        void onItemClick(StudentWithDetails studentDetails);

        void onMenuClick(View view, StudentWithDetails studentDetails); // Для кнопки "..."
    }

    public static class StudentDiff extends DiffUtil.ItemCallback<StudentWithDetails> {
        @Override
        public boolean areItemsTheSame(@NonNull StudentWithDetails oldItem, @NonNull StudentWithDetails newItem) {
            if (oldItem.student == null && newItem.student == null) return true;
            if (oldItem.student == null || newItem.student == null) return false;
            return oldItem.student.getId() == newItem.student.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull StudentWithDetails oldItem, @NonNull StudentWithDetails newItem) {

            boolean studentContentSame;
            if (oldItem.student == null && newItem.student == null) {
                studentContentSame = true;
            } else if (oldItem.student != null && newItem.student != null) {
                studentContentSame = oldItem.student.equals(newItem.student);
            } else {
                studentContentSame = false;
            }

            boolean schoolContentSame;
            if (oldItem.school == null && newItem.school == null) {
                schoolContentSame = true;
            } else if (oldItem.school != null && newItem.school != null) {
                schoolContentSame = oldItem.school.equals(newItem.school);
            } else {
                schoolContentSame = false;
            }

            boolean registrationsSame;
            List<RegistrationWithDetails> oldRegs = oldItem.registrationDetails;
            List<RegistrationWithDetails> newRegs = newItem.registrationDetails;

            if (oldRegs == null && newRegs == null) {
                registrationsSame = true;
            } else if (oldRegs != null && newRegs != null) {
                String oldSubjectsString = oldRegs.stream()
                        .filter(rd -> rd != null && rd.subject != null)
                        .map(rd -> rd.subject.getName())
                        .sorted()
                        .collect(Collectors.joining(","));
                String newSubjectsString = newRegs.stream()
                        .filter(rd -> rd != null && rd.subject != null)
                        .map(rd -> rd.subject.getName())
                        .sorted()
                        .collect(Collectors.joining(","));
                registrationsSame = oldSubjectsString.equals(newSubjectsString);

            } else {
                registrationsSame = false;
            }

            return studentContentSame && schoolContentSame && registrationsSame;
        }
    }
}
