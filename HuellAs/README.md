# HuellAs - Sprint 1

Proyecto intermodular de 2.º DAM. IES Maestre de Calatrava.

Sprint 1 (28/10/2026 - 28/11/2026): prototipo base del sistema cliente-servidor, con registro de
dueños y mascotas y persistencia en ficheros binarios.

## Ejecutar en NetBeans

1. File > Open Project y elegir esta carpeta (NetBeans la abre como proyecto Maven).
2. Run Project (F6).

Se abre la ventana de HuellAs. La clase principal (`huellas.app.MainApp`) ya está indicada en el
`pom.xml`, así que no hay que configurar nada.

Para probar el servidor en una máquina aparte está `huellas.app.ServidorApp`: clic derecho sobre
el archivo > Run File.

## Uso

1. Pulsar **Crear cuenta** y rellenar los datos. La contraseña necesita al menos 8 caracteres.
2. Volver al acceso, escribir la contraseña y pulsar **Entrar**.
3. En la tabla de mascotas, pulsar **Registrar mascota**.
4. Cerrar la aplicación y volver a abrirla: los datos siguen en `datos/`.

## Estructura

```
src/main/java/huellas/
├── model/        Usuario, Dueno, Animal, EstadoAnimal
├── repository/   EscritorObjetos, LectorObjetos, DuenoRepository, AnimalRepository
├── service/      HuellasService (reglas de negocio)
├── net/          MensajeRequest, MensajeResponse, Servidor, ConexionServidor
├── controller/   HuellasController (une la vista con el servidor)
├── viewmodel/    MascotasTableModel
├── view/         VentanaPrincipal y paneles
└── app/          MainApp, ServidorApp
```

## Pruebas

```
mvn test
```

50 pruebas con JUnit 5. No abren ventanas, así que funcionan sin entorno gráfico.
