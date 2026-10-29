# Эмулятор для языка оболочки ОС
Вариант №10

## 1. Общее описание.
Консольный эмулятор командной строки UNIX-подобной операционной системы, написанный на языке Java. Приложение разработано для имитации взаимодействия с оболочкой ОС.

## 2. Описание всех функций и настроек. 
Эмулятор распознает и раскрытие системных переменных среды(например, `$HOME`).\
Поддерживаемые команды:
  * `ls` - вывести содержимое директории.
  * `cd` - изменить текущую директорию.
  * `mdir` - создать новую директорию.
  * `rm` - удалить файл/директорию.
  * `find` - найти файл/директорию.
  * `echo` - вывести текст.
  * `help` - вывести информацию по всем командам.
  * `exit` - завершеть работу эмулятора.

## 3. Описание команд для сборки проекта и запуска тестов.

* Стандартные сборка и запуск: `make run`
* Сборка и запуск с минимальной VFS: `make run_min`
* Сборка и запуск со средней VFS: `make run_min`
* Сборка и запуск с большой VFS: `make run_big`
* Сборка и запуск трех тестов: `make test`
* Очистка файлов сборки: `make clean`
* Сборка: `make compile`


## 4. Примеры использования
```
[DEBUG] VFS Path: VFS/VFS_big.xml
[DEBUG] Startup Script: tests/test.txt
Executing script: tests/test.txt
VFS_big:/$ help
Available commands:
  ls                 - List directory contents
  cd <path>          - Change current directory
  mkdir <path>       - Create a new directory
  rm <path/paths>    - Remove a file or directory
  find <name>        - Search for files or directories
  echo <text>        - Print text to standard output
  help               - Display help message
  exit               - Exit the emulator
VFS_big:/$ ls
documents/
readme.txt
VFS_big:/$ cd documents
VFS_big:/$ ls
local/
VFS_big:/$ cd ..
VFS_big:/$ ls
documents/
readme.txt
VFS_big:/$ cd test
cd: no such file or directory: test
VFS_big:/$ cd documents
VFS_big:/$ cd /
VFS_big:/$ ls
documents/
readme.txt
VFS_big:/$ find readme
/readme.txt
VFS_big:/$ find bin
/documents/local/bin
/documents/local/bin/binary.bin
VFS_big:/$ cd /documents/local/
VFS_big:/$ ls
bin/
VFS_big:/$ find test
No matches found for: test
VFS_big:/$ test
[ERROR] Unknown command or invalid arguments: test
VFS_big:/$ echo $USER
codespace
VFS_big:/$ echo $HOME
/home/codespace
VFS_big:/$ cd /
VFS_big:/$ mkdir newDir
VFS_big:/$ mkdir newDir/newDir2
VFS_big:/$ cd newDir
VFS_big:/$ mkdir /documents/newDir3
VFS_big:/$ cd /
VFS_big:/$ find newDir
/documents/newDir3
/newDir
/newDir/newDir2
VFS_big:/$ rm newDir/newDir2 /documents/newDir3
VFS_big:/$ rm readme.txt
VFS_big:/$ ls
documents/
newDir/
VFS_big:/$ find newDir
/newDir
VFS_big:/$ rm test
rm: no such file or directory test
VFS_big:/$ mkdir /docs/testdir
mkdir: cannot create directory /docs/testdir
VFS_big:/$ exit
```