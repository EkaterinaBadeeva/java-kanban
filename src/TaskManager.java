import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> idTask = new HashMap<>();
    HashMap<Integer, Epic> idEpic = new HashMap<>();
    HashMap<Integer, Subtask> idSubtask = new HashMap<>();

    static int id = 1;

    private Integer generateNewId() {
        return id++;
    }

    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);
        idTask.put(newTask.getId(), newTask);
        return newTask;
    }

    public Subtask addNewSubtask(Subtask newSubtask) {
        int newId = generateNewId();
        newSubtask.setId(newId);
        idSubtask.put(newSubtask.getId(), newSubtask);

        if (idEpic.containsKey(newSubtask.getEpicId())) {
            Epic epic = idEpic.get(newSubtask.getEpicId());
            epic.addSubtaskId(newId);
        }
        return newSubtask;
    }

    public Epic addNewEpic(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);
        idEpic.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Task updateTask(Task updatedTask) {
        if (idTask.containsKey(updatedTask.getId())) {
            idTask.put(updatedTask.getId(), updatedTask);
            return updatedTask;
        } else {
            System.out.println("Задача с таким id не найдена");
            return null;
        }
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (idSubtask.containsKey(updatedSubtask.getId())) {
            idSubtask.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = idEpic.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
            return updatedSubtask;
        } else {
            System.out.println("Подзадача с таким id не найдена");
            return null;
        }
    }

    private void updateEpicStatus(Epic epic) {
        boolean isNewStatus = true;
        boolean isDoneStatus = true;

        for (int i : epic.getSubtaskIds()) {
            Subtask subtask = idSubtask.get(i);

            if (!subtask.status.equals(Status.NEW)) {
                isNewStatus = false;
            }
            if (!subtask.status.equals(Status.DONE)) {
                isDoneStatus = false;
            }

            if (!isNewStatus && !isDoneStatus) {
                break;
            }
        }

        if (isNewStatus || epic.isEmptySubtaskIds()) {
            epic.setStatus(Status.NEW);
        } else if (isDoneStatus) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Task deleteTask(Integer id) {
        Task task = idTask.get(id);
        idTask.remove(id);
        return task;
    }

    public Subtask deleteSubtask(Integer id) {
        Subtask subtask = idSubtask.get(id);
        int idEp = subtask.getEpicId();
        Epic epic = idEpic.get(idEp);
        idSubtask.remove(id);
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic);
        return subtask;
    }

    public Epic deleteEpic(Integer id) {
        Epic epic = idEpic.get(id);

        for (int i : epic.getSubtaskIds()) {
             Subtask subtask = idSubtask.get(i);
             idSubtask.remove(i);
        }
        idEpic.remove(id);
        return epic;
    }

    public String getAllOfTask () {
        if (idTask.isEmpty()) {
            System.out.println("Список задач пустой.");
        } else {
            return idTask.toString();
        }
        return null;
    }

    public String getAllOfSubtask () {
        if (idSubtask.isEmpty()) {
            System.out.println("Список подзадач пустой.");
        } else {
            return idSubtask.toString();
        }
        return null;
    }

    public String getAllOfEpic () {
        if (idEpic.isEmpty()) {
            System.out.println("Список эпиков пустой.");
        } else {
            return idEpic.toString();
        }
        return null;
    }

    public String getAllSubtaskOfEpic (Integer id) {
        Epic epic = idEpic.get(id);
        for (int i : epic.getSubtaskIds()) {
            Subtask subtask = idSubtask.get(i);
        }
        if (idSubtask.isEmpty()) {
            System.out.println("Список подзадач пустой.");
        } else {
            return idSubtask.toString();
        }
        return null;
    }

    public String deleteAllOfTask () {
        idTask.clear();
        return null;
    }

    public String deleteAllOfSubtask () {
        idSubtask.clear();
        return null;
    }

    public String deleteAllOfEpic () {
        idSubtask.clear();
        idEpic.clear();
        return null;
    }

    public Task getByIdTask(Integer id) {
        Task task = idTask.get(id);
        return task;
    }

    public Subtask getByIdSubtask(Integer id) {
        Subtask subtask = idSubtask.get(id);
        return subtask;
    }

    public Epic getByIdEpic(Integer id) {
        Epic epic = idEpic.get(id);
        return epic;
    }
}
