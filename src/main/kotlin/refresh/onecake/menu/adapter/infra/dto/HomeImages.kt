package refresh.onecake.menu.adapter.infra.dto

import refresh.onecake.menu.domain.Keyword

data class HomeImages(
    val image: String,
    val storeId: Long,
    val menuId: Long,
    val imageId: Long
)

data class KeywordImages(
    val image: String,
    val storeId: Long,
    val menuId: Long,
    val imageId: Long,
    val keyword: Keyword
)

data class NeighborhoodStore (
    val storeImage: String = "",
    val id: Long = -1L
)
