# Дипломная работа “Облачное хранилище”

## Описание

Данное приложение представляет собой REST-сервис, предоставляющий интерфейс для возможности загрузки файлов и вывода списка уже загруженных файлов пользователя. Сервис интегрирован с веб-приложением ["netology-diplom-frontend"](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend) и может быть запущен как отдельно, так и совместоно с ним.


## Запуск

Для запуска приложения необходимо установленное приложение [docker](https://docs.docker.com/engine/install/) и UNIX-подобное окружение ([ОС Linux](https://ru.wikipedia.org/wiki/Linux), [macOS](https://ru.wikipedia.org/wiki/MacOS), [Windows Subsystem for Linux](https://ru.wikipedia.org/wiki/Windows_Subsystem_for_Linux), окружение [Cygwin](https://ru.wikipedia.org/wiki/Cygwin), [MinWG](https://ru.wikipedia.org/wiki/MinGW) и т.п.) с установленной программой [make](https://ru.wikipedia.org/wiki/Make).


* Запуск сервиса отдельно от приложения ["netology-diplom-frontend"](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend) производится выполнением команды

    ```sh
    make run-backend
    ```

    При этом сервис принимает запросы на адрес [http://localhost:8080](http://localhost:8080).

* Запуск сервиса совместно с приложением ["netology-diplom-frontend"](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend) производится выполнением команды

    ```sh
    make run
    ```

    При этом сервис также принимает запросы на адрес [http://localhost:8080](http://localhost:8080), а приложение становится доступно по ссылке [http://localhost/](http://localhost)

## Запуск тестов

Для запуска тестов кроме выше перечисленного необходимо настроенное окружение [JRE](https://ru.wikipedia.org/wiki/Java_Runtime_Environment).

Запуск тестов производится командой

```sh
make test
```