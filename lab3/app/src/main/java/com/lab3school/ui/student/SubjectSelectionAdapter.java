package com.lab3school.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lab3school.R;
import com.lab3school.data.entity.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubjectSelectionAdapter extends ListAdapter<Subject, SubjectSelectionAdapter.SubjectViewHolder> {

    private final Set<Integer> selectedSubjectIds = new HashSet<>();
    public SubjectSelectionAdapter() {
        super(SUBJECT_DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subject_selectable, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject currentSubject = getItem(position);
        holder.bind(currentSubject, selectedSubjectIds.contains(currentSubject.getId()));

        holder.checkBoxSubjectSelected.setOnCheckedChangeListener(null);
        holder.checkBoxSubjectSelected.setChecked(selectedSubjectIds.contains(currentSubject.getId()));

        holder.checkBoxSubjectSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSubjectIds.add(currentSubject.getId());
            } else {
                selectedSubjectIds.remove(currentSubject.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            holder.checkBoxSubjectSelected.toggle();
        });
    }

    public void setSelectedSubjectIds(List<Integer> initiallySelectedIds) {
        selectedSubjectIds.clear();
        if (initiallySelectedIds != null) {
            selectedSubjectIds.addAll(initiallySelectedIds);
        }
        notifyDataSetChanged();
    }

    public List<Subject> getSelectedSubjects() {
        List<Subject> selectedSubjects = new ArrayList<>();
        for (Subject subject : getCurrentList()) {
            if (selectedSubjectIds.contains(subject.getId())) {
                selectedSubjects.add(subject);
            }
        }
        return selectedSubjects;
    }

    public Set<Integer> getSelectedSubjectIdsSet() {
        return new HashSet<>(selectedSubjectIds);
    }


    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewSubjectName;
        private final CheckBox checkBoxSubjectSelected;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubjectName = itemView.findViewById(R.id.textViewSubjectNameSelectable);
            checkBoxSubjectSelected = itemView.findViewById(R.id.checkBoxSubjectSelected);
        }

        public void bind(Subject subject, boolean isSelected) {
            textViewSubjectName.setText(subject.getName());
            checkBoxSubjectSelected.setChecked(isSelected);
        }
    }

    private static final DiffUtil.ItemCallback<Subject> SUBJECT_DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Subject>() {
                @Override
                public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
