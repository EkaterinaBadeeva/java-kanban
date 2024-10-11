package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    Task addNewTask(Task newTask);

    Subtask addNewSubtask(Subtask newSubtask);

    Epic addNewEpic(Epic newEpic);

    Task updateTask(Task updatedTask);

    Subtask updateSubtask(Subtask updatedSubtask);

    Epic updateEpic(Epic updatedEpic);

    Task deleteTask(Integer id);

    Subtask deleteSubtask(Integer id);

    Epic deleteEpic(Integer id);

    List<Task> getAllOfTask();

    List<Subtask> getAllOfSubtask();

    List<Epic> getAllOfEpic();

    List<Subtask> getAllSubtaskOfEpic(Integer id);

    void deleteAllOfTask();

    void deleteAllOfSubtask();

    void deleteAllOfEpic();

    Optional<Task> getByIdTask(Integer id);

    Optional<Subtask> getByIdSubtask(Integer id);

    Epic getByIdEpic(Integer id);

    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

}
