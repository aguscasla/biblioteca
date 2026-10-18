# Biblioteca

Sistema de gestión de biblioteca para administrar socios, catálogo y préstamos.

## Funcionalidades

- **Socios:** registrar, dar de baja, reactivar e inhabilitar socios; consultar y evaluar su estado.
- **Catálogo:** agregar, consultar y eliminar libros; buscar y filtrar títulos.
- **Préstamos:** otorgar, renovar, finalizar y consultar préstamos, validando las condiciones correspondientes.

## Entidades

### Usuario

Se registran nombre, apellido, DNI, edad, teléfono, correo electrónico, límite de préstamos y estado. Al crear un usuario, su límite es cero y su estado es `NO_REGISTRADO`. Al asociarlo a la biblioteca se establece el límite y pasa a `ACTIVO`.

Estados:

- `ACTIVO`: puede solicitar préstamos si cumple las reglas.
- `BAJA_VOLUNTARIA`: no puede solicitar préstamos; puede volver a asociarse.
- `INHABILITADO`: no puede solicitar préstamos ni volver a asociarse.
- `NO_REGISTRADO`: todavía no es socio y no puede solicitar préstamos.

### Préstamo

Registra ID, socio, ejemplar, fecha de inicio, fecha límite, fecha de devolución, cantidad de renovaciones y estado. La fecha de devolución se establece al finalizar el préstamo; inicialmente no tiene valor. El estado inicial es `EN_CURSO`.

- `EN_CURSO`: el préstamo sigue activo.
- `FINALIZADO`: el préstamo fue devuelto.

### Libro

Registra ID, título, autor, categoría, fecha de publicación, cantidad de páginas, idioma y estado. El estado inicial es `DISPONIBLE`.

- `DISPONIBLE`: puede prestarse.
- `PRESTADO`: está asociado a un préstamo en curso; vuelve a estar disponible al finalizarlo.
- `EN_REPARACION`: no puede prestarse; puede volver a estar disponible.
- `INHABILITADO`: no puede prestarse ni volver a estar disponible.

## Reglas de la biblioteca

### Implementadas

- No se otorgan préstamos a usuarios que estén de baja voluntaria, inhabilitados o no registrados.
- No se otorgan préstamos si el socio tiene tres o más préstamos devueltos fuera de término, un préstamo en curso con más de siete días de demora o si alcanzó su límite de préstamos.
- No se otorgan préstamos de libros que no estén disponibles.
- No se renuevan préstamos vencidos ni préstamos cuya fecha límite esté a más de dos días.
- Los usuarios asociados permanecen registrados en la biblioteca aunque cambie su estado.
- La evaluación de usuarios inhabilita a quienes incumplen las reglas de demora.

### Pendientes de implementar

- Impedir la renovación si el socio incumple las reglas para solicitar préstamos o está de baja voluntaria o inhabilitado.
- Verificar las reglas de solicitud de préstamos al volver a asociar a un usuario.

## Búsqueda y filtrado

El catálogo permite buscar por coincidencia exacta de autor o título, y filtrar por categoría, disponibilidad e idioma. Los criterios se pueden combinar con `y`, `o` y `no`.

```java
CriterioDeBusqueda criterio = new BusquedaPorAutor("J. K. Rowling")
    .y(new BusquedaPorCategoria(Libro.Categoria.FANTASIA));
```

```java
CriterioDeBusqueda criterio = new BusquedaPorIdioma(Libro.Idioma.INGLES).no();
```

```java
CriterioDeBusqueda autor = new BusquedaPorAutor("J. K. Rowling");
CriterioDeBusqueda idioma = new BusquedaPorIdioma(Libro.Idioma.CASTELLANO);
CriterioDeBusqueda disponible = new BusquedaPorDisponibilidad();

CriterioDeBusqueda combinado = autor.o(idioma).y(disponible);
```

## Requisitos

- Java 21
- Maven

## Compilar y ejecutar las pruebas

Desde la carpeta raíz del proyecto, ejecutá:

```bash
mvn -f biblioteca/pom.xml clean test
```

## Información del proyecto

- Creadora: Agustina Emilia Agüero
- Versión: 1.1.0
- Última actualización: 2026-10-17