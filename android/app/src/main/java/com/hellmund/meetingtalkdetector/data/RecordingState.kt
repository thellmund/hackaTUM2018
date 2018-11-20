package com.hellmund.meetingtalkdetector.data

data class RecordingState(
    val currentTalkingState: List<PersonState>
) {

    override fun toString(): String {
        return currentTalkingState
            .sortedBy { it.id }
            .joinToString("\t") { "Person ${it.id}: ${it.talkingTime}" }
    }

    companion object {

        fun create() = RecordingState(emptyList())

    }

}
