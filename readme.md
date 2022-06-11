# Scheme Type Analyzer
Компилировать командой `mvn compile`
Запускать командой `mvn exec:java -Dexec.args="e.adl e.scm"`
Вместо аргументов `e.adl` и `e.scm` можно передать свои файлы

Поддерживает следующие функции:
* *cons*
* *car*
* *cdr
* *quote
* *if* (причём первый аргумент должен обязаельно иметь формат условия, о котором ниже)

Условия в *if* должны быть вызовами одной из двух следующих функций:
* *null?*
* *equals?*
*null?* должен содержать один аргумент, причём этот аргумент обязательно один из аргументов функции.
*equals?* содержит 2 аргумента, один из которых аргумент функции, а другой произвольное выражение, не содержащее этот аргумент.
