package com.deadline.analyzer.repository;

import com.deadline.analyzer.model.Task;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.deadline.analyzer.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class TaskRepository {

    @Autowired
    private Firestore firestore;

    public Task save(Task task) throws ExecutionException, InterruptedException {
        String taskId = UUID.randomUUID().toString();
        task.setTaskId(taskId);

        firestore.collection(Constants.FirestoreCollections.TASKS)
                .document(taskId)
                .set(task)
                .get();
        return task;
    }

    public List<Task> findByUserId(String userId) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(Constants.FirestoreCollections.TASKS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get();

            List<Task> tasks = new ArrayList<>();
            querySnapshot.forEach(doc -> tasks.add(doc.toObject(Task.class)));
            return tasks;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Task findById(String taskId) {
        try {
            return firestore.collection(Constants.FirestoreCollections.TASKS)
                    .document(taskId)
                    .get()
                    .get()
                    .toObject(Task.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(Task task) throws ExecutionException, InterruptedException {
        firestore.collection(Constants.FirestoreCollections.TASKS)
                .document(task.getTaskId())
                .set(task)
                .get();
    }

    public void delete(String taskId) throws ExecutionException, InterruptedException {
        firestore.collection(Constants.FirestoreCollections.TASKS)
                .document(taskId)
                .delete()
                .get();
    }
}