package com.nabenik.kobsidian.client

/**
 * General abstraction that represents subject and token access abstactions like
 *
 * 1- User-password
 * 2- Key-secret
 * 3- Id-token
 *
 * @author Victor Orozco
 */
data class Credential(val subject:String, val token:String)
