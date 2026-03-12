package com.marcelo.fraud.controller.response;

public record AnalysisStatsResponse(Long totalAnalyses,
                                    Long totalApproved,
                                    Long totalRejected,
                                    Double approvalRate,
                                    Double averageRiskScore)
{}
