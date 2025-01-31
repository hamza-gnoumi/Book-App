package com.gnam.book_network.feedback;


import com.gnam.book_network.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(feedbackService.save(request,connectedUser));
    }

    @GetMapping("/book/{book_id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBooks(
            @PathVariable("book_id") Integer bookId,
            @RequestParam(name = "page",defaultValue = "0",required = false)int page,
            @RequestParam(name = "size",defaultValue = "10",required = false)int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBooks(bookId,page,size,connectedUser));
    }
}
