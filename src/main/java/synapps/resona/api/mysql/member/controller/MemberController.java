package synapps.resona.api.mysql.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.auth.DuplicateIdRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }



    // TODO: 커스텀 어노테이션으로 클래스 설정만 해줄 수 있게 하는 코드가 필요해보임
    @Operation(summary = "회원 등록", description = "회원 등록 후 응답 DTO 반환")
    @ApiResponse(
            responseCode = "200",
            description = "회원 등록 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberRegisterResponseDto.class)
            )
    )
    @PostMapping("/join")
    public ResponseEntity<?> join(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @Valid @RequestBody RegisterRequest registerRequest) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.signUp(registerRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUser(HttpServletRequest request,
                                     HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.getMember()));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getMemberDetailInfo(HttpServletRequest request,
                                                 HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.getMemberDetailInfo()));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/duplicate-id")
    public ResponseEntity<?> checkDuplicateId(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody DuplicateIdRequest duplicateIdRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.checkDuplicateId(duplicateIdRequest)));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestBody MemberPasswordChangeDto requestBody) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseDto = new ResponseDto(metaData, List.of(memberService.changePassword(request, requestBody)));
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(HttpServletRequest request,
                                        HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.deleteUser()));
        return ResponseEntity.ok(responseData);
    }

}