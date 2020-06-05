package com.nabenik.kobsidian

import com.nabenik.kobsidian.client.Credential
import com.nabenik.kobsidian.dropbox.DropboxClient
import com.nabenik.kobsidian.config.BackupOptions
import com.nabenik.kobsidian.config.ConfigurationReader
import com.nabenik.kobsidian.ext.PgBackupCreator
import com.nabenik.kobsidian.filereader.SystemFileReader
import picocli.CommandLine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

/**
 * Kobsidian main class implementation. It uses @JvmStatic annotation on companion object to be compatible with any Java implementation
 *
 * @author Victor Orozco
 */
class App{

    private lateinit var backupConfig: BackupOptions

    /**
     * Creates instances for objects initialization and interaction
     */
    fun call():Int{
        val backupName = if(!backupConfig.backupPrefix.isNullOrEmpty()) {
            backupConfig.backupPrefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".backup"
        }else {
            backupConfig.databaseName + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".backup"
        }

        //1- Create backup
        val backupCreator = PgBackupCreator()
        val backupResult = backupCreator.doBackup(Credential(backupConfig.databaseUser!!,backupConfig.databasePassword!!),
            backupConfig.databaseName!!, backupConfig.destinationFolder + backupName)

        //2- Create file stream
        val inputStream = SystemFileReader().createInputStream(backupConfig.destinationFolder + backupName)

        //3- Upload to destination
        val credential = Credential(backupConfig.dropboxSecret!!,
            backupConfig.dropboxAuthorizationToken!!)
        val client = DropboxClient(credential)

        return client.uploadData(backupName, inputStream)
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val cmdOptions = BackupOptions()

            //1- Parse CMD arguments
            val callableApp = App()
            val cmd = CommandLine(cmdOptions)
            try {
                val cmdResult = cmd.parseArgs(*args)
                if (cmd.isUsageHelpRequested) {
                    cmd.usage(cmd.out)
                    exitProcess(cmd.commandSpec.exitCodeOnUsageHelp())
                } else if (cmd.isVersionHelpRequested) {
                    cmd.printVersionHelp(cmd.out)
                    exitProcess(cmd.commandSpec.exitCodeOnVersionHelp())
                }

                //2- Read from configuration file
                val fileOptions = ConfigurationReader.readConfig()
                val executionOptions = intersectProperties(cmdOptions, fileOptions)
                checkArguments(cmd, executionOptions)

                if (cmdOptions.bootstrapAuthorizationProcess){ //Exclusive argument
                    //Fire bootstrap process
                    DropboxClient.fileAuthenticationFlux(Credential(executionOptions.dropboxKey!!, executionOptions.dropboxSecret!!))
                    exitProcess(cmd.commandSpec.exitCodeOnSuccess())
                }

                callableApp.backupConfig = executionOptions
                val result = callableApp.call()
                cmd.setExecutionResult(result)
                exitProcess(cmd.commandSpec.exitCodeOnSuccess())
            }catch (ex: CommandLine.ParameterException) {
                cmd.err.println(ex.message)
                if (!CommandLine.UnmatchedArgumentException.printSuggestions(ex, cmd.err)) {
                    ex.commandLine.usage(cmd.err)
                }
                exitProcess(cmd.commandSpec.exitCodeOnInvalidInput())
            }catch (ex: Exception) {
                ex.printStackTrace(cmd.err)
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
                cmdOptions.dropboxSecret ?: fileOptions.dropboxSecret,
                cmdOptions.dropboxKey ?: fileOptions.dropboxKey,
                cmdOptions.dropboxAuthorizationToken ?: fileOptions.dropboxAuthorizationToken,
                cmdOptions.backupPrefix?: fileOptions.backupPrefix
            )
        }

        /**
         * Checks arguments value and returns and error message if some argument is blank or null
         */
        private fun checkArguments(cmd: CommandLine, backupOptions: BackupOptions){
            val errorMessage = when {
                backupOptions.databaseName.isNullOrEmpty() -> "Database name is mandatory"
                backupOptions.destinationFolder.isNullOrEmpty() -> "Destination folder name is mandatory"
                backupOptions.dropboxSecret.isNullOrEmpty() -> "Dropbox user is mandatory"
                backupOptions.dropboxKey.isNullOrEmpty() ->"Dropbox key is mandatory"
                else -> null
            }
            if(!errorMessage.isNullOrEmpty()){
                throw CommandLine.ParameterException(cmd, errorMessage)
            }
        }
    }
}

