# Práctica 2: Competición de Inteligencia Artifical Genérica aplicada a Videojuegos (2 Jugadores)

A general IA for the GVG-AI competition

El controlador tiene que estar en una carpeta así:

```groovy
- abc
	|- Agent.java
	|- MyAdditionalFile1.java
	|- MyAdditionalFile2.java
- controllers
- core
- ontology
- tools
```

## Consideraciones del programa

* El paquete del agente debe llamarse como el usuario que se registra en la web de la competición.
* Implementación en una clase de Java que extiende de core.player.AbstractPlayer.java.
* Implementar al menos los siguientes métodos:
	+ Constructor público que recive: StateObservation, ElapsedCPUTimer.
	+ Sobre-escritura del método __act__ que recibe los parámetros: StateObservation, ElapsedCPUTimer, y devuelve una acción del tipo enumerado Types.ACTIONS.
