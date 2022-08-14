# Task Tracker
Как системы контроля версий помогают команде работать с общим кодом, так и трекеры задач позволяют эффективно организовать совместную работу над задачами. Перед вами - бэкенд для такого трекера, отвечающий за формирование модели данных для страницы, подобной этой:

![](https://pictures.s3.yandex.net:443/resources/Untitled_25_1639469823.png)

## Типы задач

Простейшим кирпичиком такой системы является задача (англ. task). У задачи есть следующие свойства:

* Название
* Описание
* Уникальный id задачи
* Статус, отображающий прогресс задачи. Мы будем выделять следующие статусы:
1. NEW — задача только создана, но к её выполнению ещё не приступили.
2. IN_PROGRESS — над задачей ведётся работа.
3. DONE — задача выполнена.
* Длительность (в минутах)
* Дата начала
* Дата завершения

Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask). Большую задачу, которая делится на подзадачи, мы будем называть эпиком (англ. epic).
Таким образом, в нашей системе задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. Для них должны выполняться следующие условия:

* Для каждой подзадачи известно, в рамках какого эпика она выполняется.
* Каждый эпик знает, какие подзадачи в него входят.
* Завершение всех подзадач эпика считается завершением эпика.

## Менеджер

Менеджер запускается на старте программы и управлять всеми задачами. В нём реализованы следующие функции:

1. Возможность хранить задачи всех типов.
2. Методы:
    1. Получение списка всех задач.
    2. Получение списка всех эпиков.
    3. Получение списка всех подзадач определённого эпика.
    4. Получение задачи любого типа id.
    5. Добавление новой задачи, эпика и подзадачи. Сам объект должен передаваться в качестве параметра.
    6. Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    7. Удаление ранее добавленных задач — всех и по идентификатору.
    8. История просмотров задач
3. Управление статусами осуществляется по следующему правилу:
    1. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче.
    2. Для эпиков:
        * если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        * если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        * во всех остальных случаях статус должен быть IN_PROGRESS.
