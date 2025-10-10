package com.synapps.resona.support.fixture;

import com.synapps.resona.comment.dto.CommentRequest;
import com.synapps.resona.comment.dto.ReplyRequest;
import java.util.Collections;

public class CommentFixture {

    public static final String COMMENT_CONTENT = "댓글 내용입니다.";
    public static final String REPLY_CONTENT = "답글 내용입니다.";
    public static final String LANGUAGE_CODE = "ko";

    public static CommentRequest createCommentRequest(Long feedId) {
        return new CommentRequest(feedId, COMMENT_CONTENT, LANGUAGE_CODE, Collections.emptyList());
    }

    public static ReplyRequest createReplyRequest(Long commentId) {
        return new ReplyRequest(commentId, REPLY_CONTENT, LANGUAGE_CODE, Collections.emptyList());
    }
}
