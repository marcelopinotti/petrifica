package com.marcelo.fraud.exception;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException(String analysisId) {
        super("Análise de fraude com ID " + analysisId + " não encontrada");
    }
}
