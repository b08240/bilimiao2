package com.a10miaomiao.bilimiao.entity

data class SeasonEpisode(
        val cover: String,
        val episodes: List<Episode>,
        val evaluate: String,
        val link: String,
        val media_id: Int,
        val mode: Int,
        val new_ep: NewEp,
        val paster: Paster,
        val payment: Payment,
        val publish: Publish,
        val rating: Rating,
        val record: String,
        val rights: RightsX,
        val season_id: Int,
        val season_title: String,
        val seasons: List<Season>,
        val section: List<Any>,
        val series: Series,
        val share_url: String,
        val square_cover: String,
        val stat: StatX,
        val status: Int,
        val title: String,
        val total: Int,
        val type: Int,
        val user_status: UserStatus
)