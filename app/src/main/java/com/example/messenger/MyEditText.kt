package com.example.messenger

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.BuildCompat
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
import com.example.messenger.mvvm.ChatAppViewModel

@Suppress("DEPRECATION")
class MyEditText : AppCompatEditText {
    private lateinit var chatAppViewModel: ChatAppViewModel
    var link = ""
    fun getURILink(): String {
        return link
    }
    fun setURILink(newValue: String){
        link = newValue
    }
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(editorInfo)
        val imgTypeString = arrayOf(
            "image/gif",
            "image/jpeg",
            "image/png",
            "image/webp"
        )
        EditorInfoCompat.setContentMimeTypes(
            editorInfo, imgTypeString
        )
        val callback =
            OnCommitContentListener { inputContentInfo, flags, opts ->
                // Check if the content type is a supported image type
                val linkUri = inputContentInfo.linkUri
                val mimeType = inputContentInfo.description.getMimeType(0)

                if (mimeType in imgTypeString) {
                    // Process the image here
                    val linkURI = linkUri?.toString()
                    // Release permission
                    inputContentInfo.releasePermission()

                    if (linkURI != null) {
                        link = linkURI
                        Log.d("LINKURI", "Sending sticker: $link")
                        chatAppViewModel.setStickerUrl(link) // Cập nhật đường dẫn sticker

                    }
                    true // Return true if succeeded
                } else {
                    false // Return false if it's not a supported image type
                }
            }
        return InputConnectionCompat.createWrapper(ic!!, editorInfo, callback)
    }
    fun setChatAppViewModel(viewModel: ChatAppViewModel) {
        chatAppViewModel = viewModel
    }

}
