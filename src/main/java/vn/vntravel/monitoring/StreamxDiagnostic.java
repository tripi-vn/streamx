package vn.vntravel.monitoring;

import java.util.concurrent.CompletableFuture;

public interface StreamxDiagnostic {

    String getName();

    boolean isMandatory();

    String getResource();

    CompletableFuture<StreamxDiagnosticResult.Check> check();
}
