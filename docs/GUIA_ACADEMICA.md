# Guía académica y de sustentación

## Responsabilidades del ejercicio 12

```text
GeneradorPedidos ──crea──> Pedido (inmutable)
                              │
                              v
CoordinadorPedidos ──envía──> ProcesadorPedidos
                                ├──consulta──> ValidadorPedido
                                ├──reserva───> Inventario (interfaz)
                                │                └── InventarioSeguro
                                └──produce───> ResultadoPedido (inmutable)
                                                   │
                                                   v
                                              InformeTienda
```

La inyección por constructor permite que `ProcesadorPedidos` reciba el validador, el inventario y los identificadores con fallo sin usar frameworks. La versión secuencial y la concurrente comparten exactamente el mismo procesamiento.

## Objetos compartidos y protección

| Ejercicio | Dato compartido | Riesgo | Protección |
|---|---|---|---|
| 4 | contador entero | incrementos perdidos | `synchronized` o `AtomicInteger` |
| 5 | arreglo de asientos | doble reserva | método `synchronized` que comprueba y asigna |
| 6 | frecuencias | sobrescritura de conteos | `ConcurrentHashMap.merge()` |
| 9 | documentos | pérdida, duplicado o bloqueo | `ArrayBlockingQueue`, señales de fin y cancelación |
| 10 | saldos de dos cuentas | saldo inconsistente o deadlock | dos monitores en orden global por id |
| 11 | capacidad del servicio | más de tres accesos | `Semaphore(3)`; liberación en `finally` |
| 12 | stock por producto | vender más unidades de las disponibles | reserva completa dentro de método `synchronized` |

## Condición de carrera del contador

`contador++` equivale conceptualmente a leer, sumar y escribir. Dos hilos pueden leer cero, calcular uno y escribir uno: se solicitaron dos incrementos, pero el resultado es uno. `volatile` da visibilidad, no convierte esa secuencia en una sola operación atómica.

La demostración usa dos `CyclicBarrier` fuera de cualquier monitor: la primera obliga a que ambos hilos lean antes de escribir y la segunda confirma la escritura. Las versiones correctas protegen toda la operación con `synchronized` o usan `AtomicInteger.incrementAndGet()`.

`merge(palabra, 1, Integer::sum)` tiene una actualización atómica por clave. En cambio, dos hilos pueden obtener el mismo valor con `getOrDefault()` y luego ambos ejecutar `put()`, perdiendo una suma.

## Coordinación, interrupciones y cierre

- `join()` espera los hilos de los ejercicios iniciales antes de leer o combinar resultados.
- Los `ExecutorService` se cierran en `finally` y se espera su terminación con plazo.
- Una tarea bloqueada en `BlockingQueue.take()` o `Semaphore.acquire()` responde a interrupción.
- `Future.cancel(true)` solo solicita interrupción; otra señal confirma que el `finally` y el cuerpo terminaron.
- La cola de impresión usa `offer`/`poll` con plazo para revisar cancelación sin espera activa.
- Un permiso del semáforo solo se libera si fue adquirido.

## Conservación

- Transferencias: cada operación resta y suma dentro de los mismos dos bloqueos. El total permanece en 200000.
- Tienda: stock inicial total 60. Se confirman 30 unidades de P1, 20 de P2 y 9 de P3; quedan 1 unidad. `60 = 59 vendidas + 1 disponible`.
- Los pedidos inválidos y los errores técnicos se resuelven antes de reservar, por lo que no descuentan stock.
- Cada pedido se envía una vez y cada `Future` aporta un resultado terminal; las pruebas verifican 200 identificadores distintos.

Un deadlock bancario aparecería si una transferencia bloqueara primero A y luego B mientras otra bloqueara primero B y luego A. La solución ordena siempre por identificador único, independientemente de cuál sea origen. No se incluye una reproducción en el proceso principal porque podría congelarlo.

## Observaciones reales de ejecución

Estas observaciones se obtuvieron al ejecutar el código, no son predicciones:

- La impresión normal completó 20 documentos sin duplicados. Al fallar el 7, se identificó el fallo, se canceló el resto y se informó el conjunto pendiente.
- El servicio externo terminó con 26 éxitos, 4 errores, máximo 3 operaciones activas y 3 permisos recuperados.
- Las tiendas secuencial y concurrente sin fallos produjeron 43 confirmados, 157 rechazados y stock P1=0, P2=0, P3=1.
- Con fallos técnicos en 191, 193 y 198 se obtuvieron 43 confirmados, 154 rechazados y 3 errores, sin alterar el stock final esperado.
- `mvn clean test` ejecutó 12 pruebas: 12 correctas, 0 fallos, 0 errores y 0 omitidas.

## Preguntas breves para sustentar

1. **¿Cuál es la diferencia entre `start()` y `run()`?** `start()` solicita un hilo nuevo; llamar `run()` es una llamada normal en el hilo actual.
2. **¿Por qué esperar con `join()`?** Para no combinar ni publicar resultados antes de que terminen los trabajadores y para obtener visibilidad de sus escrituras.
3. **¿Por qué no compartir acumuladores en las particiones?** Cada trabajador produce un resultado independiente y el hilo coordinador los combina sin carreras.
4. **¿Por qué `volatile` no arregla `++`?** Porque leer, sumar y escribir siguen siendo tres pasos que pueden intercalarse.
5. **¿Qué protege la reserva?** Un único monitor cubre conjuntamente comprobar disponibilidad y marcar ocupado.
6. **¿Por qué usar `merge()`?** Actualiza atómicamente una clave del mapa concurrente.
7. **¿Por qué recuperar cada `Future` aunque otro falle?** Un error de proveedor no debe eliminar cotizaciones válidas.
8. **¿Cancelado significa terminado?** No; por eso se espera una señal independiente emitida desde `finally`.
9. **¿Por qué hay tres señales de fin?** Cada consumidor necesita retirar una para poder finalizar.
10. **¿Cómo se evita el deadlock?** Todas las transferencias adquieren cuentas en el mismo orden global.
11. **¿Por qué liberar en `finally`?** Para recuperar capacidad incluso si ocurre una excepción, pero solo si el permiso se adquirió.
12. **¿Por qué pueden cambiar los clientes ganadores?** El orden de llegada concurrente no es fijo; deben conservarse cantidades, stock y unicidad, no identidades específicas.
