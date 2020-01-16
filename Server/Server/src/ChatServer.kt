import java.net.ServerSocket
//ChatServer CREATES CONNECTION AND STARTS THE APPLICATION
class ChatServer {
    //ESTABLISHES CONNECTION
    fun serve() {
        try {
            val serverSocket = ServerSocket(52886)//(0) = Socket is not reserved to a specific port
            println("Connect to port: " + serverSocket.localPort)//Tells user(s) what port to connect
            while (true) {//Loop always on
                val s = serverSocket.accept()//Forces loop to wait acceptable connection
                println("new connection " + s.inetAddress.hostAddress + " " + s.port)//Print of connection confirmation and info
                val t = Thread(CommandInterpreter(s.getInputStream(), s.getOutputStream(), client = s))//Creates new thread for user connection
                t.start()//Starts the runnable CommandInterpreter thread for user
            }
        } catch (err: Exception) {//In case of an error
            println("Error : $err")
        }
    }
}
