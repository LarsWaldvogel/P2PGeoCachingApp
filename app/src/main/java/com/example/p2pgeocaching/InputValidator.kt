package com.example.p2pgeocaching

import com.example.p2pgeocaching.p2pexceptions.StringContainsIllegalCharacterException

class InputValidator {
    
    companion object {
        private val legalCharacters: List<Char> = generateLegalCharacters()


        /**
         * Simple function that checks if any of the Strings provided in [arguments] contains an illegal
         * character.
         * If it does, throws a StringContainsIllegalCharacterException().
         */
        fun checkForIllegalCharacters(arguments: List<String>) {
            // Checks for illegal characters in strings
            for (str in arguments) {
                for (char in str) {
                    if (char !in legalCharacters) {
                        throw StringContainsIllegalCharacterException()
                    }
                }
            }
        }


        /**
         * If only a single string is provided, casts it to array list and calls original function.
         */
        fun checkForIllegalCharacters(argument: String) {
            checkForIllegalCharacters(arrayListOf(argument))
        }


        /**
         * This function generates a list of all the legal characters and returns it.
         * The legal characters are the letters a-z, A-Z and numbers 0-9.
         */
        fun generateLegalCharacters(): List<Char> {
            // Create list with space character ' ' in it
            val listOfChars = mutableListOf(' ')

            // Add characters a-z
            var char = 'a'
            while (char <= 'z') {
                listOfChars.add(char)
                char++
            }

            // Add characters A-Z
            char = 'A'
            while (char <= 'Z') {
                listOfChars.add(char)
                char++
            }

            // Add numbers 0-9
            char = '0'
            while (char <= '9') {
                listOfChars.add(char)
                char++
            }

            // Return result
            return listOfChars
        }
    }
}