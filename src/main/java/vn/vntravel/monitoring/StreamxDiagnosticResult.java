package vn.vntravel.monitoring;

import java.util.List;
import java.util.Map;

public class StreamxDiagnosticResult {
    private final boolean success;
    private final boolean mandatoryFailed;
    private final List<Check> checks;

    public StreamxDiagnosticResult(List<Check> checks) {
        success = checks.stream().allMatch(Check::isSuccess);
        mandatoryFailed = checks.stream().anyMatch(check -> !check.success && check.mandatory);
        this.checks = checks;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isMandatoryFailed() {
        return mandatoryFailed;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public static class Check {
        private final String name;
        private final boolean success;
        private final boolean mandatory;
        private final String resource;
        private final Map<String, String> info;

        public Check(StreamxDiagnostic diagnostic, boolean success, Map<String, String> info) {
            this.name = diagnostic.getName();
            this.success = success;
            this.mandatory = diagnostic.isMandatory();
            this.resource = diagnostic.getResource();
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public String getResource() {
            return resource;
        }

        public Map<String, String> getInfo() {
            return info;
        }
    }
}
