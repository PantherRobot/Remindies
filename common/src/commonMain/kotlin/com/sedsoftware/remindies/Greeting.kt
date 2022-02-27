package com.sedsoftware.remindies

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}