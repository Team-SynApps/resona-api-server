package com.synapps.resona.comment.query.service;

import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import org.springframework.stereotype.Component;

@Component
public class CommentStatusCalculator {

  public CommentDisplayStatus determineCommentStatus(CommentDocument comment, ViewerContext context) {
    if (comment.isDeleted()) {
      return CommentDisplayStatus.DELETED;
    }
    if (context.blockedMemberIds().contains(comment.getAuthor().getMemberId())) {
      return CommentDisplayStatus.BLOCKED;
    }
    if (context.hiddenCommentIds().contains(comment.getCommentId())) {
      return CommentDisplayStatus.HIDDEN;
    }
    return CommentDisplayStatus.NORMAL;
  }

  public CommentDisplayStatus determineReplyStatus(ReplyEmbed reply, ViewerContext context) {
    if (reply.isDeleted()) {
      return CommentDisplayStatus.DELETED;
    }
    if (context.blockedMemberIds().contains(reply.getAuthor().getMemberId())) {
      return CommentDisplayStatus.BLOCKED;
    }
    if (context.hiddenReplyIds().contains(reply.getReplyId())) {
      return CommentDisplayStatus.HIDDEN;
    }
    return CommentDisplayStatus.NORMAL;
  }

  public String getDisplayContent(String originalContent, CommentDisplayStatus status) {
    if (status == CommentDisplayStatus.NORMAL) {
      return originalContent;
    }
    return status.getDescription();
  }
}