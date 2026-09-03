# Book Exchange

- Created in year 2024

Book Exchange is a JavaFX desktop application for listing, discovering, and
reserving publications. It was developed as a Java coursework project and
demonstrates a layered desktop application backed by a relational database.

Users can register as clients, publish books and other reading material, browse
listings from other users, reserve and return publications, and leave threaded
reviews. Administrators can manage users and inspect publication and reservation
data.

## Features

### Accounts and access

- Client and administrator account types
- User registration and login
- BCrypt password hashing and verification
- Role-dependent screens and actions
- User profile creation, editing, and deletion

### Publication management

- Create, view, update, and delete publications
- Supported types: Book, Manga, and Periodical
- Type-specific forms and validation
- Publication status tracking (`AVAILABLE` and `RESERVED`)
- Duplicate-title validation
- Owners see their listings in Publication Management; the exchange view shows
  available listings from other users

### Exchange and reservations

- Browse publications available from other clients
- Filter by publication type and type-specific attributes
- Reserve and return publications
- Track the current borrower and reservation timestamp
- View reservation and return history
- Filter history by action and date range
- Separate views for borrowed and lent publications

### Reviews

- Leave reviews on publication owners
- Reply to existing reviews in a tree structure
- View, update, and delete review entries according to the active role

## Technology stack

| Technology | Purpose |
| --- | --- |
| Java 23 language target | Application language and runtime baseline |
| JavaFX 23 and FXML | Desktop interface and view definitions |
| Maven | Dependency management, compilation, and launching |
| Hibernate ORM 6.6 / Jakarta Persistence | Object-relational mapping and database access |
| MySQL Connector/J 8 | MySQL connectivity |
| MySQL Server 8 | Persistent relational storage |
| Lombok | Model constructors, getters, and setters |
| Favre BCrypt | Password hashing and verification |

The project has been verified with OpenJDK 26 while compiling to Java 23
bytecode.

## Project structure

```text
src/main/java/coursework/
├── fxControllers/         JavaFX controllers
├── hibenateControllers/   Generic and application-specific persistence logic
├── model/                 JPA entities and enums
├── persistence/           Shared database configuration and factory
├── utility/               Password utilities
├── utils/                 JavaFX helper utilities
├── Launcher.java          IDE/classpath entry point
└── StartGUI.java          JavaFX Application class

src/main/resources/
├── coursework/            FXML views
└── META-INF/
    └── persistence.xml    JPA and Hibernate defaults
```

## Requirements

- JDK 23 or newer (OpenJDK 26 is verified)
- MySQL Server 8.x
- Maven 3.x, or IntelliJ IDEA with bundled Maven

MySQL Workbench is optional but useful for inspecting the generated database.

## Database setup

MySQL must be running on `localhost:3306`. On Windows, verify the service from
PowerShell:

```powershell
Get-Service MySQL80
```

The application can create the `courseworkFour` database automatically when its
database user has sufficient permission. You can also create it manually:

```sql
CREATE DATABASE courseworkFour
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Hibernate creates and updates the tables on application startup. No SQL schema
import is required.

### Database credentials

Do not commit database passwords. Supply credentials through environment
variables:

| Environment variable | Default | Description |
| --- | --- | --- |
| `COURSEWORK_DB_URL` | `jdbc:mysql://localhost:3306/courseworkFour?...` | JDBC connection URL |
| `COURSEWORK_DB_USER` | `root` | MySQL username |
| `COURSEWORK_DB_PASSWORD` | Empty | MySQL password |

Equivalent Java system properties are `coursework.db.url`,
`coursework.db.user`, and `coursework.db.password`.

## Running in IntelliJ IDEA

1. Open the repository and import `pom.xml` as a Maven project.
2. Press **Reload All Maven Projects** in the Maven tool window.
3. Set the Project SDK and Maven Runner JRE to JDK 23 or newer.
4. Confirm that MySQL is running.
5. Open **Run > Edit Configurations** and create an Application configuration.
6. Set the main class to `coursework.Launcher`.
7. Add your credentials under **Environment variables**:

   ```text
   COURSEWORK_DB_USER=root;COURSEWORK_DB_PASSWORD=your_mysql_password
   ```

8. Run the configuration. Use the registration action on the login screen to
   create the first application user.

Run `coursework.Launcher`, not `coursework.StartGUI` directly. The wrapper avoids
JavaFX classpath/module-path detection issues in IDE application configurations.

## Running from PowerShell

Ensure `java` and `mvn` are available on `PATH`, then run:

```powershell
$env:COURSEWORK_DB_USER = "root"
$env:COURSEWORK_DB_PASSWORD = "your_mysql_password"
mvn clean javafx:run
```

Compile without opening the interface:

```powershell
mvn clean compile
```

## Development notes

- Database settings are centralized in `coursework.persistence.Database`.
- `persistence.xml` contains non-secret local-development defaults only.
- The project currently has no automated test suite; run `mvn clean compile`
  and exercise affected interface workflows before submitting changes.
- The chat controller and FXML are retained as unfinished coursework code, but
  the chat tab is not currently enabled in the main interface.
