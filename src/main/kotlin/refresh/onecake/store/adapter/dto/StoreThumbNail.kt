package refresh.onecake.store.adapter.dto

data class StoreThumbNail(
    var storeId: Long,
    var storeImage: String,
    var guName: String,
    var storeName: String,
    var likedNum: Long,
    var reviewNum: Long,
    var isLiked: Boolean
)
