package com.nabenik.kobsidian.client

/**
 * General abstraction that represents subject and token access abstactions like
 *
 * 1- User-password
 * 2-
 */
data class Credential(val subject:String, val token:String)
