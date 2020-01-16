package com.example.chatonly

object Users { //Leftover class used as template to TextMessage.kt
    val userList: HashSet<String> = hashSetOf()//only one name can be added
    fun addUsername(username: String): Boolean {//stores username for client message displaying
        if (!isUsernameExist(username)) {
            userList.add(username)
            return true
        }
        return false
    }
    fun removeUsername(username: String) {//Unnecessary since disconnect/reconnect from socket resets userList
        if (isUsernameExist(username)) {
            userList.remove(username)
        }
    }
    fun isUsernameExist(username: String): Boolean {//check if username is already in hashSet
        if (username in userList) {
            return true
        } else {
            return false
        }
    }
    fun getUserList(): List<String> {//returns username for client message displaying
        return userList.toList()
    }
    override fun toString(): String {
        var users = userList.toList()
        var forrmatString: String = ""
        for (i in users) {
            forrmatString += "\n $i"
        }
        return forrmatString
    }
}