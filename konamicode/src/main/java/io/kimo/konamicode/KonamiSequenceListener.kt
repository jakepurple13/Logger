package io.kimo.konamicode

/**
 * KonamiSequenceListener
 *
 * Listeners of sequenced actions are implemented with this contract.
 */
interface KonamiSequenceListener {
    fun onSwipeSequenceAchieved(): Boolean
    fun validSwipeSequence(): Boolean
    fun resetSwipeSequence()
    fun onPressedSequenceAchieved(): Boolean
    fun validPressedSequence(): Boolean
    fun resetPressedSequence()
}
