package com.gnam.book_network.feedback;

import lombok.Builder;

@Builder
public record FeedbackResponse(
        Double note,
        String comment,
        boolean ownFeedback
) {
}
