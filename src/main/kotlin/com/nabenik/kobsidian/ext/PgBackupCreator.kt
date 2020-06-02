package com.nabenik.kobsidian.ext

import com.nabenik.kobsidian.client.Credential
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class PgBackupCreator{

    /**
     * Invokes postgres utility for backup
     *
     * If backup is successfull it returns 0, otherwhise -1 or an specific Nix code
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

            var line: String? = null
            do {
                line = outputReader.readLine()
                println(line)
            }while (line != null)
            outputReader.close()
            process.waitFor()
            return process.exitValue()
        }catch (ex: Exception){
            ex.printStackTrace()
            return -1;
        }
    }
}