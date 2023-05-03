# MediaServer
Music player-server running in Java servlets with simple webpage frontend for playing self-hosted music.

## Development
### Prerequirements
* [PostgreSQL](https://www.postgresql.org/) is our database
* [Maven](https://maven.apache.org/index.html) for building
* [Tomcat](https://tomcat.apache.org/) for hosting the app

### Setting up the app on Tomcat
Build the project with Maven: `mvn clean package` which packages it to `/target/`-folder in project's directory. You can deploy it by copying either `/target/MediaServer.war` or `/target/MediaServer/`-folder to the `/webapps/`-folder in Tomcat's directory.

Many IDEs and editors have a plugin for quickly setting up a Tomcat, which makes running project pretty easy for development purposes. 

### Setting up the PostgreSQL with Tomcat
Create definition in [Tomcat Context](https://tomcat.apache.org/tomcat-10.1-doc/jndi-datasource-examples-howto.html#PostgreSQL)

```
<Context>

<Resource name="jdbc/postgres" auth="Container"
          type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://127.0.0.1:5432/mydb"
          username="myuser" password="mypasswd" maxTotal="20" maxIdle="10"
maxWaitMillis="-1"/>
</Context>
```

## Background of the project
Hobby/learning project to get more comfortable with fullstack programming. The code you see here is not in any way professional and at the moment it's pretty messy.

Currently the project has been on a pause for few years but I thought that I should put it up anyway. I'm trying to get back to it and make it clearer and document it better.