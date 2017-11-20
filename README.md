# ScoutJooq
Scout jOOQ - Eclipse Scout Template Application using jOOQ for Persistence

## Usage

The archetype is currently not deployed on any repository. You need to install it locally:


```bash
mvn clean install

```

Once it is in your local catalog you can generate a project from it:

```bash
mvn archetype:generate \
-DarchetypeGroupId=org.eclipse.scout.archetypes \
-DarchetypeArtifactId=basic-application-template
```

Properties used in the archetype

* groupId
* artifactId
* version
* package
* appName
* javaVersion

## Post Generation Tasks

All config.properties files need to be updated with proper security keys. These can be generated with the appropriate scout utilities

## Changing from Postgres to other Database

There are several tasks to be performed:

1. Change the JOOQ Dialect and related items in the database project.
2. Import an appropriate driver in the database project pom.
3. Change config.properties files to use the correct jdbc string and driver name
