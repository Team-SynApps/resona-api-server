package com.synapps.atch.mysql.member.exception;

import com.synapps.atch.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberException extends BaseException {
  public static final String MEMBER_NOT_FOUND = "MEM001";
  public static final String DUPLICATE_EMAIL = "MEM002";
  public static final String INVALID_PASSWORD = "MEM003";
  public static final String INVALID_TIMESTAMP = "MEM004";

  public MemberException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static MemberException memberNotFound() {
    return new MemberException("Member not found", HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND);
  }

  public static MemberException duplicateEmail() {
    return new MemberException("Email already exists", HttpStatus.CONFLICT, DUPLICATE_EMAIL);
  }

  public static MemberException invalidPassword() {
    return new MemberException("Invalid password", HttpStatus.BAD_REQUEST, INVALID_PASSWORD);
  }
  public static MemberException invalidTimeStamp(){
    return new MemberException("Invalid time", HttpStatus.BAD_REQUEST, INVALID_TIMESTAMP);
  }
}