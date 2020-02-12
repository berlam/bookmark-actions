package net.berla.intellij.bookmarkactions

import com.intellij.ide.bookmarks.Bookmark
import com.intellij.ide.bookmarks.BookmarkManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.fileEditor.FileDocumentManager

/**
 * @see com.intellij.ide.bookmarks.actions.GotoBookmarkActionBase
 */
abstract class GotoGlobalBookmarkActionBase protected constructor(next: Boolean) :
    EditorAction(object : EditorActionHandler() {
        override fun doExecute(
            editor: Editor,
            caret: Caret?,
            dataContext: DataContext
        ) {
            navigateToBookmark(dataContext, editor)
        }

        override fun isEnabledForCaret(
            editor: Editor,
            caret: Caret,
            dataContext: DataContext
        ): Boolean {
            return getBookmarkToGo(dataContext, editor) != null
        }

        private fun navigateToBookmark(dataContext: DataContext, editor: Editor) {
            val bookmark = getBookmarkToGo(dataContext, editor)
            if (bookmark == null || !bookmark.isValid) {
                return
            }
            bookmark.navigate(true)
        }

        private fun getBookmarkToGo(dataContext: DataContext, editor: Editor): Bookmark? {
            val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return null
            val bookmarkManager = BookmarkManager.getInstance(project)
            val validBookmarks = bookmarkManager.validBookmarks
            val size = validBookmarks.size
            if (size == 0) {
                return null
            }
            val currentLine = editor.caretModel.logicalPosition.line
            for (i in size - 1 downTo 0) {
                val validBookmark = validBookmarks[i]
                val document = FileDocumentManager.getInstance().getCachedDocument(validBookmark.file)
                if (document != null && document == editor.document && validBookmark.line == currentLine) {
                    return if (next) {
                        validBookmarks[if (i + 1 > size - 1) 0 else i + 1]
                    } else {
                        validBookmarks[if (i - 1 < 0) size - 1 else i - 1]
                    }
                }
            }
            return validBookmarks[size - 1]
        }
    })
