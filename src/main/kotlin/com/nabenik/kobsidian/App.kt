package com.nabenik.kobsidian

import com.nabenik.kobsidian.client.Credential
import com.nabenik.kobsidian.client.DropboxClient
import com.nabenik.kobsidian.ext.PgBackupCreator
import com.nabenik.kobsidian.filereader.SystemFileReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class App{

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {

            val backupFolder = args[0]
            val dbName = args[1]
            val backupName = dbName + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".backup"

            //1- Create backup
            val backupCreator = PgBackupCreator()
            val backupResult = backupCreator.doBackup(Credential(args[2],args[3]), dbName, backupFolder + backupName)

            //2- Create file stream
            val inputStream = SystemFileReader().createInputStream(backupFolder + backupName)

            //3- Upload to destination
            val credential = Credential("tuxtor",
                    "sl.AbIwQQYFsUeStPOvOtySR2UquDlQ2TQCjxGak4SQYpotqL9WKxL7Xy_ZYqIS2vPi6U2xOnXVkV4TweeqGo_1DDQrwddnvtVLyA1X-HS3eGU8gmnDMQ4MhxjpeLjosTvvF41_BkJi")
            val client = DropboxClient(credential)

            val result = client.uploadData(backupName, inputStream)
            println("The result is $result")

        }
    }
}

