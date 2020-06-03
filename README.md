# Kobsidian Backup
Kobsidian backup is a backup command executor and file uploader written in Kotlin. It is aimed to receive backup parameters from config files and/or cli arguments to fire backups from native tools and upload these backups to http destinations.

The current implementation supports backing from

* PostgreSQL

And uploading the backups to

* Dropbox

## Technical details

Project configuration produces two targets:

1. Jar without dependencies (aimed for tight integration with systems)
2. Self contained Native Image (executable on Linux)

The project was created with

* GraalVM CE 20.1.0
* Kotlin 1.3.72
* PicoCLI 
* DropboxSDK

## Building

To build this project you will need a GraalVM CE installation with native image and JAVA_HOME pointing to it:

You could execute the following goal to obtain the jar version:

```console
mvn -B clean package
```

And the following goal to obtain the Native Image version (tested on Linux and MacOS)

```console
mvn -B clean verify
```

## Minimal configuration

At this time kobsidian offers the following arguments/properties:

* Argument: ```--database```/Property: ```database.name``` - Database name/instance to be backuped  (mandatory)
* Argument: ```--user```/Property: ```database.user``` - Database user with dump execution privileges
* Argument: ```--password```/Property: ```database.password``` - Database password for the given user, interactive argument
* Argument: ```--target```/Property: ```backup.folder``` - Local filesystem folder where backup will be saved  (mandatory)
* Argument: ```--dropbox-user```/Property: ```dropbox.user``` - Dropbox user account name for backups upload  (mandatory)
* Argument: ```--dropbox-key```/Property: ```dropbox.key``` - Dropbox Oauth Key for backups upload  (mandatory)

As always, you could check command help:

```console
$ kobsidian-backup -h
Usage: kobsidian-backup [-hV] [-p[=DATABASE PASSWORD]] [-b=DROPBOX USER]
                        [-d=DATABASE NAME] [-k=DROPBOX KEY] [-t=DESTINATION 
                        FOLDER] [-u=DATABASE USER]
Creates backups from Postgres and uploads these to Dropbox
  -b, --dropbox-user=DROPBOX USER
                             Dropbox user
  -d, --database=DATABASE NAME
                             Database target for backup actions
  -h, --help                 Show this help message and exit.
  -k, --dropbox-key=DROPBOX KEY
                             Dropbox connection key
  -p, --password[=DATABASE PASSWORD]
                             Database password with backup privileges
  -t, --target=DESTINATION FOLDER
                             Destination folder for backup
  -u, --user=DATABASE USER   Database user with backup privileges
  -V, --version              Print version information and exit.

``` 
Besides command line arguments, configuration could be saved by using [properties files](https://en.wikipedia.org/wiki/.properties). 

Configuration follows a priority, hence it will stop on the first existing file using the following priority

0. `kobsidian.properties` - Next to executable
1. `~/.kobsidianconfig` - Next to executable
2. `/etc/kobsidian/kobsidian.properties` - Next to executable


## How to generate a GraalVM Native Image executable from Jar File

If you want to generate the native image binary manually you could personalize the following comand. Note that Dropbox trusted-certs.raw is explicitly included to Native Image process.

```console
$GRAALVM_HOME/bin/native-image --enable-https --no-fallback -H:IncludeResources="com.dropbox.core.trusted-certs.raw"   -jar target/kobsidian-backup-1.0-SNAPSHOT.jar
``` 
