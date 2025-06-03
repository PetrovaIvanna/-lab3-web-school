package com.lab3school.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lab3school.data.dao.RegistrationDao;
import com.lab3school.data.dao.SchoolDao;
import com.lab3school.data.dao.StudentDao;
import com.lab3school.data.dao.StudentGradeDao;
import com.lab3school.data.dao.SubjectDao;
import com.lab3school.data.entity.Registration;
import com.lab3school.data.entity.School;
import com.lab3school.data.entity.Student;
import com.lab3school.data.entity.StudentGrade;
import com.lab3school.data.entity.Subject;
import com.lab3school.data.util.LocalDateConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {School.class, Student.class, Subject.class, Registration.class, StudentGrade.class},
        version = 1,
        exportSchema = false)
@TypeConverters({LocalDateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract SchoolDao schoolDao();

    public abstract StudentDao studentDao();

    public abstract SubjectDao subjectDao();

    public abstract RegistrationDao registrationDao();

    public abstract StudentGradeDao studentGradeDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "school_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                SubjectDao subjectDao = INSTANCE.subjectDao();
                subjectDao.insert(new Subject("Математика", "Основи алгебри, геометрії та математичного аналізу."));
                subjectDao.insert(new Subject("Фізика", "Основи механіки та термодинаміки."));
                subjectDao.insert(new Subject("Українська мова", "Вивчення граматики та синтаксису української мови."));
                subjectDao.insert(new Subject("Українська література", "Вивчення літературної спадщини України."));
                subjectDao.insert(new Subject("Історія України", "Ключові події, постаті та процеси в історії України."));
                subjectDao.insert(new Subject("Англійська мова", "Розвиток навичок читання, письма, аудіювання та говоріння англійською мовою"));
                subjectDao.insert(new Subject("Біологія", "Вивчення життєвих процесів організмів нашої планети."));
                SchoolDao schoolDao = INSTANCE.schoolDao();
                schoolDao.insert(new School("Ліцей №4", "м. Київ, вул. Центральна, 1"));
                schoolDao.insert(new School("Гімназія №1", "м. Київ, вул. Центральна, 2"));
            });
        }
    };
}
