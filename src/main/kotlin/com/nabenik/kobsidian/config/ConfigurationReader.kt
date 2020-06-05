package com.nabenik.kobsidian.config

import java.io.File
import java.io.OutputStream
import java.lang.Exception
import java.util.*

object ConfigurationReader {

    private fun loadConfigFile(): File?{
        return when {
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
    }


    fun readPropertyValue(key: String): String?{
        try {
            val configFile = loadConfigFile()
            if(configFile != null){
                val prop = Properties()
                prop.load(configFile.inputStream())
                return prop.getProperty(key, null)
            }
        }catch (ex: Exception){
            println(ex)
        }
        return null
    }

    fun writePropertyValue(key: String, value: String){
        try {
            val configFile = loadConfigFile()
            if(configFile != null){
                val prop = Properties()
                prop.load(configFile.inputStream())
                prop.setProperty(key, value);
                prop.store(configFile.outputStream(), "")
            }
        }catch (ex: Exception){
            println(ex)
        }
    }

    fun readConfig(): BackupOptions {
        try {
            val configFile = loadConfigFile()

            if(configFile != null){
                val prop = Properties()
                prop.load(configFile.inputStream())

                return BackupOptions(
                    prop.getProperty("database.name", null),
                    prop.getProperty("database.user", null),
                    prop.getProperty("database.password", null),
                    prop.getProperty("backup.folder", null),
                    prop.getProperty("dropbox.secret", null),
                    prop.getProperty("dropbox.key", null),
                    prop.getProperty("dropbox.auth", null),
                    prop.getProperty("backup.prefix", null)
                )
                //TODO read all properties
            }
        }catch (ex: Exception){
            println(ex)
        }
        return BackupOptions()
    }
}


