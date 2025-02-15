package refresh.onecake.menu.adapter.infra.dto

data class StoredMenuForm(
    var cakeSize: String,
    var image: String,
    var price: Int,
    var menuDescription: String,
    var taste: String,
    var consumerInput: List<String>,
    var cakeInput: List<String>
)
