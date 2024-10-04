@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.data.response

data class DetailResponse(
	val error: Boolean? = null,
	val message: String? = null,
	val event: Event? = null
)

data class Event(
	val summary: String? = null,
	val mediaCover: String? = null,
	val registrants: Int? = null,
	val imageLogo: String? = null,
	val link: String? = null,
	val description: String? = null,
	val ownerName: String? = null,
	val cityName: String? = null,
	val quota: Int? = null,
	val name: String? = null,
	val id: Int? = null,
	val beginTime: String? = null,
	val endTime: String? = null,
	val category: String? = null
)

