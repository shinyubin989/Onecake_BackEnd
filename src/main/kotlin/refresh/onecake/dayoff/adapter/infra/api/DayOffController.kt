package refresh.onecake.dayoff.adapter.infra.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import refresh.onecake.adapter.api.dto.DayOffDto
import refresh.onecake.response.adapter.dto.DefaultResponseDto
import refresh.onecake.dayoff.application.DayOffService
import refresh.onecake.response.adapter.api.ApiResponse

@RestController
@RequestMapping("/api/v1/")
class DayOffController (
    private val dayOffService: DayOffService
){

    @Operation(summary = "소비자 - 주문하기 - GET 가게의 휴무일 ")
    @GetMapping("consumer/stores/{storeId}/order/dayOff")
    fun getStoreDayOffs(@PathVariable storeId: Long): ResponseEntity<List<String>?> {
        return ApiResponse.success(
            HttpStatus.OK,
            dayOffService.getStoreDayOffs(storeId)
        )
    }




    @Operation(summary = "휴일 지정")
    @PostMapping("/seller/store/dayOff")
    fun setDayOff(@RequestBody dayOffDto: DayOffDto): ResponseEntity<DefaultResponseDto> {
        return ApiResponse.success(
            HttpStatus.OK,
            dayOffService.setDayOff(dayOffDto)
        )
    }

    @Operation(summary = "휴일 가져오기")
    @GetMapping("/seller/store/dayOff")
    fun getDayOff(): ResponseEntity<List<String>> {
        return ApiResponse.success(HttpStatus.OK, dayOffService.getDayOff())
    }


}