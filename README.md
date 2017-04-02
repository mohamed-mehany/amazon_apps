# Amazon Apps

## Installation:

- Install Postgresql

```
    $ sudo apt-get update
    $ sudo apt-get install postgresql postgresql-contrib
```

- Login to the psql as superuser

```
    $ sudo -u postgres psql 
```

- Create a User/Database and Grant All Privilages 

```
    postgres=#  CREATE USER myUser WITH PASSWORD 'myPassword';
    postgres=#  CREATE DATABASE myDB;
    postgres=#  GRANT ALL PRIVILEGES ON DATABASE myDB to myUser;
    postgres=#  \q
```

- Run the `SQL Scripts`

```
    $ cd <path_to_project>
    $ cd db_scripts/
    $ psql -d myDB -f <script_name>.sql
```





## Running the app:

- import the project to the `IDE` you're using
- add the external jars under the `libs/` directory
- create a new file `settings.json` under the `config/` directory
- add the configrution to the file (you will find an example in `config/settings.example.json`) 
- run the app





## Workflow and Conventions:

- To start working on a new model create a new directory `src/commands/modelName/` (camelCase)
- Create a new class in the `src/commands/modelName/CommandNameCmd.java` (PascaleCase) [e.g: `AddUserCmd`] 
- Extend the `Command` class and implement the `Runnable` interface and add the `execute` method
- Example:

```
public class AddUserCmd extends Command implements Runnable {	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
  {
    // Implementation goes here
  }
}
```

- As a convention don't create any other methods other than the `execute(..)` method in the command class
- If you wanna add any methods/helpers for the model create another class under the same package and import it 