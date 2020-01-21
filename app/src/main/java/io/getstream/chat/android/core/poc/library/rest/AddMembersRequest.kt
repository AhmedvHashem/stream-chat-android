package io.getstream.chat.android.core.poc.library.rest

import com.google.gson.annotations.SerializedName


class AddMembersRequest(
    @SerializedName("add_members")
    val members: List<String>
)
