# GUÍA Y GUION DE SUSTENTACIÓN ORAL — APF3 (10 MINUTOS)
## PROYECTO DENTRIX — CURSO INTEGRADOR I (UTP)

---

### ESTRUCTURA GENERAL DE LA PRESENTACIÓN (TIEMPO TOTAL: 10 MINUTOS)

| Bloque / Tiempo | Expositor | Rol | Diapositivas | Contenido Clave a Dominar |
|:---:|:---|:---:|:---:|:---|
| **00:00 - 02:30** (2.5 min) | **Marc Anthony Berrocal** | PM | Diapositivas 1 a 3 | Contexto de la empresa, problemática odontológica, alcance comprometido y gestión del repositorio Git/GitHub. |
| **02:30 - 05:00** (2.5 min) | **Maick Vicente** | Frontend | Diapositivas 4 y 5 | Diseño de la solución, interfaces gráficas UX/UI en JavaFX 25, Scene Builder y flujo de navegación. |
| **05:00 - 07:30** (2.5 min) | **Yair Salamán** | Backend | Diapositivas 6 y 7 | Arquitectura multicapa MVC, patrón DAO, principios SOLID y modelo de datos relacional en PostgreSQL. |
| **07:30 - 10:00** (2.5 min) | **Yarol Bello** | QA / Test | Diapositivas 8 a 10 | Uso de recursos Java (Guava, POI, Commons, Logback), metodología TDD con 31 pruebas JUnit 5 y conclusiones. |

---

## GUION DETALLADO DIAPOSITIVA POR DIAPOSITIVA

### BLOQUE 1: CONTEXTO, ALCANCE Y CONTROL DE VERSIONES (00:00 - 02:30)
**Expositor:** Marc Anthony Berrocal Bernaola (Project Manager)

#### Diapositiva 1: Portada e Introducción al Proyecto Dentrix
> *"Buenos días profesora María Elena Tasa y compañeros. Mi nombre es Marc Anthony Berrocal, Project Manager del equipo Dentrix. Hoy tenemos el agrado de presentar nuestro Avance de Proyecto Final 3 para el desarrollo de la solución informática integral del Centro Odontológico Dentrix. Nuestro equipo está conformado por Maick Vicente como Frontend Lead, Yair Salamán como Backend Lead, Yarol Bello como Analista de Calidad, y mi persona en la gestión del proyecto."*

#### Diapositiva 2: Análisis del Contexto y Alcance de la Solución
> *"Analizando el contexto de los centros odontológicos en nuestro país, identificamos una alta fragmentación operativa: retrasos en la asignación de citas, historias clínicas dispersas en papel o ficheros aislados, y desconexión entre los tratamientos ejecutados en el sillón dental y la cobranza en caja.  
Para resolver esto, diseñamos Dentrix como una solución de escritorio corporativa y robusta basada en JavaFX y PostgreSQL. Para este APF3, el alcance comprometido cubría inicialmente el 40% del sistema; sin embargo, gracias a una gestión ágil eficiente, hemos consolidado el 100% de los 9 módulos clínicos y administrativos comprometidos: desde la autenticación con roles RBAC hasta la facturación y generación de reportes gerenciales."*

#### Diapositiva 3: Control de Versiones con Git y GitHub
> *"Para garantizar la integridad y trazabilidad del trabajo en equipo, integramos un flujo de control de versiones con Git y GitHub en nuestro repositorio oficial `clinica-dental-agenda`.  
Adoptamos una estrategia estructurada de ramificación: la rama `main` para código en producción estable, la rama `dev` para integración continua, y ramas individuales de trabajo para cada desarrollador bajo el estándar `rama/<apellido>`. Todos los avances están versionados mediante commits semánticos y Pull Requests con revisión cruzada de código. Además, protegemos los datos sensibles aislando las credenciales locales en `config.properties`, asegurando que nunca sean expuestas en el repositorio.  
A continuación, cedo la palabra a mi compañero Maick Vicente para detallar las interfaces gráficas."*

---

### BLOQUE 2: DISEÑO DE LA SOLUCIÓN E INTERFACES UX/UI (02:30 - 05:00)
**Expositor:** Maick Vicente (Frontend Lead)

