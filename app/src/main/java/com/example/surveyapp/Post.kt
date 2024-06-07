package com.example.surveyapp

import kotlinx.serialization.Serializable

@Serializable
data class Survey(
    val SurveyID: Int? = null, // SurveyID opsiyonel hale getirildi
    val SurveyTitle: String,
    val Deadline: String? = null,
    val Time: String? = null,
    val Image: String? = null,  // Base64 encoded image
    val Questions: List<Question> = listOf()  // Sorular eklendi
)

@Serializable
data class Question(
    val SurveyID: Int,
    val QuestionText: String,
    val Options: List<Option> = listOf()  // Seçenekler eklendi
)

@Serializable
data class Option(
    val OptionID: Int? = null,  // OptionID opsiyonel hale getirildi
    val QuestionID: Int,
    val OptionText: String,
    val Votes: Int = 0  // Oy sayısı eklendi
)

@Serializable
data class ResponseMessage(
    val message: String,
    val SurveyID: Int? = null,
    val QuestionID: Int? = null,
    val OptionID: Int? = null
)

@Serializable
data class SurveyResult(
    val QuestionID: Int,
    val QuestionText: String,
    val Options: List<OptionResult>
)

@Serializable
data class OptionResult(
    val OptionID: Int,
    val OptionText: String,
    val Votes: Int
)
