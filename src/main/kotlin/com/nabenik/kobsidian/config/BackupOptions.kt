package com.nabenik.kobsidian.config

import picocli.CommandLine

@CommandLine.Command(name = "kobsidian-backup", mixinStandardHelpOptions = true, version = ["kobsidian-backup 1.0.0"],
    description = ["Creates backups from Postgres and uploads these to Dropbox"])
class BackupOptions{

    constructor(){
        this.databaseName = null
        this.databaseUser = null
        this.databasePassword = null
        this.destinationFolder = null
        this.dropboxUser = null
        this.dropboxKey = null
    }

    constructor(databaseName: String?, databaseUser: String?, databasePassword: String?, destinationFolder: String?, dropboxUser: String?, dropboxKey: String?){
        this.databaseName = databaseName
        this.databaseUser = databaseUser
        this.databasePassword = databasePassword
        this.destinationFolder = destinationFolder
        this.dropboxUser = dropboxUser
        this.dropboxKey = dropboxKey
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

    @CommandLine.Option(names = ["-b", "--dropbox-user"],
        paramLabel = "DROPBOX USER", description = ["Dropbox user"]
    )
    var dropboxUser: String?

    @CommandLine.Option(names = ["-k", "--dropbox-key"],
        paramLabel = "DROPBOX KEY", description = ["Dropbox connection key"]
    )
    var dropboxKey: String?

}
