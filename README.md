
## How to generate a GraalVM executable

```console
$GRAALVM_HOME/bin/native-image --enable-https --no-fallback -H:IncludeResources="com.dropbox.core.trusted-certs.raw"   -jar target/kobsidian-backup-1.0-SNAPSHOT.jar
``` 
