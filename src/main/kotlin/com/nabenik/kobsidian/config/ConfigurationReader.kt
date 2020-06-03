package com.nabenik.kobsidian.config

import java.io.File
import java.lang.Exception
import java.util.*

object ConfigurationReader {

    fun readConfig(): BackupOptions {
        try {
            val configFile = when {
                File("kobsidian.properties").exists() -> {
                    //Test with kobsidian.config in classpath
                    File("kobsidian.properties")
                }
                File(System.getProperty("user.home") + "/.kobsidianconfig").exists() -> {
                    //Test with kobsidian.config in classpath
                    File(System.getProperty("user.home") + "/.kobsidianconfig")
                }
                File("/etc/kobsidian/kobsidian.properties").exists() -> {
                    //Test with /etc/kobsidian/kobsidian.config - Linux and Mac only
                    File("/etc/kobsidian/kobsidian.properties")
                }
                else -> {
                    null
                }
            }

            if(configFile != null){
                val prop = Properties()
                prop.load(configFile.inputStream())

                return BackupOptions(
                    prop.getProperty("database.name", null),
                    prop.getProperty("database.user", null),
                    prop.getProperty("database.password", null),
                    prop.getProperty("backup.folder", null),
                    prop.getProperty("dropbox.user", null),
                    prop.getProperty("dropbox.key", null)
                )
                //TODO read all properties
            }
        }catch (ex: Exception){
            println(ex)
        }
        return BackupOptions()
    }
}


