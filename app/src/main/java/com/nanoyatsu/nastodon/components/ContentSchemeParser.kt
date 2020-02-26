package com.nanoyatsu.nastodon.components

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

/**
 * content:// スキームのURIをパースする
 * 参考 ： https://stackoverflow.com/questions/32661221/android-cursor-didnt-have-data-column-not-found/33930169#33930169
 * todo deprecated対応
 */
object ContentSchemeParser {
    private val TAG = this.javaClass.simpleName
    fun getPathFromUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        Log.e(TAG, "uri:" + uri.authority)
        if (DocumentsContract.isDocumentUri(context, uri)) {
            when (uri.authority) {
                "com.android.externalstorage.documents" -> { // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    return if ("primary".equals(type, ignoreCase = true)) {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    } else {
                        "/storage/" + type + "/" + split[1]
                    }
                }
                "com.android.providers.downloads.documents" -> { // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                "com.android.providers.media.documents" -> { // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    // val type = split[0]
                    val contentUri: Uri = MediaStore.Files.getContentUri("external")
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { //MediaStore
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): String? {
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver
                .query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val cIndex: Int = cursor.getColumnIndexOrThrow(projection[0])
                return cursor.getString(cIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}