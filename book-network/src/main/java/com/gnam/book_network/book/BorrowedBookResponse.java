package com.gnam.book_network.book;


import lombok.Builder;

@Builder
public record BorrowedBookResponse(
        Integer id,
        String title,
        String authorName,
        String isbn,
        byte[] cover,
        double rate,
        boolean returned,
        boolean returnApproved
) {
}
