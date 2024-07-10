package com.synapps.atch.mysql.member.controller;


import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.mysql.member.dto.request.DuplicateIdRequest;
import com.synapps.atch.mysql.member.dto.request.SignupRequest;
import com.synapps.atch.mysql.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> join(@Valid @RequestBody SignupRequest request) throws Exception {
        ResponseDto response = new ResponseDto(true, List.of(memberService.signUp(request)));
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<?> getUser() {
        ResponseDto response = new ResponseDto(true, List.of(memberService.getMember()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/duplicate-id")
    public ResponseEntity<?> checkDuplicateId(@RequestBody DuplicateIdRequest request) throws Exception {
        ResponseDto response = new ResponseDto(true, List.of(memberService.checkDuplicateId(request)));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser() {
        ResponseDto response = new ResponseDto(true, List.of(memberService.deleteUser()));
        return ResponseEntity.ok(response);
    }
}