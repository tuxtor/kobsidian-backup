package com.nabenik.kobsidian.config

import org.apache.commons.configuration2.builder.fluent.Configurations
import java.io.File
import java.lang.Exception

object ConfigurationReader {

    fun readConfig(): BackupOptions {
        val configs = Configurations()
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
                val config = configs.properties(configFile)
                return BackupOptions(
                    config.getString("database.name", null),
                    config.getString("database.user", null),
                    config.getString("database.password", null),
                    config.getString("backup.folder", null),
                    config.getString("dropbox.user", null),
                    config.getString("dropbox.key", null)
                )
                //TODO read all properties
            }
        }catch (ex: Exception){
            println(ex)
        }
        return BackupOptions()
    }
}


