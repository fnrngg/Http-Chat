package com.example.httpchat.ui.conversation

import com.example.httpchat.models.responses.Message

interface ConversationContract {
    interface View {
        fun setConversation(conversation: List<Message>)

    }

    interface Presenter {
        fun getConversation(userId: String)

        fun dispose()

        fun sendMessage(message: String)
    }
}