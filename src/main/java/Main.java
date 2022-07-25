import api.servers.HttpTaskServer;
import api.servers.KVServer;
import managers.Managers;
import managers.tasks.HttpTaskManager;
import managers.tasks.TaskManager;
import tasks.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager manager = Managers.getHttpTaskManager(kvServer.getUrl());
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();

        // тест-комплект: 3 обычные, 3 эпика, 7 подзадач
        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        Task task3 = new Task("Сделать уроки",
                "Алгебра, геометрия, история",
                TaskStatus.IN_PROGRESS);

        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task epic2 = new Epic("Накопить денег на ноутбук",
                "Накопить 20.000 рублей", TaskStatus.NEW);
        Task epic3 = new Epic("Снять любительское кино",
                "Попробовать свои силы в режиссуре", TaskStatus.NEW);

        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 4);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 4);
        Task subtask3 = new Subtask("Найти подработку",
                "Найти летнюю подработку",
                TaskStatus.DONE, 5);
        Task subtask4 = new Subtask("Экономить на школьных обедах",
                "Откладывать каждый день по 50 рублей",
                TaskStatus.IN_PROGRESS, 5);
        Task subtask5 = new Subtask("Найти камеру",
                "Найти, у кого из знакомых есть видеокамера",
                TaskStatus.DONE, 6);
        Task subtask6 = new Subtask("Провести съёмки",
                "Найти день, чтобы всем было удобно и снять короткий метр",
                TaskStatus.DONE, 6);
        Task subtask7 = new Subtask("Выложить на ютуб",
                "Загрузить видео в сеть для обратной связи",
                TaskStatus.DONE, 12);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(epic1); // эпик тестирует статус "новая"
        manager.addTask(epic2); // эпик тестирует статус "выполняется"
        manager.addTask(epic3); // эпик тестирует статус "выполнена"
        manager.addTask(subtask1);
        manager.addTask(subtask2);
        manager.addTask(subtask3);
        manager.addTask(subtask4);
        manager.addTask(subtask5);
        manager.addTask(subtask6);
        manager.addTask(subtask7); // подзадача пытается сделать эпиком другую подзадачу
        manager.getTaskById(1);
        manager.getTaskById(5);
        manager.getTaskById(3);
        manager.getTaskById(1);

        manager = Managers.getHttpTaskManager(kvServer.getUrl());
        httpTaskServer.stop();
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
        ((HttpTaskManager) manager).loadFromKvServer();

        manager.getTaskById(4)
                .setStartTime("07:00 19.01.2021");
        manager.getTaskById(5)
                .setStartTime("07:10 19.01.2021");
        manager.getTaskById(6)
                .setStartTime("07:30 19.01.2021");
        manager.getTaskById(12)
                .setStartTime("07:15 19.01.2021");
        manager.getTaskById(11)
                .setStartTime("11:00 19.01.2021");
        manager.getTaskById(10)
                .setStartTime("10:00 19.01.2021");
        ((HttpTaskManager)manager).updateEpicStartTimeAndDuration(4);
        ((HttpTaskManager)manager).updateEpicStartTimeAndDuration(5);
        ((HttpTaskManager)manager).updateEpicStartTimeAndDuration(6);



    }
}