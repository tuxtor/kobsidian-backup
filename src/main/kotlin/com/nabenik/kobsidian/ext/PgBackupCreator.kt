package com.nabenik.kobsidian.ext

import com.nabenik.kobsidian.client.Credential
import picocli.CommandLine
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class PgBackupCreator{

    /**
     * Invokes postgres utility for backup
     *
     * If backup is successful it returns 0, otherwise -1 or an specific Nix code
     */
    fun doBackup(credential: Credential, dbName: String, destinationFile: String):Int{

        val runtime = Runtime.getRuntime()
        val processBuilder = ProcessBuilder("pg_dump",
            "--host", "localhost",
            "--port", "5432",
            "--username", credential.subject,
            "--no-password", "--verbose",
            "--file", destinationFile,
            dbName)

        try{
            val env = processBuilder.environment()
            env.put("PGPASSWORD", credential.token)

            val process = processBuilder.start()
            val outputReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?
            do {
                line = outputReader.readLine()
                println(line)
            }while (line != null)
            outputReader.close()
            process.waitFor()
            println("Postgres backup exit code ${process.exitValue()}")
            return process.exitValue()
        }catch (ex: Exception){
            ex.printStackTrace()
            return CommandLine.ExitCode.SOFTWARE;
        }
    }
}
