package com.example.chatonly
object TextMessage {//Object used to compile the message for the Android client
    val textMessage: HashSet<String> = hashSetOf() //Message from Android clients text input
    val outsideMessage:HashSet<String> = hashSetOf()

    fun addMessage(givenText: String) {
            textMessage.add(givenText)
     }
    fun addOutsideMessage(givenText: String) {
        outsideMessage.add(givenText)
    }
    fun removeMessage() {
            textMessage.clear()
    }
    fun removeOutsideMessage() {
        outsideMessage.clear()
    }
    fun getTheMessage(): List<String> {
        return textMessage.toList()
    }
    fun getTheOutsideMessage(): List<String> {
        return outsideMessage.toList()
    }
    override fun toString(): String {
        var users = textMessage.toList()
        var forrmatString: String = ""
        for (i in users) {
            forrmatString += "\n $i"
        }
        return forrmatString
    }
}



