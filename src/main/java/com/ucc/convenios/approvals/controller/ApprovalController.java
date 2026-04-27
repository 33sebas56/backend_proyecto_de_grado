package com.ucc.convenios.approvals.controller;

import com.ucc.convenios.approvals.dto.ApprovalDecisionRequest;
import com.ucc.convenios.approvals.dto.ApprovalRoundResponse;
import com.ucc.convenios.approvals.dto.ApprovalStepResponse;
import com.ucc.convenios.approvals.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/my-pending")
    public List<ApprovalStepResponse> getMyPendingApprovals(Authentication authentication) {
        return approvalService.getMyPendingApprovals(authentication);
    }

    @GetMapping("/convenios/{convenioId}/rounds")
    public List<ApprovalRoundResponse> getRoundsByConvenio(@PathVariable UUID convenioId) {
        return approvalService.getRoundsByConvenio(convenioId);
    }

    @GetMapping("/rounds/{roundId}/steps")
    public List<ApprovalStepResponse> getStepsByRound(@PathVariable UUID roundId) {
        return approvalService.getStepsByRound(roundId);
    }

    @PostMapping("/{stepId}/approve")
    public ApprovalStepResponse approveStep(
            @PathVariable UUID stepId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return approvalService.approveStep(stepId, request, authentication);
    }

    @PostMapping("/{stepId}/request-correction")
    public ApprovalStepResponse requestCorrection(
            @PathVariable UUID stepId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return approvalService.requestCorrection(stepId, request, authentication);
    }

    @PostMapping("/{stepId}/reject")
    public ApprovalStepResponse rejectStep(
            @PathVariable UUID stepId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return approvalService.rejectStep(stepId, request, authentication);
    }
}