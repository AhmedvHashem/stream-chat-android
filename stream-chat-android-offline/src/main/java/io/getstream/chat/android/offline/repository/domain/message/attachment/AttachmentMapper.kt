package io.getstream.chat.android.offline.repository.domain.message.attachment

import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_FAILED
import io.getstream.chat.android.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_IN_PROGRESS
import io.getstream.chat.android.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_SUCCESS
import java.io.File

internal fun Attachment.toEntity(messageId: String, index: Int): AttachmentEntity = AttachmentEntity(
    id = getOrGenerateId(messageId, index),
    messageId = messageId,
    authorName = authorName,
    titleLink = titleLink,
    authorLink = authorLink,
    thumbUrl = thumbUrl,
    imageUrl = imageUrl,
    assetUrl = assetUrl,
    ogUrl = ogUrl,
    mimeType = mimeType,
    fileSize = fileSize,
    title = title,
    text = text,
    type = type,
    image = image,
    url = url,
    name = name,
    fallback = fallback,
    uploadFilePath = upload?.absolutePath,
    uploadState = uploadState?.toEntity(),
    extraData = extraData,
)

internal fun AttachmentEntity.toModel(): Attachment = Attachment(
    authorName = authorName,
    titleLink = titleLink,
    authorLink = authorLink,
    thumbUrl = thumbUrl,
    imageUrl = imageUrl,
    assetUrl = assetUrl,
    ogUrl = ogUrl,
    mimeType = mimeType,
    fileSize = fileSize,
    title = title,
    text = text,
    type = type,
    image = image,
    url = url,
    name = name,
    fallback = fallback,
    upload = uploadFilePath?.let(::File),
    uploadState = uploadState?.toModel(uploadFilePath?.let(::File)),
    extraData = extraData.toMutableMap(),
)

private fun Attachment.UploadState.toEntity(): UploadStateEntity {
    val (statusCode, errorMessage) = when (this) {
        Attachment.UploadState.Success -> UPLOAD_STATE_SUCCESS to null
        Attachment.UploadState.Idle -> UPLOAD_STATE_IN_PROGRESS to null
        is Attachment.UploadState.InProgress -> UPLOAD_STATE_IN_PROGRESS to null
        is Attachment.UploadState.Failed -> UPLOAD_STATE_FAILED to (
            this.error.message
                ?: this.error.cause?.localizedMessage
            )
    }
    return UploadStateEntity(statusCode, errorMessage)
}

private fun UploadStateEntity.toModel(uploadFile: File?): Attachment.UploadState = when (this.statusCode) {
    UPLOAD_STATE_SUCCESS -> Attachment.UploadState.Success
    UPLOAD_STATE_IN_PROGRESS -> Attachment.UploadState.InProgress(0, uploadFile?.length() ?: 0)
    UPLOAD_STATE_FAILED -> Attachment.UploadState.Failed(ChatError(message = this.errorMessage))
    else -> error("Integer value of $statusCode can't be mapped to UploadState")
}

private fun Attachment.getOrGenerateId(messageId: String, index: Int): String {
    return if (extraData.containsKey(AttachmentEntity.EXTRA_DATA_ID_KEY)) {
        extraData[AttachmentEntity.EXTRA_DATA_ID_KEY] as String
    } else {
        AttachmentEntity.generateId(messageId, index).also { id ->
            extraData[AttachmentEntity.EXTRA_DATA_ID_KEY] = id
        }
    }
}
