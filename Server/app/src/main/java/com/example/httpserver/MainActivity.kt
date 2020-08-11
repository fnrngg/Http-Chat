package com.example.httpserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.httpserver.beans.UserAndMessageThumbnail
import com.example.httpserver.database.MainDatabase
import com.example.httpserver.database.daos.MessagesDao
import com.example.httpserver.database.daos.UserMappingsDao
import com.example.httpserver.database.daos.UsersDao
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
    private lateinit var messagesDao: MessagesDao
    private lateinit var userMappingsDao: UserMappingsDao

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        mainDatabase = MainDatabase.getInstance(this)
        usersDao = mainDatabase.getUsersDao()
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
            mHttpServer!!.createContext("/allAvailableUsers", availableUsersHandler)


            mHttpServer!!.createContext(
                "/messages/loadConversationThumbnails",
                loadConversationThumbnailsHandler
            )
            mHttpServer!!.createContext("/messages/searchConversation", conversationSearchHandler)

            mHttpServer!!.createContext("/messages/loadConversation", loadConversationHandler)

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
                    val profession = jsonBody["profession"] as String
                    val picture = jsonBody["picture"] as String

                    val userExists = usersDao.checkIfUserExists(name)
                    if (userExists) {
                        val user: User = usersDao.getUserByName(name)
                        sendResponse(exchange, Gson().toJson(user))
                    } else {
                        val newUser = User(0, name, profession, picture)
                        usersDao.insertUser(newUser)
                        sendResponse(exchange, Gson().toJson(newUser))
                    }
                }
            }
        }
    }

    private val availableUsersHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = exchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)

                    val userId = jsonBody["userId"] as Long

                    val userIsRegistered = usersDao.checkIfUserExists(userId)
                    if (userIsRegistered) {
                        val availableUsers = usersDao.getAllAvailableUsers(userId)
                        val responseBody = Gson().toJson(availableUsers)
                        sendResponse(exchange, responseBody)
                    } else {
                        notifyError(exchange, "User Is Registered")
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

                    val userExists = usersDao.checkIfUserExists(userIdFrom)
                    val secondUserExists = usersDao.checkIfUserExists(userIdTo)
                    if (userExists && secondUserExists) {
                        val mappingExists =
                            userMappingsDao.checkIfMappingExists(userIdFrom, userIdTo)
                        if (mappingExists) {
                            val mapping = userMappingsDao.getMapping(userIdFrom, userIdTo)
                            messagesDao.insertMessage(
                                Message(
                                    0,
                                    text,
                                    System.currentTimeMillis(),
                                    userIdFrom,
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
                                    userIdFrom,
                                    newUserMapping.id
                                )
                            )
                        }
                        sendResponse(httpExchange, "Message Successfully Sent")
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }
                }

            }
        }
    }

    private val conversationSearchHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)
                    val userIdOne = jsonBody["userIdOne"] as Long
                    val userNameTwo = jsonBody["userNameTwo"] as String
                    val userOneExists = usersDao.checkIfUserExists(userIdOne)
                    if (userOneExists) {
                        val userTwoExists = usersDao.checkIfUserExists(userNameTwo)
                        if (userTwoExists) {
                            val user = usersDao.getUserByName(userNameTwo)
                            val userAndMessageThumbnail = UserAndMessageThumbnail(user, null)
                            sendResponse(httpExchange, Gson().toJson(userAndMessageThumbnail))
                        } else {
                            notifyError(httpExchange, "User Is Not Registered")
                        }
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }

                }
            }
        }
    }

    private val loadConversationThumbnailsHandler = HttpHandler { httpExchange ->
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
                    val loadIndex = jsonBody["loadIndex"] as Long

                    val loadPosition = 10 * loadIndex
                    val userExists = usersDao.checkIfUserExists(userId)
                    if (userExists) {
                        val resultArray = arrayListOf<UserAndMessageThumbnail>()
                        val userMappings = userMappingsDao.getMappingsForUser(userId)
                        if (loadPosition >= userMappings.size) {
                            notifyError(httpExchange, "Index Out Of Actual Conversations Size")
                        } else {
                            val stopPosition = loadPosition + 10
                            for (position in loadPosition until userMappings.size) {
                                if (position == stopPosition) break
                                val userMapping = userMappings[position.toInt()]
                                var otherUserId: Long = 0
                                if (userMapping.userOne != userId) {
                                    otherUserId = userMapping.userOne
                                } else if (userMapping.userTwo != userId) {
                                    otherUserId = userMapping.userTwo
                                }
                                val user = usersDao.getUserById(otherUserId)
                                val messages =
                                    messagesDao.getConversationThumbnails(userMapping.id)
                                resultArray.add(UserAndMessageThumbnail(user, messages))
                            }
                        }
                        val responseBody = Gson().toJson(resultArray)
                        sendResponse(httpExchange, responseBody)
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }
                }

            }
        }
    }

    private val loadConversationHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)
                    val userIdOne = jsonBody["userIdOne"] as Long
                    val userIdTwo = jsonBody["userIdTwo"] as Long

                    val userOneExists = usersDao.checkIfUserExists(userIdOne)
                    val userTwoExists = usersDao.checkIfUserExists(userIdTwo)
                    if (userOneExists && userTwoExists) {
                        val mappingExists =
                            userMappingsDao.checkIfMappingExists(userIdOne, userIdTwo)
                        if (mappingExists) {
                            val mapping = userMappingsDao.getMapping(userIdOne, userIdTwo)
                            val conversation = messagesDao.getConversationBetween(mapping.id)
                            val responseBody = Gson().toJson(conversation)
                            sendResponse(httpExchange, responseBody)
                        } else {
                            notifyError(httpExchange, "Conversation Does Not Exist")
                        }
                    } else {
                        notifyError(httpExchange, "User Is Not Registered")
                    }
                }

            }
        }
    }
}