# api_actividad2 — Todo.ly API Testing

Actividad 2 (Automatización II, semana 2): pruebas de la API pública de [Todo.ly](https://todo.ly) con **Java + Gradle + RestAssured + Cucumber**, sobre la estructura *clean* vista en clase (`FactoryRequest`, `RequestInfo`, `Configuration` y steps genéricos con variables).

## Flujo cubierto (flujo 1)

`src/test/resources/userProjectToken.feature`

1. Crear un usuario nuevo (sin autenticación). Email y password se generan en cada ejecución.
2. Obtener el **token** con las credenciales de ese usuario (Basic).
3. Crear, actualizar y eliminar un proyecto usando el **token** (header `Token`). Al eliminar se verifica `Deleted = true`.
4. Eliminar el usuario (token).
5. Comprobar que el usuario ya no existe: pedir el token con Basic devuelve `ErrorCode 105`.

Verificaciones: status 200, que la respuesta no traiga `ErrorCode` (Todo.ly responde 200 también en los errores), valores clave (`Email`, `FullName`, `Content`, `Deleted`) y esquema JSON de las respuestas de creación de usuario y de proyecto.

## Estructura

```
src/test/java
├── client    FactoryRequest, IRequest, RequestGet/Post/Put/Delete, RequestInfo
│             IAuth + NoAuth / BasicAuth / TokenAuth  (Strategy de autenticación)
├── config    Configuration (solo BASE_URL)
└── runner    MyStepdefsClean (steps genéricos), Runner (hooks)
src/test/resources
├── userProjectToken.feature
└── userSchema.json, projectSchema.json
```

- `RequestInfo` lleva la estrategia de autenticación; cada `Request*` la aplica con `info.getAuth().apply(given())`.
- En el feature se elige con `... with NONE|BASIC|TOKEN auth`. `BASIC` usa las variables `EMAIL` y `PASSWORD`; `TOKEN` usa la variable `TOKEN`.
- Las variables se guardan con `save "<atributo>" in "<VARIABLE>"` y se reemplazan en la URL, el body y los valores esperados. Convención: nombres de variable en MAYÚSCULAS, texto literal nunca en mayúsculas.
- Si el escenario falla a mitad, un hook `@After` elimina el usuario creado.
- No hay credenciales en el código.

## Ejecución

Requisitos: JDK 17+ (probado con JDK 26) y conexión a internet. No hace falta tener Gradle instalado: se puede usar el wrapper (`gradlew`).

**Terminal** (PowerShell, cmd o bash; las comillas evitan problemas con `@` en PowerShell):

```
./gradlew cucumberCli "-Psuite=@UserProjectToken"
```

- Sin `-Psuite` se ejecuta el tag por defecto `@Regression`.
- Reporte HTML: `report/cucumber-report.html`.

**IntelliJ IDEA**

1. Instalar el plugin **Cucumber for Java** (File → Settings → Plugins → Marketplace; acepta también *Gherkin*) y reiniciar.
2. Abrir la carpeta del proyecto como proyecto Gradle y esperar la sincronización.
3. Abrir `src/test/resources/userProjectToken.feature` y pulsar la flecha verde junto a `Feature` o `Scenario`.
4. Si aparece un error de *glue*: Run → Edit Configurations → plantilla *Cucumber java*: Main class `io.cucumber.core.cli.Main`, Glue `runner`.

## Nota sobre la documentación de la API

El 24/10/2026 la ApiWiki oficial (http://todo.ly/ApiWiki/) estaba caída (HTTP 500, error interno del servidor) y no había copia accesible en Internet Archive. El comportamiento de los endpoints usados (crear usuario sin auth, `GET /api/authentication/token.json` → `TokenString`, header `Token`, `PUT`/`DELETE /api/user/0.json`, CRUD de proyectos y formato `ErrorCode`/`ErrorMessage`) se verificó con pruebas reales contra la API.
