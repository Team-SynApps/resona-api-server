package synapps.resona.api.socialMedia.restriction.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.socialMedia.code.SocialErrorCode;

public class BlockException extends BaseException {

  protected BlockException(String message, HttpStatus status,
      String errorCode) {
    super(message, status, errorCode);
  }

  private static BlockException of(SocialErrorCode code) {
    return new BlockException(code.getMessage(), code.getStatus(), code.getCustomCode());
  }

  public static BlockException cannotBlockSelf() {
    return of(SocialErrorCode.CANNOT_BLOCK_SELF);
  }

  public static BlockException alreadyBlocked() {
    return of(SocialErrorCode.ALREADY_BLOCKED);
  }

  public static BlockException notBlocked() {
    return of(SocialErrorCode.NOT_BLOCKED);
  }
}
