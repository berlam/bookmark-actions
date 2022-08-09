package net.berla.intellij.bookmarkactions

import com.intellij.ide.bookmarks.BookmarkManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GotoGlobalBookmarkActionTest : BasePlatformTestCase() {

    private lateinit var files: Array<PsiFile>

    /**
     * @return path to test data file directory relative to working directory in the run configuration for this test.
     */
    override fun getTestDataPath(): String {
        return "src/test/resources"
    }

    override fun setUp() {
        super.setUp()

        files = myFixture.configureByFiles("first.txt", "second.txt")

        val bm = BookmarkManager.getInstance(project)
        bm.applyNewStateInTestMode(emptyList())
        bm.addTextBookmark(files[0].virtualFile, 0, "First line index")
        bm.addTextBookmark(files[1].virtualFile, 0, "Second line index")
    }

    @Test
    fun testNext() {
        val action = "GotoNextGlobalBookmark"

        myFixture.performEditorAction(action)
        assertEquals(files[1].virtualFile, currentFile())
        assertEquals(0, getLine())
    }

    private fun getLine() = myFixture.editor.caretModel.logicalPosition.line

    private fun currentFile() = FileEditorManager.getInstance(project).selectedFiles[0]
}
