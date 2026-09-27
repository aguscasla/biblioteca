# Biblioteca
Un sistema de biblioteca capaz de otorgar préstamos de libros hacia sus socios.

## Acerca de la biblioteca
La biblioteca tendrá un catálogo de todos sus libros, una lista de sus socios identificados por dni, una lista de los préstamos dados. Los libros podrán prestarse únicamente a los socios activos de la biblioteca, estos socios pueden pedir una cantidad limitada de libros, si el tiempo límite se les acaba también podrán renovarlo. 
El sistema registra a una persona cuando se asocia por primera vez a la biblioteca. Ese registro se conserva aunque deje de ser socio. Cada socio tiene un estado: activo, baja voluntaria o inhabilitado. Una persona con baja voluntaria puede volver a asociarse; una persona inhabilitada por incumplir las normas no puede volver a hacerlo. Las personas que no están registradas en la colección no pertenecen al sistema.

## ¿Qué se sabe de un libro?
Un libro esta compuesto por varios atributos, y son los siguientes:
* Título
* Autor
* Categoría
* Género (en caso de los libros de literatura)
* Fecha de publicación
* Estado
* ID
* Cantidad de páginas
* Idioma

Ademas, cada ID debe ser único por libro. Los estados de un libro pueden ser los siguientes; disponible, prestado, en reparación, e inhabilitado, este último no permite al libro ser agregado de nuevo al catálogo.

## Acerca del usuario
Un usuario puede existir o iniciar el trámite de asociación sin ser todavía socia. El sistema la registra en la colección de socios cuando la biblioteca formaliza su alta por primera vez. Esa alta no requiere requisitos previos. Desde ese momento, la persona queda registrada de forma permanente y su estado inicial es activo.
Del usuario se conoce sus siguientes datos:
* Nombre
* Apellido
* DNI
* Edad
* Teléfono
* Correo electrónico
* Límite de préstamos
* Estado (activo, inhabilitado, baja voluntaria)

Aparte de sus datos, el sistema debe conocer si este se encuentra asociado o no a la biblioteca, al igual que su historial de préstamos. El límite de préstamos corresponde a la cantidad de libros que puede tener en posesóon al mismo tiempo, si llega al límite no puede pedir algún otro libro hasta que se devuelvan los anteriores. Un socio con baja voluntaria puede volver a asociarse. Una persona inhabilitada por incumplir las normas no puede volver a asociarse.

## El funcionamiento de los préstamos
Cada préstamo representa la entrega de un ejemplar físico a un socio. El préstamo queda registrado en un único historial general de la biblioteca. Los préstamos en curso se obtienen filtrando ese historial por estado; los de un socio, buscando los préstamos asociados a su DNI o a su registro de socio. Así se evita guardar y actualizar el mismo préstamo en varios historiales.

### Atributos de un préstamo
* ID: identificador único del préstamo.
* Socio: socio al que se entregó el ejemplar.
* Ejemplar: libro físico prestado, identificado por su ID.
* Fecha de inicio: día en que se realiza el préstamo.
* Fecha límite: último día para devolverlo sin atraso.
* Fecha de devolución: se completa cuando el ejemplar es devuelto; mientras siga en curso, queda vacía.
* Cantidad de renovaciones: número de veces que se extendió el préstamo.

### Estado y vencimiento
Un préstamo tiene uno de estos estados:
* En curso: el ejemplar todavía no fue devuelto.
* Finalizado: el ejemplar fue devuelto.

Un préstamo en curso está **vencido** si la fecha actual es posterior a su fecha límite. El vencimiento es una condición calculada, no un estado que reemplace a “en curso”: un préstamo vencido sigue ocupando un lugar del límite del socio hasta que se devuelve el ejemplar.
Al devolverlo, el préstamo pasa a finalizado y se registra la fecha de devolución. El historial conserva el préstamo y permite determinar si se devolvió a tiempo o con atraso.

### Reglas para otorgar un préstamo
El sistema otorga un préstamo solo si se cumplen todas estas condiciones:
1. El usuario sea socio activo.
2. El socio no está inhabilitado ni tiene una restricción que le impida solicitar préstamos.
3. No alcanzó su límite de ejemplares en posesión. Para calcularlo, se cuentan todos sus préstamos en curso, incluidos los vencidos.
4. El ejemplar existe y está disponible.

Al otorgar el préstamo, el sistema registra su fecha de inicio y fecha límite, lo agrega al historial general y cambia el estado del ejemplar a **prestado**.

### Renovación
Un préstamo puede renovarse **una sola vez**, siempre que:
* siga en curso;
* no esté vencido;
* el socio solicite la renovación durante los dos días anteriores a la fecha límite, incluidos esos dos días;
* el ejemplar no tenga una reserva pendiente de otro socio.

La renovación extiende la fecha límite siete días a partir de la fecha límite vigente. Por ejemplo, si vence el 10/08, al renovarlo pasa a vencer el 17/08. La renovación se registra aumentando la cantidad de renovaciones.

### Devolución
Al recibir el ejemplar, el sistema registra la fecha de devolución y finaliza el préstamo. La devolución no borra ni modifica el historial del préstamo.

### Relación con el historial del socio
El historial de un socio se consulta a partir del historial general de préstamos. Incluye tanto préstamos finalizados como préstamos en curso. La cantidad de ejemplares que tiene en posesión se calcula contando sus préstamos en curso; no hace falta mantener una segunda lista de libros en posesión, porque podría quedar desactualizada respecto de los préstamos.

### Vencimientos e inhabilitación
El sistema conserva todos los préstamos vencidos, estén devueltos o sigan en curso. Para aplicar sanciones, se deben contar por separado:
* **Préstamos vencidos devueltos:** préstamos finalizados cuya devolución fue posterior a la fecha límite.
* **Préstamos vencidos pendientes:** préstamos en curso cuya fecha límite ya pasó.

## Reglas de negocio.
El sistema debera responder si un libro, segun su codigo identificador, esta disponible para un prestamo. Tambien debe dar de alta y baja a los socios, agregar y eliminar libros del catálogo. Cada tanto, el sistema puede revisar uno por uno a sus socios, y evaluar asi quienes pueden ser dados de baja y quedar inhabilitados si no cumplen ciertas condiciones. 

## Baja automatica a un socio.
La baja automática se aplica si el socio acumula tres o más préstamos vencidos devueltos, o si mantiene al menos un ejemplar sin devolver durante más de siete días después de su fecha límite. Un préstamo que continúa en curso no se cuenta también como un préstamo vencido devuelto. Al inhabilitar al socio, el préstamo pendiente sigue registrado y el ejemplar continúa figurando como prestado hasta su devolución.

## Ejemplos de préstamos
### Préstamo y renovación

Un socio activo solicita un ejemplar disponible y cumple las condiciones para pedirlo. El sistema registra el préstamo con su fecha de inicio y fecha límite, y marca el ejemplar como prestado.

Si el socio solicita renovar dentro del plazo permitido y cumple las condiciones, el sistema extiende la fecha límite siete días. La renovación queda registrada en el mismo préstamo.

### Préstamo vencido y devolución

Un socio conserva un ejemplar después de su fecha límite. El préstamo sigue en curso, pero se considera vencido; continúa contando para el límite de préstamos del socio y el ejemplar sigue prestado.

Al devolverlo, el sistema registra la fecha de devolución y finaliza el préstamo. El ejemplar queda disponible si está en condiciones de circular; si necesita reparación, pasa a estar en reparación.