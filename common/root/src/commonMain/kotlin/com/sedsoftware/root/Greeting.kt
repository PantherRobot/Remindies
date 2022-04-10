package com.sedsoftware.root

@Suppress("MemberNameEqualsClassName")
class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}
