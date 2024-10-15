package manager;

import model.*;

import java.time.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> idTask = new HashMap<>();
    protected Map<Integer, Epic> idEpic = new HashMap<>();
    protected Map<Integer, Subtask> idSubtask = new HashMap<>();
    private HistoryManager history;
    protected TreeSet<Task> allTasks;

    // Хранение сетки с интервалами в 15 минут с начала текущего года
    protected Map<Integer, Boolean> taskDuration;
    private static final Integer INTERVAL = 15;

    protected int id = 0;

    public InMemoryTaskManager() {
        this.history = Managers.getDefaultHistory();
        allTasks = new TreeSet<Task>(Comparator.comparing(Task::getStartTime));
        taskDuration = new HashMap<>();
    }

    private Integer generateNewId() {
        return ++id;
    }

    @Override
    public Task addNewTask(Task newTask) {
        if (!controlIntersectionTasks(newTask)) {
            System.out.println("Пересечение интервалов задач");
            return null;
        }

        int newId = generateNewId();
        newTask.setId(newId);
        idTask.put(newTask.getId(), newTask);
        addToTreeSet(newTask);
        return newTask;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        if (!idEpic.containsKey(newSubtask.getEpicId())) {
            System.out.println("Эпик по подзадаче не найден");
            return null;
        }

        if (!controlIntersectionTasks(newSubtask)) {
            System.out.println("Пересечение интервалов подзадач");
            return null;
        }

        int newId = generateNewId();
        newSubtask.setId(newId);
        Epic epic = idEpic.get(newSubtask.getEpicId());
        epic.addSubtaskId(newId);
        idSubtask.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
        addToTreeSet(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);
        idEpic.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        if (controlIntersectionTasks(updatedTask)) {
            System.out.println("Пересечение интервалов задач");
            return null;
        }

        if (idTask.containsKey(updatedTask.getId())) {
            idTask.put(updatedTask.getId(), updatedTask);
            updateTreeSet(updatedTask);
            return updatedTask;
        } else {
            System.out.println("Задача с таким id не найдена");
            return null;
        }
    }

    @Override
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
        if (!controlIntersectionTasks(updatedSubtask)) {
            System.out.println("Пересечение интервалов задач");
            return null;
        }
        idSubtask.put(updatedSubtaskId, updatedSubtask);
        updateTreeSet(updatedSubtask);
        Epic epic = idEpic.get(updatedSubtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
        return updatedSubtask;
    }

    @Override
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

    @Override
    public Task deleteTask(Integer id) {
        Task task = idTask.get(id);
        idTask.remove(id);
        removeTaskFromTaskDuration(task);
        allTasks.remove(task);
        history.removeHistory(id);
        return task;
    }

    @Override
    public Subtask deleteSubtask(Integer id) {
        Subtask subtask = idSubtask.get(id);
        if (subtask != null) {
            int idEp = subtask.getEpicId();
            Epic epic = idEpic.get(idEp);
            idSubtask.remove(id);
            removeTaskFromTaskDuration(subtask);
            allTasks.remove(subtask);
            history.removeHistory(id);
            epic.deleteSubtaskId(id);
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
        }
        return subtask;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic = idEpic.get(id);

        if (epic != null) {
            epic.getSubtaskIds().forEach(i -> {
                removeTaskFromTaskDuration(idSubtask.get(i));
                allTasks.remove(idSubtask.get(i));
                idSubtask.remove(i);
                history.removeHistory(i);
                epic.deleteSubtaskId(i);
            });
        }

        idEpic.remove(id);
        history.removeHistory(id);
        return epic;
    }

    @Override
    public ArrayList<Task> getAllOfTask() {
        if (idTask.isEmpty()) {
            System.out.println("Список задач пустой.");
            return null;
        }
        return new ArrayList<Task>(idTask.values());
    }

    @Override
    public ArrayList<Subtask> getAllOfSubtask() {
        if (idSubtask.isEmpty()) {
            System.out.println("Список подзадач пустой.");
            return null;
        }
        return new ArrayList<Subtask>(idSubtask.values());

    }

    @Override
    public ArrayList<Epic> getAllOfEpic() {
        if (idEpic.isEmpty()) {
            System.out.println("Список эпиков пустой.");
            return null;
        }
        return new ArrayList<Epic>(idEpic.values());
    }

    @Override
    public List<Subtask> getAllSubtaskOfEpic(Integer id) {
        Epic epic = idEpic.get(id);
        return epic.getSubtaskIds().stream()
                .map(i -> {
                    return idSubtask.get(i);
                })
                .toList();
    }

    @Override
    public void deleteAllOfTask() {
        history.removeHistory(idTask.keySet());
        removeByTypeOfTaskFromTaskDuration(TypeTasks.TASK);
        removeByTypeOfTaskFromTreeSet(TypeTasks.TASK);
        idTask.clear();
    }

    @Override
    public void deleteAllOfSubtask() {
        history.removeHistory(idSubtask.keySet());
        removeByTypeOfTaskFromTaskDuration(TypeTasks.SUBTASK);
        removeByTypeOfTaskFromTreeSet(TypeTasks.SUBTASK);
        idSubtask.clear();

        idEpic.values().stream()
                .filter(Epic::isEmptySubtaskIds)
                .forEach(epic -> {
                    epic.deleteAllSubtaskIds();
                    updateEpicStatus(epic);
                    updateEpicStartTime(epic);
                    updateEpicDuration(epic);
                    updateEpicEndTime(epic);
                });
    }

    @Override
    public void deleteAllOfEpic() {
        history.removeHistory(idSubtask.keySet());
        removeByTypeOfTaskFromTaskDuration(TypeTasks.SUBTASK);
        removeByTypeOfTaskFromTreeSet(TypeTasks.SUBTASK);
        idSubtask.clear();
        idEpic.values().forEach(Epic::deleteAllSubtaskIds);
        history.removeHistory(idEpic.keySet());
        idEpic.clear();
    }

    @Override
    public Optional<Task> getByIdTask(Integer id) {
        Task task = idTask.get(id);
        history.addHistory(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Subtask> getByIdSubtask(Integer id) {
        Subtask subtask = idSubtask.get(id);
        history.addHistory(subtask);
        return Optional.ofNullable(subtask);
    }

    @Override
    public Optional<Epic> getByIdEpic(Integer id) {
        Epic epic = idEpic.get(id);
        history.addHistory(epic);
        return Optional.ofNullable(epic);
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

    private void updateEpicStartTime(Epic epic) {

        Instant epicStartTime = epic.getSubtaskIds().stream()
                .map(subtaskId -> {
                    return (idSubtask.get(subtaskId));
                })
                .filter(subtask -> !subtask.equals(null) && subtask.getStartTime() != null)
                .map(subtask -> {
                    return (subtask.getStartTime());
                })
                .min(Instant::compareTo)
                .orElse(null);

        epic.setStartTime(epicStartTime);
    }

    private void updateEpicDuration(Epic epic) {
        Duration epicDuration = epic.getSubtaskIds().stream()
                .map(subtaskId -> {
                    return (idSubtask.get(subtaskId));
                })
                .filter(subtask -> !subtask.equals(null) && subtask.getDuration() != null)
                .map(subtask -> {
                    return (subtask.getDuration());
                })
                .reduce(Duration.ZERO, (duration, duration2) -> duration = duration.plus(duration2));

        if (epicDuration == Duration.ZERO) {
            epicDuration = null;
        }

        epic.setDuration(epicDuration);
    }

    private void updateEpicEndTime(Epic epic) {
        Instant epicEndTime = epic.getSubtaskIds().stream()
                .map(subtaskId -> {
                    return (idSubtask.get(subtaskId));
                })
                .filter(subtask -> !subtask.equals(null) && subtask.getEndTime() != null)
                .map(subtask -> {
                    return (subtask.getEndTime());
                })
                .max(Instant::compareTo)
                .orElse(null);

        epic.setEndTime(epicEndTime);
    }

    public List<Task> getPrioritizedTasks() {
        return allTasks.stream().filter(task -> !task.getStartTime().equals(null)).toList();
    }

    private void addToTreeSet(Task task) {
        if (task.getStartTime() != null)
            allTasks.add(task);
    }

    private void updateTreeSet(Task updatedTask) {
        int idTask = updatedTask.getId();
        Optional<Task> oldTask = allTasks.stream()
                .filter(task -> task != null)
                .filter(task -> task.getId() == idTask)
                .findFirst();

        if (oldTask.isPresent()) {
            allTasks.remove(oldTask.get());
        }

        if (updatedTask.getStartTime() != null) {
            allTasks.add(updatedTask);
        }
    }

    private void removeByTypeOfTaskFromTreeSet(TypeTasks typeTasks) {
        allTasks.stream()
                .filter(task -> task.getTypeTasks() == typeTasks)
                .toList()
                .forEach(task -> allTasks.remove(task));
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    private boolean controlIntersectionTasks(Task task) {

        boolean isFree = true;
        if ((task.getStartTime() == null) || (task.getDuration() == null)) {
            isFree = false;
            return isFree;
        }

        Integer startCellNumber = reductionInstantTo15Min(task.getStartTime());
        Integer endCellNumber = reductionInstantTo15Min(task.getEndTime());
        // Проверка временного интервала
        for (int index = startCellNumber; index <= endCellNumber; index++) {
            if (taskDuration.containsKey(index)) {
                if (taskDuration.get(index) == true) {
                    isFree = false;
                    return isFree;
                }
            }
        }

        // Установка временного интервала (заполнение taskDuration)
        for (int index = startCellNumber; index <= endCellNumber; index++) {
            taskDuration.put(index, true);
        }

        return isFree;
    }

    private void removeByTypeOfTaskFromTaskDuration(TypeTasks typeTasks) {
        allTasks.stream()
                .filter(task -> task.getTypeTasks() == typeTasks)
                .toList()
                .forEach(task -> {
                    removeTaskFromTaskDuration(task);
                });
    }

    private void removeTaskFromTaskDuration(Task task) {

        if (task.getStartTime() == null || task.getDuration() == null) {
            return;
        }

        Integer startCellNumber = reductionInstantTo15Min(task.getStartTime());
        Integer endCellNumber = reductionInstantTo15Min(task.getEndTime());
        // Проверка временного интервала
        for (int index = startCellNumber; index <= endCellNumber; index++) {
            if (taskDuration.containsKey(index)) {
                taskDuration.remove(index);
            }
        }
    }

    private Integer reductionInstantTo15Min(Instant instant) {

        int cellNumber = 0;

        Year year = Year.now();
        ZoneId zoneId = ZoneId.of("UTC");
        Instant startOfYear = ZonedDateTime.of(year.getValue(), 1, 1, 0, 0, 0, 0, zoneId)
                .toInstant();

        Duration durationFromStartYear = Duration.between(startOfYear, instant);
        Integer durationInMin = Math.toIntExact(durationFromStartYear.toMinutes());

        if ((durationInMin % INTERVAL) == 0) {
            cellNumber = durationInMin / INTERVAL;
        } else {
            cellNumber = durationInMin / INTERVAL + 1;
        }

        return cellNumber;
    }
}
