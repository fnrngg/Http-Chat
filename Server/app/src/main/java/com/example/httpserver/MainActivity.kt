package com.example.httpserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.httpserver.beans.UserAndMessages
import com.example.httpserver.database.MainDatabase
import com.example.httpserver.database.daos.ActiveUsersDao
import com.example.httpserver.database.daos.MessagesDao
import com.example.httpserver.database.daos.UserMappingsDao
import com.example.httpserver.database.daos.UsersDao
import com.example.httpserver.database.entities.ActiveUser
import com.example.httpserver.database.entities.Message
import com.example.httpserver.database.entities.User
import com.example.httpserver.database.entities.UserMapping
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.android.synthetic.main.main_layout.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private var serverUp = false
    private var mHttpServer: HttpServer? = null
    private lateinit var mainDatabase: MainDatabase
    private lateinit var usersDao: UsersDao
    private lateinit var activeUsersDao: ActiveUsersDao
    private lateinit var messagesDao: MessagesDao
    private lateinit var userMappingsDao: UserMappingsDao

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        mainDatabase = MainDatabase.getInstance(this)
        usersDao = mainDatabase.getUsersDao()
        activeUsersDao = mainDatabase.getActiveUsersDao()
        messagesDao = mainDatabase.getMessagesDao()
        userMappingsDao = mainDatabase.getUserMappingsDao()

        val port = resources.getInteger(R.integer.default_port)

        serverButton.setOnClickListener {
            serverUp = if (!serverUp) {
                startServer(port)
                true
            } else {
                stopServer()
                false
            }
        }
    }

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun sendResponse(httpExchange: HttpExchange, responseText: String) {
        httpExchange.sendResponseHeaders(200, responseText.length.toLong())
        val os = httpExchange.responseBody
        os.write(responseText.toByteArray())
        os.close()
    }

    private fun notifyError(httpExchange: HttpExchange, responseText: String) {
        httpExchange.sendResponseHeaders(404, responseText.length.toLong())
        val os = httpExchange.responseBody
        os.write(responseText.toByteArray())
        os.close()
    }

    private fun startServer(port: Int) {
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()

            mHttpServer!!.createContext("/", rootHandler)
            mHttpServer!!.createContext("/index", rootHandler)

            //login
            mHttpServer!!.createContext("/login", loginHandler)

            //activeUsers
            mHttpServer!!.createContext("/activeUsers", activeUsersHandler)

            // Handle /messages endpoint
            mHttpServer!!.createContext("/messages/loadMessages", loadMessagesHandler)

            mHttpServer!!.createContext("/messages/sendMessage", sendMessageHandler)

            mHttpServer!!.start()
            serverTextView.text = getString(R.string.server_up)
            serverButton.text = getString(R.string.stop_server)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopServer() {
        if (mHttpServer != null) {
            mHttpServer!!.stop(0)
            serverTextView.text = getString(R.string.server_down)
            serverButton.text = getString(R.string.start_server)
        }
    }

    // Handler for root endpoint
    private val rootHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    sendResponse(exchange, "Welcome to my server")
                }
            }
        }
    }

    private val loginHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = exchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)

                    val name = jsonBody["name"] as String
                    val password = jsonBody["password"] as String

                    val userExists = usersDao.checkIfUserExists(name)
                    if (userExists) {
                        val user: User = usersDao.getUserByName(name)
                        if (user.password == password) {
                            activeUsersDao.insertActiveUser(ActiveUser(0, user.id))
                            sendResponse(exchange, "Logged In Successfully")
                        } else {
                            notifyError(exchange, "Incorrect Password")
                        }
                    } else {
                        val newUser = User(0, name, password)
                        usersDao.insertUser(newUser)
                        activeUsersDao.insertActiveUser(ActiveUser(0, newUser.id))
                        sendResponse(exchange, "Logged In Successfully")
                    }
                }
            }
        }
    }

    private val activeUsersHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = exchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)

                    val userId = jsonBody["userId"] as Long

                    val userIsLoggedIn = activeUsersDao.checkIfUserIsLoggedIn(userId)
                    if (userIsLoggedIn) {
                        val activeUsers = activeUsersDao.getActiveUsersFor(userId)
                        val responseBody = Gson().toJson(activeUsers)
                        sendResponse(exchange, responseBody)
                    } else {
                        notifyError(exchange, "User Is Not Logged In Somehow")
                    }
                }
            }
        }
    }

    private val sendMessageHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)

                    val userIdFrom = jsonBody["from"] as Long
                    val userIdTo = jsonBody["to"] as Long
                    val text = jsonBody["text"] as String

                    val userIsLoggedIn = activeUsersDao.checkIfUserIsLoggedIn(userIdFrom)
                    val userExists = usersDao.checkIfUserExists(userIdFrom)
                    if (userExists) {
                        if (userIsLoggedIn) {
                            val secondUserExists = usersDao.checkIfUserExists(userIdTo)
                            if (secondUserExists) {
                                val mappingExists =
                                    userMappingsDao.checkIfMappingExists(userIdFrom, userIdTo)
                                if (mappingExists) {
                                    val mapping = userMappingsDao.getMapping(userIdFrom, userIdTo)
                                    messagesDao.insertMessage(
                                        Message(
                                            0,
                                            text,
                                            System.currentTimeMillis(),
                                            mapping.id
                                        )
                                    )
                                } else {
                                    val newUserMapping = UserMapping(0, userIdFrom, userIdTo)
                                    userMappingsDao.insertUserMapping(newUserMapping)
                                    messagesDao.insertMessage(
                                        Message(
                                            0,
                                            text,
                                            System.currentTimeMillis(),
                                            newUserMapping.id
                                        )
                                    )
                                }
                                sendResponse(httpExchange, "Message Successfully Sent")
                            } else {
                                notifyError(
                                    httpExchange,
                                    "Message Recipient User Is Not Registered"
                                )
                            }
                        } else {
                            notifyError(httpExchange, "User Is Not Logged In")
                        }
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }
                }

            }
        }
    }

    private val loadMessagesHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "GET" -> {
                    val messages = messagesDao.getAllMessages()
                    val responseBody = Gson().toJson(messages)
                    sendResponse(httpExchange, responseBody)
                }
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)
                    val userId = jsonBody["userId"] as Long

                    val userExists = usersDao.checkIfUserExists(userId)
                    if (userExists) {
                        val userIsLoggedIn = activeUsersDao.checkIfUserIsLoggedIn(userId)
                        if (userIsLoggedIn) {
                            val resultArray = arrayListOf<UserAndMessages>()
                            val userMappings = userMappingsDao.getMappingsForUser(userId)
                            for (userMapping in userMappings) {
                                var otherUserId: Long = 0
                                if (userMapping.userOne != userId) {
                                    otherUserId = userMapping.userOne
                                } else if (userMapping.userTwo != userId) {
                                    otherUserId = userMapping.userTwo
                                }
                                val user = usersDao.getUserById(otherUserId)
                                val messages = messagesDao.getConversationBetween(userMapping.id)
                                resultArray.add(UserAndMessages(user, messages))
                            }
                            val responseBody = Gson().toJson(resultArray)
                            sendResponse(httpExchange, responseBody)
                        } else {
                            notifyError(httpExchange, "User Is Not Logged In")
                        }
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }
                }

            }
        }
    }
}