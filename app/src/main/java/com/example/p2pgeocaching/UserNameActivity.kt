package com.example.p2pgeocaching

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.databinding.ActivityUserNameBinding
import com.example.p2pgeocaching.p2pexceptions.StringContainsIllegalCharacterException
import java.io.File


class UserNameActivity : AppCompatActivity() {

    companion object {
        const val U_NAME_FILE = "userName"
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityUserNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "UserNameActivity was opened successfully.")

        binding = ActivityUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the file for the username
        val context = applicationContext
        val userNameFile = File(context.filesDir, MainActivity.U_NAME_FILE)

        // validate input, show error message or return
        binding.userNameButton.setOnClickListener {
            Log.d(TAG, "Button was pressed")
            handleInput(userNameFile)
        }

        // close keyboard when enter is pressed
        binding.userNameEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }

    /**
     * This function looks if the entered text is legal.
     * If it is, it saves it and writes to the file.
     * If it is not, displays error message.
     */
    private fun handleInput(file: File) {
        Log.d(TAG, "handleInput() was called")
        val userNameString: String = binding.userNameEditText.text.toString()
        Log.d(TAG, "usernameString is: $userNameString")

        // Username is empty
        if (userNameString == "") {
            Log.d(TAG, "Empty username detected")
            binding.errorText.text = getString(R.string.warning_empty_user_name)

        } else { // Username is not empty
            var hasIllegalCharacters = false
            try {
                InputValidator.checkForIllegalCharacters(userNameString)
            } catch (e: StringContainsIllegalCharacterException) {
                Log.d(TAG, "String contains illegal characters")

                // Contains illegal characters
                hasIllegalCharacters = true
                val text = getString(R.string.warning_illegal_characters_user_name)
                val illegalCharString = InputValidator.illegalCharacters.joinToString()
                binding.errorText.text =
                    getString(R.string.warning_illegal_characters_user_name, illegalCharString)
            }

            // Everything is correct, write to file, go back
            if (!hasIllegalCharacters) {
                Log.d(TAG, "User name was accepted")
                file.delete()
                Log.d(TAG, "Original file was deleted")
                file.writeText(userNameString)
                Log.d(TAG, "Written to file: $userNameString")
                /*
                val inputStream: InputStream = File(U_NAME_FILE).inputStream()
                Log.d(
                    TAG,
                    "File currently contains: " +
                            inputStream.bufferedReader().use { it.readText() }.toString()
                )
                */
                binding.errorText.text = ""

                // Go back to original screen
                /*
                val context = applicationContext
                var intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                */
                finish()
            }
        }
    }

    /**
     * Closes keyboard when enter is pressed
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }

}
