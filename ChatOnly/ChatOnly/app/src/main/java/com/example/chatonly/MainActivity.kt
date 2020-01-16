package com.example.chatonly
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import android.widget.ScrollView
import kotlinx.android.synthetic.main.alert_dialog.view.*
import java.net.Socket
import java.time.LocalTime
import android.widget.TextView
import java.io.PrintStream
import java.net.InetAddress
import java.util.*
class MainActivity : AppCompatActivity(), View.OnClickListener {//Creates the visible side of the app
    lateinit var linearlayout: LinearLayout
    lateinit var messageField: EditText
    lateinit var buttonSend: Button
    lateinit var textView: TextView
    var userName = " "
    lateinit var s:Socket//used to connect to socket
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dialogBuilder = AlertDialog.Builder(this@MainActivity)//fullscreen alertdialog to get username
        dialogBuilder.setTitle("Set username")
        val view = layoutInflater.inflate(R.layout.alert_dialog, null)
        dialogBuilder.setView(view)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        view.buttonClick.setOnClickListener {
            var name = view.editTextName.text.toString()//takes in text input as string
            val rangepoisto = name
            val specialRemoval = Regex("[^0-9A-Za-z]")//^Removes all but the markings inside []
            val spaceRemoval = specialRemoval.replace(rangepoisto, "")
            name = spaceRemoval//username input with special characters removed
            if (name.length > 0 && name.length <13) {//check to have acceptable length username
                if (Users.addUsername(name)) {//Adds name to be component in message displaying
                    userName = name//given name that is sent to socket
                    Thread(Runnable {//CONNECTS TO SOCKET
                        s=Socket(InetAddress.getByName("10.0.2.2"), 52886)
                        val inp= s.getInputStream()
                        val out = PrintStream(s.getOutputStream(), true)
                        out.println(":user $userName")//automatically gives chosen username, if name is taken in server,
                        // user must choose another name for socket to connect, but will still displays chosen name in client
                    }).start()
                } else {
                }
                Toast.makeText(this@MainActivity, name, Toast.LENGTH_SHORT).show()//shows briefly accepted name
                alertDialog.dismiss()
                Thread(Runnable {//starts loop to listen inputstream for displaying messages from socket
                    val inp = s.getInputStream()
                    val scanner: Scanner = Scanner(inp)
                    while (true) {
                        var input = scanner.nextLine().toString()
                        println("$input")
                        TextMessage.removeOutsideMessage()//removes potential messages to avoid duplicates
                        TextMessage.addOutsideMessage(input)
                        send(input,false)//sends input as message, false as color code for the message in Android client
                    }
                }).start()
            }else {//Message briefly displayed for the user to inform the given username wasn't valid
                Toast.makeText(this@MainActivity, "Username must be between 1-12 characters", Toast.LENGTH_SHORT).show()
            }
        }
        linearlayout = findViewById(R.id.linearLayout)
        buttonSend = findViewById(R.id.button)
        messageField = findViewById(R.id.editText)
        textView = findViewById(R.id.textView)
        buttonSend.setOnClickListener(this)
        val sv = findViewById(R.id.autoScroll) as ScrollView
        sv.scrollTo(0, sv.bottom)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {//SEND BUTTON ACTIVATED
        val message = messageField.text //takes text from input
        val userlist= Users.getUserList().toString() //brings the given username
        val rangepoisto = userlist  //Stores userlist as 'var', so it can be modified later
        val stringpoisto = Regex("[^0-9A-Za-z]")//^Removes all but the markings inside []
        val nimi = stringpoisto.replace(rangepoisto, "")
        val time = LocalTime.now()
        val timeHours = time.hour+2          //Displays hours +2 for correct time zone
        val timeMinutes = time.minute      //Displays minutes
        val timeSeconds = time.second      //Displays seconds
        val oma = "$nimi: $message @$timeHours:$timeMinutes:$timeSeconds" //message parts for android client
        val messageOut = "$message"//The message that is sent out to socket
        TextMessage.addMessage(oma)//message is compiled for the client in TextMessage.kt
        var bracketsOff = TextMessage.getTheMessage().toString().replace("[", "").replace("]", "")//removes brackets
        send(bracketsOff,true)//sends bracketsOff as message, true as color code for the message in Android client
        //send(TextMessage.getTheMessage().toString(),true)
        Thread(Runnable {//New thread is created on click and send to socket
            val out = PrintStream(s.getOutputStream(), true)
            out.println("$messageOut")//send message to socket
        }).start()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun send(message:String,source:Boolean){//takes in message, and source(from socket or client) for color coding
        val inp = s.getInputStream()
        val scanner: Scanner = Scanner(inp)
        val sv = findViewById(R.id.autoScroll) as ScrollView
        val textView = TextView(this)
        val params = LinearLayout.LayoutParams(//params for textview, new message creates new textview in linearlayout
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = params
        textView.setTextColor(Color.BLACK)
        textView.setPadding(20,15,20,15)
        textView.width
        if (source==true){//checks source and colors message accordingly for the android client
            textView.setBackgroundResource(R.drawable.rounded_corner)//from the phone
        }else{
            textView.setBackgroundResource(R.drawable.rounded_corners2)//from socket
        }
        var outside=TextMessage.getTheOutsideMessage().toString()
        var inside = TextMessage.getTheMessage().toString()
        Thread(Runnable {//creates the android client message and new textview to display it
            runOnUiThread {
                textView.text = message
                linearlayout.addView(textView)//adds text view
                TextMessage.removeMessage()
            }
        }).start()
        sv.scrollTo(0, sv.bottom)//scrolls messages down to better see new messages
        sv.fullScroll(ScrollView.FOCUS_DOWN)
        messageField.text.clear() //clears text input field
    }
}
