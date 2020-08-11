package com.example.httpchat.ui.messages

interface MessagesContract {

    interface View {
        fun setConversations(conversations: List<String>)
    }

    interface Presenter {
        fun getConversations(nickname: String = "", loadedNum: Int)

        fun dispose()

        fun deleteConversation(userId: String)
    }
}