#### Diapositiva 4: Enfoque de Diseño de Experiencia de Usuario (UX/UI en JavaFX)
> *"Muchas gracias Marc. Profesora, el objetivo central de nuestra capa de presentación fue ofrecer una experiencia de usuario moderna, limpia y altamente responsiva para el personal de la clínica.  
Decidimos utilizar JavaFX 25 con maquetación declarativa en FXML mediante Scene Builder y estilización modular con hojas de estilo CSS (`estilos.css`). Elegimos JavaFX sobre tecnologías legadas como Swing debido a su completa separación entre la vista y la lógica, la fluidez de sus transiciones y su compatibilidad con pantallas de alta resolución.  
Para no congelar la interfaz durante operaciones de base de datos, implementamos la clase utilitaria `TareasFX`, que ejecuta las consultas complejas en hilos secundarios y actualiza la vista de forma segura en el hilo gráfico de JavaFX."*

#### Diapositiva 5: Demostración de las 11 Vistas del Sistema
> *"El sistema implementa 11 interfaces de usuario completamente integradas:  
1. `login.fxml`: Autenticación con detección visual de bloqueo tras 3 intentos.  
2. `menu-principal.fxml`: Menú responsivo basado en `VBox` que adapta las opciones según el rol del usuario.  
3. `dashboard.fxml`: Métricas en tiempo real con 4 KPIs gerenciales del día.  
4. `agenda.fxml`: Agenda con visualización de citas, bloques de almuerzo y estados de atención.  
5. `sala-espera.fxml`: Monitor de flujo que permite al recepcionista derivar al paciente al consultorio.  
6. `pacientes.fxml`: Padrón con validación estricta de DNI único (RF-04).  
7. `ficha-clinica.fxml`: La interfaz clínica central, que integra el odontograma FDI interactivo con las piezas dentales 11 a 48, asociación de diagnósticos MINSA CIE-10 y pase a cobro.  
8. `caja.fxml`: Cobro multimodal con desglose de IGV al 18% y registro de arqueo con fondo inicial.  
9. `reportes.fxml`: Visualización de productividad médica y exportación de datos.  
10 y 11. `configuracion.fxml` y `usuarios.fxml`: Parametrización del centro médico y control de copias de seguridad de PostgreSQL con `pg_dump`.  
Paso ahora la posta a Yair Salamán para explicar la arquitectura interna."*

---

### BLOQUE 3: ARQUITECTURA MVC, SOLID Y PERSISTENCIA DAO (05:00 - 07:30)
**Expositor:** Yair Salamán (Backend Lead)

#### Diapositiva 6: Arquitectura Multicapa (MVC + Capa de Servicios)
> *"Buenas tardes profesora. La arquitectura de Dentrix está construida bajo el patrón Modelo-Vista-Controlador (MVC), fortalecido con una Capa de Servicios y una Capa de Persistencia formal.  
La regla de oro de nuestro diseño es el desacoplamiento total: la vista FXML jamás toca la base de datos, y los controladores nunca ejecutan código SQL. Los eventos fluyen desde el controlador hacia los Servicios (`service/`), donde residen todas las reglas del negocio, validaciones y cálculos. Estos servicios se comunican con la persistencia exclusivamente a través de contratos abstractos definidos en interfaces DAO."*

#### Diapositiva 7: Patrón DAO, Principios SOLID y Base de Datos Relacional
> *"El patrón DAO (*Data Access Object*) nos permite aislar el 100% de la persistencia JDBC en las clases `*DAOImpl`, encapsulando consultas parametrizadas con `PreparedStatement` para garantizar inmunidad total ante ataques de Inyección SQL.  
Aplicamos rigurosamente los 5 principios SOLID:
- **S (Responsabilidad Única):** Cada clase tiene un único propósito delimitado.
- **O (Abierto/Cerrado) y L (Liskov):** Los servicios consumen interfaces DAO, permitiendo sustituir o extender el almacenamiento sin modificar la lógica de negocio.
- **I (Segregación de Interfaces):** Descartamos DAOs monolíticos y creamos contratos específicos para cada una de las 14 tablas del dominio.
- **D (Inversión de Dependencias):** Los módulos de alto nivel dependen de abstracciones, inyectadas a través de nuestro singleton contenedor `AppContext`.  
Todo esto descansa sobre nuestro modelo de base de datos relacional v2 en PostgreSQL con 14 tablas, donde implementamos triggers para auditar el fondo de caja en formato JSONB y controlar el saldo deudor de boletas y abonos de manera atómica.  
Dejo con ustedes a Yarol Bello para presentar el uso de librerías y las pruebas unitarias."*

---

### BLOQUE 4: RECURSOS JAVA, TDD CON JUNIT 5 Y CONCLUSIONES (07:30 - 10:00)
**Expositor:** Yarol Bello (QA / Test Engineer)

