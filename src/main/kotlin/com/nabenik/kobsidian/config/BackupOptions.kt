package com.nabenik.kobsidian.config

import picocli.CommandLine

@CommandLine.Command(name = "kobsidian-backup", mixinStandardHelpOptions = true, version = ["kobsidian-backup 1.0.7"],
    description = ["Creates backups from Postgres and uploads these to Dropbox"])
class BackupOptions{

    constructor(){
        this.databaseName = null
        this.databaseUser = null
        this.databasePassword = null
        this.destinationFolder = null
        this.dropboxSecret = null
        this.dropboxKey = null
        this.dropboxAuthorizationToken = null
        this.backupPrefix = null
    }

    constructor(databaseName: String?, databaseUser: String?, databasePassword: String?, destinationFolder: String?, dropboxSecret: String?, dropboxKey: String?, dropboxAuthorizationToken: String?, backupPrefix: String?){
        this.databaseName = databaseName
        this.databaseUser = databaseUser
        this.databasePassword = databasePassword
        this.destinationFolder = destinationFolder
        this.dropboxSecret = dropboxSecret
        this.dropboxKey = dropboxKey
        this.dropboxAuthorizationToken = dropboxAuthorizationToken
        this.backupPrefix = backupPrefix
    }

    @CommandLine.Option(names = ["-d", "--database"],
        paramLabel = "DATABASE NAME", description = ["Database target for backup actions"]
    )
    var databaseName: String?

    @CommandLine.Option(names = ["-t", "--target"],
        paramLabel = "DESTINATION FOLDER", description = ["Destination folder for backup"]
    )
    var destinationFolder: String?

    @CommandLine.Option(names = ["-u", "--user"],
        paramLabel = "DATABASE USER", description = ["Database user with backup privileges"]
    )
    var databaseUser: String?

    @CommandLine.Option(names = ["-p", "--password"],
        paramLabel = "DATABASE PASSWORD", description = ["Database password with backup privileges"],
        interactive = true, arity = "0..1"
    )
    var databasePassword: String?

    @CommandLine.Option(names = ["-s", "--dropbox-secret"],
        paramLabel = "DROPBOX SECRET", description = ["Dropbox secret"]
    )
    var dropboxSecret: String?

    @CommandLine.Option(names = ["-k", "--dropbox-key"],
        paramLabel = "DROPBOX KEY", description = ["Dropbox connection key"]
    )
    var dropboxKey: String?

    @CommandLine.Option(names = ["-a", "--dropbox-token"],
            paramLabel = "DROPBOX AUTH TOKEN", description = ["Dropbox authorization token"]
    )
    var dropboxAuthorizationToken: String?

    @CommandLine.Option(names = ["-f", "--backup-prefix"],
            paramLabel = "OPTIONAL BACKUP PREFIX", description = ["Optional backup prefix on backup uploads"]
    )
    var backupPrefix: String?

    @CommandLine.Option(names = ["-b", "--bootstrap-auth"],
            paramLabel = "BOOTSTRAP AUTHORIZATION FLUX", description = ["Bootstraps dropbox authorization process"]
    )
    var bootstrapAuthorizationProcess = false


}
