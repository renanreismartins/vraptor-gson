Vraptor-Gson
============

Um plugin para [Vraptor](https://vraptor.org) que serializa/deserializa JSON através do framework [GSON](https://sites.google.com/site/gson/). 100% Compatível com a API do Vraptor.


Motivação:
----------
O JSON serializado através do XStream era poluído, além de não deserializar objetos serializados por ele mesmo.


Instruções de uso:
------------------
Basta adicionar o arquivo target/vraptor-gson-1.0.jar em seu classpath.

Fique a vontade para criar seus [adaptadores (converters)](https://sites.google.com/site/gson/gson-user-guide/#TOC-Custom-Serialization-and-Deserialization) do GSON, Para isso implemente a interface JsonSerializer e anote sua classe com @Component. Semelhante como é feito com os converters do Xstream.
