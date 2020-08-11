package com.example.httpserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.httpserver.beans.UserAndMessageThumbnail
import com.example.httpserver.database.MainDatabase
import com.example.httpserver.database.daos.ConversationVisibilityDao
import com.example.httpserver.database.daos.MessagesDao
import com.example.httpserver.database.daos.UserMappingsDao
import com.example.httpserver.database.daos.UsersDao
import com.example.httpserver.database.entities.ConversationVisibility
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
    private lateinit var conversationVisibilityDao: ConversationVisibilityDao

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        mainDatabase = MainDatabase.getInstance(this)
        usersDao = mainDatabase.getUsersDao()
        messagesDao = mainDatabase.getMessagesDao()
        userMappingsDao = mainDatabase.getUserMappingsDao()
        conversationVisibilityDao = mainDatabase.getConversationVisibilityDao()

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

            mHttpServer!!.createContext(
                "/messages/deleteConversation",
                deleteConversationHandler
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

                    val name = jsonBody.getString("name")
                    val profession = jsonBody.getString("profession")
                    val picture = jsonBody.getString("picture")

                    val userExists = usersDao.checkIfUserExists(name)
                    if (userExists) {
                        val user: User = usersDao.getUserByName(name)
                        sendResponse(exchange, Gson().toJson(user))
                    } else {
                        val newUser = User(0, name, profession, picture)
                        usersDao.insertUser(newUser)
                        val user = usersDao.getUserByName(name)
                        sendResponse(exchange, Gson().toJson(user))
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

                    val userId = jsonBody.getLong("userId")

                    val userIsRegistered = usersDao.checkIfUserExists(userId)
                    if (userIsRegistered) {
                        val availableUsers = usersDao.getAllAvailableUsers(userId)
                        val responseBody = Gson().toJson(availableUsers)
                        sendResponse(exchange, responseBody)
                    } else {
                        notifyError(exchange, "User Is Not Registered")
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

                    val userIdFrom = jsonBody.getLong("from")
                    val userIdTo = jsonBody.getLong("to")
                    val text = jsonBody.getString("text")

                    val userExists = usersDao.checkIfUserExists(userIdFrom)
                    val secondUserExists = usersDao.checkIfUserExists(userIdTo)
                    if (userExists && secondUserExists) {
                        val mappingExists =
                            userMappingsDao.checkIfMappingExists(userIdFrom, userIdTo)
                        if (mappingExists) {
                            val mapping = userMappingsDao.getMapping(userIdFrom, userIdTo)
                            val conversationIsVisible =
                                conversationVisibilityDao.conversationIsVisibleFor(
                                    userIdFrom,
                                    mapping.id
                                )
                            if (!conversationIsVisible) {
                                conversationVisibilityDao.insertConversationVisibility(
                                    ConversationVisibility(
                                        0,
                                        mapping.id,
                                        userIdFrom,
                                        System.currentTimeMillis()
                                    )
                                )
                            }
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
                            val userMapping = userMappingsDao.getMapping(userIdFrom, userIdTo)
                            val timeMillis = System.currentTimeMillis()
                            conversationVisibilityDao.insertConversationVisibility(
                                ConversationVisibility(
                                    0,
                                    userMapping.id,
                                    userMapping.userOne,
                                    timeMillis
                                )
                            )
                            conversationVisibilityDao.insertConversationVisibility(
                                ConversationVisibility(
                                    0,
                                    userMapping.id,
                                    userMapping.userTwo,
                                    timeMillis
                                )
                            )
                            messagesDao.insertMessage(
                                Message(
                                    0,
                                    text,
                                    System.currentTimeMillis(),
                                    userIdFrom,
                                    userMapping.id
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
                    val userIdOne = jsonBody.getLong("userIdOne")
                    val userNameTwo = jsonBody.getString("userNameTwo")
                    val userOneExists = usersDao.checkIfUserExists(userIdOne)
                    if (userOneExists) {
                        val availableUsers =
                            usersDao.getAllAvailableAlikeUsers(userIdOne, "%$userNameTwo%")
                        val resultArray = arrayListOf<UserAndMessageThumbnail>()
                        for (availableUser in availableUsers) {
                            val userMappingExists =
                                userMappingsDao.checkIfMappingExists(userIdOne, availableUser.id)
                            if (userMappingExists) {
                                val mapping =
                                    userMappingsDao.getMapping(userIdOne, availableUser.id)
                                val conversationIsVisible =
                                    conversationVisibilityDao.conversationIsVisibleFor(
                                        userIdOne,
                                        mapping.id
                                    )
                                if (conversationIsVisible) {
                                    val thumbnailMessage =
                                        messagesDao.getConversationThumbnail(mapping.id)
                                    val conversationVisibility =
                                        conversationVisibilityDao.getConversationVisibility(
                                            userIdOne,
                                            mapping.id
                                        )
                                    if (thumbnailMessage.dateMillis >= conversationVisibility.dateMillis) {
                                        val userAndMessageThumbnail =
                                            UserAndMessageThumbnail(availableUser, thumbnailMessage)
                                        resultArray.add(userAndMessageThumbnail)
                                    } else {
                                        val userAndMessageThumbnail =
                                            UserAndMessageThumbnail(availableUser, null)
                                        resultArray.add(userAndMessageThumbnail)
                                    }
                                } else {
                                    val userAndMessageThumbnail =
                                        UserAndMessageThumbnail(availableUser, null)
                                    resultArray.add(userAndMessageThumbnail)
                                }
                            } else {
                                val userAndMessageThumbnail =
                                    UserAndMessageThumbnail(availableUser, null)
                                resultArray.add(userAndMessageThumbnail)
                            }
                        }
                        sendResponse(httpExchange, Gson().toJson(resultArray))
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
                    val userId = jsonBody.getLong("userId")
                    val loadIndex = jsonBody.getLong("loadIndex")

                    val loadPosition = 10 * loadIndex
                    val userExists = usersDao.checkIfUserExists(userId)
                    if (userExists) {
                        val resultArray = arrayListOf<UserAndMessageThumbnail>()
                        val userMappings = userMappingsDao.getMappingsForUser(userId)
                        if (loadPosition >= userMappings.size) {
                            notifyError(httpExchange, "Index Out Of Actual Conversations Size")
                        } else {
                            var stopPosition = loadPosition + 10
                            for (position in loadPosition until userMappings.size) {
                                if (position == stopPosition) break
                                val userMapping = userMappings[position.toInt()]
                                val conversationVisibilityExists =
                                    conversationVisibilityDao.conversationIsVisibleFor(
                                        userId,
                                        userMapping.id
                                    )
                                if (conversationVisibilityExists) {
                                    var otherUserId: Long = 0
                                    if (userMapping.userOne != userId) {
                                        otherUserId = userMapping.userOne
                                    } else if (userMapping.userTwo != userId) {
                                        otherUserId = userMapping.userTwo
                                    }
                                    val user = usersDao.getUserById(otherUserId)
                                    val message =
                                        messagesDao.getConversationThumbnail(userMapping.id)
                                    val conversationVisibility =
                                        conversationVisibilityDao.getConversationVisibility(
                                            userId,
                                            userMapping.id
                                        )
                                    if (message.dateMillis >= conversationVisibility.dateMillis) {
                                        resultArray.add(UserAndMessageThumbnail(user, message))
                                    } else {
                                        resultArray.add(UserAndMessageThumbnail(user, null))
                                    }
                                } else {
                                    stopPosition += 1
                                }
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
                    val userIdOne = jsonBody.getLong("userIdOne")
                    val userIdTwo = jsonBody.getLong("userIdTwo")

                    val userOneExists = usersDao.checkIfUserExists(userIdOne)
                    val userTwoExists = usersDao.checkIfUserExists(userIdTwo)
                    if (userOneExists && userTwoExists) {
                        val mappingExists =
                            userMappingsDao.checkIfMappingExists(userIdOne, userIdTwo)
                        if (mappingExists) {
                            val mapping = userMappingsDao.getMapping(userIdOne, userIdTwo)
                            val conversationIsVisible =
                                conversationVisibilityDao.conversationIsVisibleFor(
                                    userIdOne,
                                    mapping.id
                                )
                            if (conversationIsVisible) {
                                val conversationVisibility =
                                    conversationVisibilityDao.getConversationVisibility(
                                        userIdOne,
                                        mapping.id
                                    )
                                val conversation: List<Message> =
                                    messagesDao.getConversationBetween(mapping.id)
                                val filteredConversation = arrayListOf<Message>()
                                for (message in conversation) {
                                    if (message.dateMillis >= conversationVisibility.dateMillis) {
                                        filteredConversation.add(message)
                                    }
                                }
                                if (filteredConversation.size == 0) {
                                    notifyError(httpExchange, "Conversation Does Not Exist")
                                } else {
                                    val responseBody = Gson().toJson(filteredConversation)
                                    sendResponse(httpExchange, responseBody)
                                }
                            } else {
                                notifyError(httpExchange, "Conversation Does Not Exist")
                            }
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

    private val deleteConversationHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)
                    val userId = jsonBody.getLong("userId")
                    val userMappingId = jsonBody.getLong("userMappingId")

                    val userExists = usersDao.checkIfUserExists(userId)
                    val visibilityQuantity =
                        conversationVisibilityDao.conversationVisibilitiesQuantityFor(userMappingId)
                    if (userExists) {
                        if (visibilityQuantity > 0) {
                            if (visibilityQuantity > 1) {
                                conversationVisibilityDao.deleteConversationFor(
                                    userId,
                                    userMappingId
                                )
                            } else {
                                conversationVisibilityDao.deleteConversationFor(
                                    userId,
                                    userMappingId
                                )
                                messagesDao.deleteMessagesByMappingId(userMappingId)
                                userMappingsDao.deleteMappingById(userMappingId)
                            }
                            sendResponse(httpExchange, "Successfully Deleted")
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