# Práctica 2: Competición de Inteligencia Artifical Genérica aplicada a Videojuegos (2 Jugadores)

A general IA for the GVG-AI competition


## Requisitos del programa

* El paquete del agente debe llamarse como el usuario que se registra en la web de la competición.
* Implementación en una clase de Java que extiende de core.player.AbstractPlayer.java.
* Implementar al menos los siguientes métodos:
	+ Constructor público que recive: StateObservation, ElapsedCPUTimer.
	+ Sobre-escritura del método __act__ que recibe los parámetros: StateObservation, ElapsedCPUTimer, y devuelve una acción del tipo enumerado Types.ACTIONS.

La localización y estructura del agente debe ser del siguiente estilo:

```groovy
- <Nombre_paquete>
	|- Agent.java
	|- <Fichero adicional 1>.java
	|- <Fichero adicional 2>java
	|- ...
- controllers
- core
- ontology
- tools
```

## Cambios hechos
En el sample GA cambiar la heuristica y borrar un random
## Enlaces de interés
### Colonia de hormigas
Papers interesantes:
* http://www.site.uottawa.ca/~sshar009/seal08.pdf
