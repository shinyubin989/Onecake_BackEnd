package refresh.onecake.store.application

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import refresh.onecake.address.domain.Address
import refresh.onecake.address.domain.AddressRepository
import refresh.onecake.member.application.SecurityUtil
import refresh.onecake.member.domain.SellerRepository
import refresh.onecake.menu.adapter.infra.dto.NeighborhoodStore
import refresh.onecake.menu.adapter.infra.dto.StoreMenuListDto
import refresh.onecake.menu.domain.MenuRepository
import refresh.onecake.response.adapter.dto.DefaultResponseDto
import refresh.onecake.review.domain.ReviewRepository
import refresh.onecake.store.adapter.dto.*
import refresh.onecake.store.domain.Store
import refresh.onecake.store.domain.StoreRepository
import refresh.onecake.storelike.domain.StoreLikeRepository

@Service
class StoreService (
    private val storeRepository: StoreRepository,
    private val menuRepository: MenuRepository,
    private val modelMapper: ModelMapper,
    private val addressRepository: AddressRepository,
    private val storeLikeRepository: StoreLikeRepository,
    private val reviewRepository: ReviewRepository,
    private val sellerRepository: SellerRepository
){

    fun registerStore(applyStoreRequestDto: ApplyStoreRequestDto) : DefaultResponseDto {

        val id = SecurityUtil.getCurrentMemberId()
        if (storeRepository.existsById(id)) {
            return DefaultResponseDto(false, "이미 입점한 판매자 입니다.")
        }
        else {
            val address = Address(
                id = id,
                jibunAddress = applyStoreRequestDto.address?.jibunAddress,
                roadFullAddr = applyStoreRequestDto.address?.roadFullAddr,
                siNm = applyStoreRequestDto.address?.siNm,
                sggNm = applyStoreRequestDto.address?.sggNm,
                emdNm = applyStoreRequestDto.address?.emdNm,
                lnbrMnnm = applyStoreRequestDto.address?.lnbrMnnm,
                addressDetail = applyStoreRequestDto.address?.addressDetail
            )
            addressRepository.save(address)
            val store = Store(
                id = id,
                storeName = applyStoreRequestDto.storeName,
                businessRegistrationNumber = applyStoreRequestDto.businessRegistrationNumber,
                address = address,
                storePhoneNumber = applyStoreRequestDto.storePhoneNumber,
                storeDiscription = applyStoreRequestDto.storeDiscription,
                openTime = applyStoreRequestDto.openTime,
                closeTime = applyStoreRequestDto.closeTime,
                kakaoChannelUrl = applyStoreRequestDto.kakaoChannelUrl,
                storeImage = applyStoreRequestDto.storeImage,
                isActivated = true
            )
            storeRepository.save(store)
            val seller = sellerRepository.getById(id)
            seller.store = store
            sellerRepository.save(seller)
            return DefaultResponseDto(true, "입점 신청을 완료하였습니다.")
        }
    }

    fun storeMainInfo(storeId:Long): StoreMainInfoDto {
        val id = SecurityUtil.getCurrentMemberId()
        val store = storeRepository.getById(storeId)
        val address = addressRepository.getById(storeId)
        val temp = store.storeName.elementAt(store.storeName.length - 1)
        val index = (temp - 0xAC00.toChar()) % 28
        val description = if (temp < 0xAC00.toChar() || temp > 0xD7A3.toChar()) {
            address.sggNm + "에 위치한 " + store.storeName + "이에요."
        } else if (index > 0) {
            address.sggNm + "에 위치한 " + store.storeName + "이에요."
        } else {
            address.sggNm + "에 위치한 " + store.storeName + "에요."
        }

        return StoreMainInfoDto(
            storeImage = store.storeImage,
            storeName = store.storeName,
            storeDescription = description,
            likeNum = storeLikeRepository.countByStoreId(storeId),
            isLiked = storeLikeRepository.existsByMemberIdAndStoreId(id, storeId)
        )
    }

    fun storeMenuList(storeId: Long): List<StoreMenuListDto> {
        return menuRepository.findAllByStoreIdAndIsActivatedOrderByMenuNameAsc(storeId, true)
            .map { modelMapper.map(it, StoreMenuListDto::class.java) }
    }

    fun getStoreInformation(storeId: Long): StoreDetailInfoDto {
        val store = storeRepository.getById(storeId)
        return StoreDetailInfoDto(
            operatingTime = store.openTime + " ~ " + store.closeTime,
            dayOff = "주문 시 확인",
            address = addressRepository.getById(storeId).roadFullAddr.orEmpty(),
            storeDescription = store.storeDiscription
        )
    }

    fun getAllStoreByAddressAndFilter(addressAndFilter: AddressAndFilter): List<StoreThumbNail>? {
        val id = SecurityUtil.getCurrentMemberId()
        val addressId: List<Long>? = addressRepository.findAllBySggNm(addressAndFilter.address)?.map { it.id }
        val output: MutableList<StoreThumbNail> = mutableListOf()
        for (i in addressId?.indices!!) {
            val store = storeRepository.findByAddressIdAndIsActivated(addressId[i], true) ?: continue
            output.add(
                StoreThumbNail(
                    storeId = store.id,
                    storeImage = store.storeImage,
                    guName = addressRepository.getById(addressId[i]).sggNm!!,
                    storeName = store.storeName,
                    likedNum = storeLikeRepository.countByStoreId(store.id),
                    reviewNum = reviewRepository.countByStoreId(store.id),
                    isLiked = storeLikeRepository.existsByMemberIdAndStoreId(id, store.id)
                )
            )
        }
        if (addressAndFilter.filter == "review") {
            output.sortByDescending { it.reviewNum }
        } else {
            output.sortByDescending { it.likedNum }
        }
        return output
    }

    /*
        TO DO : N+1 해결
     */
    fun getNeighborhoodStore(): List<NeighborhoodStore> {
        val addresses = addressRepository.findAllBySggNm("마포구")
        val stores: MutableList<Store> = mutableListOf()
        for (i in addresses?.indices!!) {
            val store = storeRepository.findByAddressIdAndIsActivated(addresses[i].id, true)
            if(store != null) stores.add(store)
            if(stores.size >= 10) break
        }
        return stores.map { modelMapper.map(it, NeighborhoodStore::class.java) }
    }


    fun getSellerChatUrl(): String {
        val id = SecurityUtil.getCurrentMemberId()
        return storeRepository.getById(id).kakaoChannelUrl
    }
}