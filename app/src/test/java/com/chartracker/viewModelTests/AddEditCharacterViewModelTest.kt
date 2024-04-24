package com.chartracker.viewModelTests

import android.net.Uri
import com.chartracker.MainDispatcherRule
import com.chartracker.database.CharacterEntity
import com.chartracker.database.MockCharacterDB
import com.chartracker.database.MockImageDB
import com.chartracker.viewmodels.characters.AddEditCharacterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddEditCharacterViewModelTest {
    private val mockCharacterDB = MockCharacterDB()
    private val mockImageDB = MockImageDB()
    private lateinit var viewmodel: AddEditCharacterViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**********************************   ADD   ***************************************************/
    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_init_getCurrentNamesTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_init_getCurrentNamesFailTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacter_duplicateNameTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        viewmodel.submitCharacter(CharacterEntity("Frodo"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.duplicateNameError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacter_imageUploadErrorTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        viewmodel.submitCharacter(CharacterEntity("Sauron"), Uri.parse("badUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacter_imageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity("Sauron")
        viewmodel.submitCharacter(newChar, Uri.parse("fileUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(newChar.imagePublicUrl.value == "downloadUrl")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacterTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity("true")
        viewmodel.submitCharacter(newChar, null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.readyToNavToCharacters.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacterFail_withImageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity("Gimli")
        viewmodel.submitCharacter(newChar, Uri.parse("fileUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacterFail_noImageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity("Gimli")
        viewmodel.submitCharacter(newChar, null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCharacter_submitCharacter_addCharacter_alliesEnemiesNeutralsTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity("true")
        addAllies()
        addEnemies()
        addNeutrals()
        viewmodel.submitCharacter(newChar, null)

        //allows for the internal coroutines to run
        advanceUntilIdle()
        assert(viewmodel.alliesList == listOf("Aragorn", "Borimir", "Sam"))
        assert(viewmodel.enemiesList == listOf("Aragorn", "Borimir", "Sam"))
        assert(viewmodel.neutralList == listOf("Aragorn", "Borimir", "Sam"))
        assert(viewmodel.readyToNavToCharacters.value)
    }

    /********************************   UPDATE   **************************************************/

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_init_getCharacter_IdIssue_Test() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Farimir",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_init_getCharacterTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()
        assert(viewmodel.character.value.name.value == "Frodo")
        assert(viewmodel.character.value.home.value == "Bag End")
        assert(!viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_init_getCharacter_emptyCharacterTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Sauron",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.character.value.name.value == "")
        assert(viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacter_duplicateNameTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Sam")
        viewmodel.submitCharacter(newChar,null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.duplicateNameError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacter_newImageErrorTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Frodo")
        viewmodel.submitCharacter(newChar,Uri.parse("Uri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacter_newImageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Frodo")
        viewmodel.submitCharacter(newChar,Uri.parse("fileUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(newChar.imagePublicUrl.value == "downloadUrl")
        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacter_sameImageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Frodo")
        viewmodel.submitCharacter(newChar,Uri.parse("https://firebasestorage.googleapis.com"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(newChar.imagePublicUrl.value == null)
        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacter_deletingImageTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Sam",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Sam")
        viewmodel.submitCharacter(newChar, null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(newChar.imagePublicUrl.value == null)
        assert(newChar.imageFilename.value == null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacterFailsTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Sam",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Sam")
        viewmodel.submitCharacter(newChar,null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacterTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        val newChar = CharacterEntity(name = "Frodo")
        viewmodel.submitCharacter(newChar,null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.readyToNavToCharacters.value)
    }

    /*******************************   DELETE   ***************************************************/

    @ExperimentalCoroutinesApi
    @Test
    fun updateCharacter_submitCharacterDeleteTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = "Frodo",
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )
        viewmodel.submitCharacterDelete()

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.readyToNavToCharactersDelete.value)
    }

    /*********************************   MISC   ***************************************************/

    @ExperimentalCoroutinesApi
    @Test
    fun alliesUpdatedTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        addAllies()

        assert(viewmodel.alliesList == listOf("Aragorn", "Borimir", "Sam"))
    }

    private fun addAllies(){
        viewmodel.alliesUpdated("Aragorn", true)
        viewmodel.alliesUpdated("Borimir", true)
        viewmodel.alliesUpdated("Borimir", false)
        viewmodel.alliesUpdated("Borimir", true)
        viewmodel.alliesUpdated("Sam", true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun enemiesUpdatedTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        addEnemies()

        assert(viewmodel.enemiesList == listOf("Aragorn", "Borimir", "Sam"))
    }
    private fun addEnemies(){
        viewmodel.enemiesUpdated("Aragorn", true)
        viewmodel.enemiesUpdated("Borimir", true)
        viewmodel.enemiesUpdated("Borimir", false)
        viewmodel.enemiesUpdated("Borimir", true)
        viewmodel.enemiesUpdated("Sam", true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun neutralsUpdatedTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        addNeutrals()

        assert(viewmodel.neutralList == listOf("Aragorn", "Borimir", "Sam"))
    }

    private fun addNeutrals() {
        viewmodel.neutralsUpdated("Aragorn", true)
        viewmodel.neutralsUpdated("Borimir", true)
        viewmodel.neutralsUpdated("Borimir", false)
        viewmodel.neutralsUpdated("Borimir", true)
        viewmodel.neutralsUpdated("Sam", true)
    }
}