#### Diapositiva 8: Uso de Recursos y Librerías Java Empresariales
> *"Muchas gracias Yair. Profesora, para optimizar la robustez, eficiencia y seguridad de la solución, integramos en el proyecto las 4 librerías de apoyo a la codificación requeridas en la rúbrica del curso:  
1. **Google Guava:** Implementamos `Preconditions` (`checkNotNull` y `checkArgument`) en los servicios de negocio y acceso a datos para validar estados antes de procesar cualquier transacción.  
2. **Apache POI:** Desarrollamos un motor de exportación en `ReporteService` que genera libros nativos de Microsoft Excel (`.xlsx`) con estilos corporativos en las cabeceras, formato monetario y autoajuste de columnas para reportes gerenciales.  
3. **Apache Commons Lang3:** Empleamos utilitarios de saneamiento de cadenas (`StringUtils.isNotBlank`, `StringUtils.trimToNull`) para normalizar entradas de usuario y prevenir errores de formato.  
4. **Logback y SLF4J:** Configuramos `logback.xml` con rotación diaria automática hacia archivos `.log` y salida estructurada en consola, permitiendo auditar eventos de autenticación, inicios de sesión y errores sin exponer información sensible ni contraseñas.  
A esto se suma el pool de conexiones **HikariCP** para maximizar el rendimiento y **jBCrypt** para el cifrado irreversible de credenciales."*

#### Diapositiva 9: Metodología TDD y Suite de Pruebas Automatizadas (JUnit 5)
> *"Bajo la filosofía de Desarrollo Dirigido por Pruebas (TDD), implementamos una suite automatizada con **JUnit 5 (Jupiter)** que consta de **31 pruebas unitarias** que cubren el 100% de los servicios: configuración, autenticación con bloqueo de seguridad, agenda, pacientes, caja y generación de reportes con Apache POI.  
Al ejecutar `mvn clean test`, las 31 pruebas se ejecutan satisfactoriamente en menos de 11 segundos con cero fallos y cero errores, demostrando la alta estabilidad y fiabilidad técnica del código desarrollado."*

#### Diapositiva 10: Conclusiones y Demostración Final
> *"En conclusión:  
1. Hemos cumplido al 100% con todos los criterios de la rúbrica de APF3: Arquitectura MVC, Patrón DAO, Principios SOLID, Seguridad integral y las 4 librerías Java requeridas.  
2. El sistema Dentrix se encuentra plenamente funcional con sus 11 vistas y sus 31 pruebas unitarias automatizadas.  
3. Contamos con la documentación técnica formal completa generada con Javadoc y el informe técnico detallado.  
Agradecemos su atención y quedamos a su entera disposición para responder a las preguntas que considere pertinentes. Muchas gracias."*

---

## BANCO DE PREGUNTAS Y RESPUESTAS PREPARADAS PARA LA EVALUACIÓN

**P1: ¿En qué parte del código se evidencia el principio de Inversión de Dependencias (DIP) de SOLID?**  
> *R: Se evidencia en la clase `AppContext` y en los constructores de la capa de servicio (como `PacienteService` o `CajaService`). Los servicios no instancian directamente `new PacienteDAOImpl()`, sino que reciben por inyección la interfaz `PacienteDAO`. Esto permite desacoplar la lógica de negocio de la implementación de la persistencia.*

**P2: ¿Cómo garantiza el sistema la seguridad de las contraseñas y la protección contra Inyecciones SQL?**  
> *R: Las contraseñas nunca se guardan en texto claro; se hashean con BCrypt mediante `PasswordUtil` usando la librería `jBCrypt`. En cuanto a la base de datos, el 100% de las consultas e inserciones en los DAOs utilizan `PreparedStatement` con parámetros vinculados (`?`), eliminando cualquier riesgo de concatenación de texto malicioso.*

**P3: ¿Qué ventaja ofrece usar Apache POI en lugar de simplemente generar un archivo CSV?**  
> *R: CSV es un formato de texto plano sin tipos de datos ni diseño. Apache POI nos permite generar un archivo `.xlsx` binario nativo de Excel con múltiples hojas organizadas, estilos visuales institucionales, paleta de colores corporativa, formato numérico de moneda real y ajuste automático de ancho de celdas, elevando el valor gerencial del reporte.*

**P4: ¿Cómo aplicaron el concepto de TDD en el proyecto?**  
> *R: Diseñamos primero los casos de prueba unitarios en `src/test/java` definiendo los resultados esperados (por ejemplo, el bloqueo tras 3 intentos fallidos de login o la prohibición de dos citas a la misma hora en el mismo consultorio). Luego implementamos el código de servicio hasta que la prueba pase a verde, procediendo a refactorizar el código con Clean Code.*
