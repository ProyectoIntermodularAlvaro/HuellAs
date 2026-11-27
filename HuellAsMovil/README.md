# HuellAs - Sprint 1 (Android / Kotlin)

Proyecto intermodular de 2.º DAM. IES Maestre de Calatrava.

Sprint 1 (28/10/2026 - 28/11/2026): prototipo base del sistema cliente-servidor, con registro de
dueños y mascotas y persistencia en ficheros binarios.

## Abrir y ejecutar

1. Android Studio: **File > Open** y elegir esta carpeta.
2. Esperar a que Gradle descargue las dependencias (necesita internet la primera vez).
3. **Run 'app'** en un emulador o en un móvil con Android 7.0 (API 24) o superior.

## Uso

1. Pulsar **¿No tienes cuenta? Regístrate** y rellenar los datos. La contraseña necesita al menos
   8 caracteres.
2. Volver al acceso, escribir la contraseña y pulsar **Entrar**.
3. Pulsar el botón **+** para registrar una mascota.
4. Cerrar la aplicación y volver a abrirla: los datos siguen guardados.

## Arquitectura

La aplicación arranca un servidor de sockets dentro del propio proceso y la interfaz se conecta a
él como cliente. La arquitectura cliente-servidor es real aunque todo se ejecute en el mismo
móvil: la pantalla no llama a la capa de negocio, le envía peticiones.

```
app/src/main/java/es/huellas/
├── model/        Usuario, Dueno, Animal, EstadoAnimal
├── repository/   EscritorObjetos, LectorObjetos, DuenoRepository, AnimalRepository
├── service/      HuellasService
├── net/          MensajeRequest, MensajeResponse, Servidor, ConexionServidor
├── controller/   HuellasController
├── viewmodel/    HuellasViewModel, EstadoUi
├── ui/           Pantallas Jetpack Compose
├── HuellasApplication.kt
└── MainActivity.kt
```

## Dónde se guardan los datos

En la carpeta privada de la aplicación (`filesDir`), en dos ficheros binarios:

- `duenos.dat` - usuarios registrados
- `animales.dat` - mascotas registradas

Se regeneran solos: si se desinstala la app o se borran los datos, arranca vacía sin error.

## Pruebas

```
./gradlew test
```

47 pruebas unitarias con JUnit 4. No necesitan emulador: se ejecutan sobre la JVM porque cubren
el modelo, los repositorios, el servicio y la comunicación cliente-servidor, que no dependen del
framework de Android.
