import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> idTask = new HashMap<>();
    private HashMap<Integer, Epic> idEpic = new HashMap<>();
    private HashMap<Integer, Subtask> idSubtask = new HashMap<>();

    private int id = 1;

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
        if (!idEpic.containsKey(newSubtask.getEpicId())) {
            System.out.println("Эпик по подзадаче не найден");
            return null;
        }

        int newId = generateNewId();
        newSubtask.setId(newId);
        Epic epic = idEpic.get(newSubtask.getEpicId());
        epic.addSubtaskId(newId);
        idSubtask.put(newSubtask.getId(), newSubtask);
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
        int updatedSubtaskId = updatedSubtask.getId();
        if (!idSubtask.containsKey(updatedSubtaskId)) {
            System.out.println("Подзадача с таким id не найдена");
            return null;
        }

        if (updatedSubtask.getEpicId() != idSubtask.get(updatedSubtaskId).getEpicId()) {
            System.out.println("Подзадача с таким id не найдена");
            return null;
        }

        idSubtask.put(updatedSubtaskId, updatedSubtask);
        Epic epic = idEpic.get(updatedSubtask.getEpicId());
        updateEpicStatus(epic);
        return updatedSubtask;
    }

    public Epic updateEpic(Epic updatedEpic) {
        int updatedEpicId = updatedEpic.getId();
        if (!idEpic.containsKey(updatedEpicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }

        Epic epic = idEpic.get(updatedEpicId);
        epic.setName(updatedEpic.getName());
        epic.setDescription(updatedEpic.getDescription());

        return updatedEpic;
    }

    private void updateEpicStatus(Epic epic) {
        boolean isNewStatus = true;
        boolean isDoneStatus = true;

        for (int i : epic.getSubtaskIds()) {
            Subtask subtask = idSubtask.get(i);

            if (!subtask.getStatus().equals(Status.NEW)) {
                isNewStatus = false;
            }
            if (!subtask.getStatus().equals(Status.DONE)) {
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
        if(subtask != null) {
            int idEp = subtask.getEpicId();
            Epic epic = idEpic.get(idEp);
            idSubtask.remove(id);
            epic.deleteSubtaskId(id);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    public Epic deleteEpic(Integer id) {
        Epic epic = idEpic.get(id);
        if(epic != null) {
            for (int i : epic.getSubtaskIds()) {
                idSubtask.remove(i);
            }
        }
        idEpic.remove(id);
        return epic;
    }

    public ArrayList<Task> getAllOfTask () {
        if (idTask.isEmpty()) {
            System.out.println("Список задач пустой.");
            return null;
        }
        return new ArrayList<Task>(idTask.values());
    }

    public ArrayList<Subtask> getAllOfSubtask () {
        if (idSubtask.isEmpty()) {
            System.out.println("Список подзадач пустой.");
            return null;
        }
        return new ArrayList<Subtask>(idSubtask.values());

    }

    public ArrayList<Epic> getAllOfEpic () {
        if (idEpic.isEmpty()) {
            System.out.println("Список эпиков пустой.");
            return null;
        }
        return new ArrayList<Epic>(idEpic.values());
    }

    public ArrayList<Subtask> getAllSubtaskOfEpic (Integer id) {
        Epic epic = idEpic.get(id);
        ArrayList<Subtask> allSubtaskOfEpic = new ArrayList<>();
        if (epic != null) {
            for (int i : epic.getSubtaskIds()) {
                Subtask subtask = idSubtask.get(i);
                allSubtaskOfEpic.add(subtask);
            }
        }
        return allSubtaskOfEpic;
    }

    public void deleteAllOfTask () {
        idTask.clear();
    }

    public void deleteAllOfSubtask () {
        idSubtask.clear();

        for (Epic epic : idEpic.values()) {
            epic.deleteAllSubtaskIds();
            updateEpicStatus(epic);
        }
    }

    public void deleteAllOfEpic () {
        idSubtask.clear();

        for (Epic epic : idEpic.values()) {
            epic.deleteAllSubtaskIds();
        }
        idEpic.clear();
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
