package com.example.p2pgeocaching

import com.example.p2pgeocaching.p2pexceptions.StringContainsIllegalCharacterException

class InputValidator {

    // TODO change, so it contains only a-z, A-Z, " ", 0-9

    companion object {
        val illegalCharacters: List<Char> = listOf(';', '{', '}', '"')

        /**
         * Simple function that checks if any of the Strings provided in [arguments] contains an illegal
         * character.
         * If it does, throws a StringContainsIllegalCharacterException().
         */
        fun checkForIllegalCharacters(arguments: List<String>) {
            // Checks for illegal characters in strings
            for (str in arguments) {
                for (illChr in illegalCharacters) {
                    if (str.contains(illChr)) {
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
    }
}