package com.example.springChat.controller;

import com.example.springChat.dto.ApiResponse;
import com.example.springChat.dto.ReportRequestDTO;
import com.example.springChat.service.ReportsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
@Slf4j
public class ReportController {
    private final ReportsService reportsService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> report(@RequestBody ReportRequestDTO dto,
                                                    @AuthenticationPrincipal UserDetails userDetails){
        reportsService.sendDiscordReport(dto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("신고가 완료 되었습니다."));
    }
}
