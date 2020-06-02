package com.nabenik.kobsidian

import com.nabenik.kobsidian.client.Credential
import com.nabenik.kobsidian.client.DropboxClient
import com.nabenik.kobsidian.config.BackupOptions
import com.nabenik.kobsidian.config.ConfigurationReader
import com.nabenik.kobsidian.ext.PgBackupCreator
import com.nabenik.kobsidian.filereader.SystemFileReader
import picocli.CommandLine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

@CommandLine.Command(name = "kobsidian-backup", mixinStandardHelpOptions = true, version = ["kobsidian-backup 1.0.0"],
    description = ["Creates backups from Postgres and uploads these to Dropbox"])
class App{



    fun call(backupConfig: BackupOptions):Int{
        val backupName = backupConfig.databaseName + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".backup"

        //1- Create backup
        val backupCreator = PgBackupCreator()
        val backupResult = backupCreator.doBackup(Credential(backupConfig.databaseUser!!,backupConfig.databasePassword!!),
            backupConfig.databaseName!!, backupConfig.destinationFolder + backupName)

        //2- Create file stream
        val inputStream = SystemFileReader().createInputStream(backupConfig.destinationFolder + backupName)

        //3- Upload to destination
        val credential = Credential(backupConfig.dropboxUser!!,
            backupConfig.dropboxKey!!)
        val client = DropboxClient(credential)

        val result = client.uploadData(backupName, inputStream)
        println("The result is $result")

        return result
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val cmdOptions = BackupOptions()

            //1- Parse CMD arguments
            val callableApp = App()
            val cmd = CommandLine(callableApp);
            try {
                val cmdResult = CommandLine(cmdOptions).parseArgs(*args)
                if (cmd.isUsageHelpRequested) {
                    cmd.usage(cmd.out);
                    exitProcess(cmd.commandSpec.exitCodeOnUsageHelp())
                } else if (cmd.isVersionHelpRequested) {
                    cmd.printVersionHelp(cmd.out);
                    exitProcess(cmd.commandSpec.exitCodeOnVersionHelp())
                }

                //2- Read from configuration file
                val fileOptions = ConfigurationReader.readConfig()
                val executionOptions = intersectProperties(cmdOptions, fileOptions)
                checkArguments(cmd, executionOptions)

                val result = callableApp.call(executionOptions);
                cmd.setExecutionResult(result);
                exitProcess(cmd.commandSpec.exitCodeOnSuccess())
            }catch (ex: CommandLine.ParameterException) {
                cmd.err.println(ex.message);
                if (!CommandLine.UnmatchedArgumentException.printSuggestions(ex, cmd.err)) {
                    ex.commandLine.usage(cmd.err);
                }
                exitProcess(cmd.commandSpec.exitCodeOnInvalidInput())
            }catch (ex: Exception) {
                ex.printStackTrace(cmd.err);
                exitProcess(cmd.commandSpec.exitCodeOnExecutionException())
            }
        }

        /**
         * Mixes value between command line options and properties file
         */
        private fun intersectProperties(cmdOptions: BackupOptions, fileOptions: BackupOptions): BackupOptions {
            return BackupOptions(
                cmdOptions.databaseName ?: fileOptions.databaseName,
                cmdOptions.databaseUser ?: fileOptions.databaseUser,
                cmdOptions.databasePassword ?: fileOptions.databasePassword,
                cmdOptions.destinationFolder ?: fileOptions.destinationFolder,
                cmdOptions.dropboxUser ?: fileOptions.dropboxUser,
                cmdOptions.dropboxKey ?: fileOptions.dropboxKey
            )
        }

        /**
         * Checks arguments value and returns and error message if some argument is blank or null
         */
        private fun checkArguments(cmd: CommandLine, backupOptions: BackupOptions){
            val errorMessage = when {
                backupOptions.databaseName.isNullOrEmpty() -> "Database name is mandatory"
                backupOptions.destinationFolder.isNullOrEmpty() -> "Destination folder name is mandatory"
                backupOptions.dropboxUser.isNullOrEmpty() -> "Dropbox user is mandatory"
                backupOptions.dropboxKey.isNullOrEmpty() ->"Dropbox key is mandatory"
                else -> null
            }
            if(!errorMessage.isNullOrEmpty()){
                throw CommandLine.ParameterException(cmd, errorMessage)
            }
        }
    }
}

