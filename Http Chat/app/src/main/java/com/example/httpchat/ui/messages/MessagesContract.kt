package com.example.httpchat.ui.messages

import com.example.httpchat.models.responses.UserAndMessageThumbnail

interface MessagesContract {

    interface View {
        fun setConversations(conversations: List<UserAndMessageThumbnail>)
    }

    interface Presenter {
        fun getConversations(nickname: String = "", loadedNum: Int)

        fun dispose()

        fun deleteConversation(userMappingId: Long)
    }
}