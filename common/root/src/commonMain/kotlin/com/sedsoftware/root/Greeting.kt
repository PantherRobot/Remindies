package com.sedsoftware.root

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}