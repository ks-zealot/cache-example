# Цель
Приложение, представляет собой двухуровневый кэш, готовый для встравивания в приложение как внешняя библиотека.

# Структура приложения

Кэш не несет зависимостей, кроме библиотеки slf4j дял логирования
представляет собой простой потокобезопасный двухуровневый кэш - объекты кладутся в inmemory уровень.если он
 переполняется, то кладутся в кэш, являющийся надстройкой над  файловой системой
 #Настройки
 Кэш использует файл cache.xml
 ```
 <?xml version="1.0" encoding="ISO-8859-1" ?>
     <caches>
         <cache>
             <name>cache1</name>
             <maxSizeFirstLevel>100</maxSizeFirstLevel>
             <maxSizeSecondLevel>10000</maxSizeSecondLevel>
             <ttl>100</ttl>
             <filesystemPath>tmp</filesystemPath>
             <delay>10</delay>
         </cache>
     </caches>
    ```
     где
     * name - имя кэша. Обязательное значени
     * maxSizeFirstLevel - размер inmemory кэша, по умолчанию 10
     * maxSizeSecondLevel - размер кэша в файловой системе, по умочланию 100
     * ttl - время жизни объектов в миллисекундах, по умолчанию 10
     * filesystePath - путь, куда будут сохранять объекты для второго кэша. По умолчанию tmp
     * delay - таймаут в секундах, с которым кэш будет проверяться на наличие устравеших элеметов. Должен быть
      меньше ttl. По умолчанию 10.
 Пример представлен в проекте
 Кэш ищет файл в домашней директории пользователя, который запусает ява машина, если там не находит, то ищет
 в директории, из которой запускается ява машина. Если и там не находит,то загружается дефолтный конфиг из src/main/resources
 # Зависимости
  * log4j - логирование
  * slf4j - обертка движка логирования

  #Использование
 * Cache.getInstance().getCache()
 * Cache.getInstance.getCache("cache1")
 * new CacheBuilder().setDelay(10)
  .setPath("tmp").setTtl(100).setMaxSecondLevelSize(10)
  .setMaxFirstLevelSize(100).build();
