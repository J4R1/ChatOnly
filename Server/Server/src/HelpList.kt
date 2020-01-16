import java.io.PrintStream
//HelpList IS FOR PRINTING THE AVAILABLE COMMANDS FOR THE USER
class HelpList(val printOut: PrintStream) {
    //Prints out commands and other helpful info
    fun show() {
        printOut.println("Input :exit or :quit for exit")//for exit
        printOut.println("Input :users to see all users in this chat")//user list(active users)
        printOut.println("Input :history to see all chat history")//ENTIRE chat history of the session
    }
}
