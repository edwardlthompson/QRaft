package org.qraft.app.gallery

/** System Back closes an open gallery card before tab pop. */
object GalleryNav {
    fun consumeBack(openId: String?): Boolean = !openId.isNullOrEmpty()
}
