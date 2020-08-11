package com.example.httpchat.ui.conversation

interface ConversationContract {
    interface View {
        fun setConversation(conversation: List<String>?)

    }

    interface Presenter {
        fun getConversation(userId: String)

        fun dispose()

        fun sendMessage(message: String)
    }
}