package com.nabenik.kobsidian.dropbox

import com.dropbox.core.*
import com.dropbox.core.v2.DbxClientV2
import com.nabenik.kobsidian.client.Credential
import com.nabenik.kobsidian.client.UploadClient
import com.nabenik.kobsidian.config.ConfigurationReader
import picocli.CommandLine
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess

/**
 * Connects and uploads backup file to dropbox
 *
 * @author tuxtor
 */
class DropboxClient(credential: Credential) :
        UploadClient {

    private val client: DbxClientV2

    init {
        val config = DbxRequestConfig.newBuilder("kobsidian-backup").build()
        client = DbxClientV2(config, credential.token)

        val account = client.users().currentAccount
        println(account.name.displayName)
    }

    override fun uploadData(filename:String, stream: InputStream):Int{
        return try {
            client.files().uploadBuilder("/${filename}")
                    .uploadAndFinish(stream)
            CommandLine.ExitCode.OK;
        }catch (ex:Exception){
            ex.printStackTrace()
            CommandLine.ExitCode.SOFTWARE;
        }
    }

    companion object{
        /**
         * Fires Dropbox server authentication flux and saves auth token to properties file
         *
         * @param dropboxCredential An instance of Credential in the form of (Dropbox Key, Dropbox Secret) pair
         */
        fun fileAuthenticationFlux(dropboxCredential: Credential) {
            val appInfo = DbxAppInfo(dropboxCredential.subject, dropboxCredential.token)

            val requestConfig = DbxRequestConfig("kobsidian-backup")
            val webAuth = DbxWebAuth(requestConfig, appInfo)
            val webAuthRequest = DbxWebAuth.newRequestBuilder()
                    .withNoRedirect()
                    .build()
            val authorizeUrl = webAuth.authorize(webAuthRequest)
            println("1. Go to $authorizeUrl")
            println("2. Click \"Allow\" (you might have to log in first).")
            println("3. Copy the authorization code.")
            print("Enter the authorization code here: ")
            var code = BufferedReader(InputStreamReader(System.`in`)).readLine()
            if (code == null) {
                exitProcess(1)
                return
            }
            code = code.trim { it <= ' ' }
            val authFinish: DbxAuthFinish
            authFinish = try {
                webAuth.finishFromCode(code)
            } catch (ex: DbxException) {
                System.err.println("Error in DbxWebAuth.authorize: " + ex.message)
                exitProcess(1)
                return
            }
            println("Authorization complete.")
            println("Saving access Token: " + authFinish.accessToken)
            ConfigurationReader.writePropertyValue("dropbox.auth", authFinish.accessToken)
        }
    }
}
