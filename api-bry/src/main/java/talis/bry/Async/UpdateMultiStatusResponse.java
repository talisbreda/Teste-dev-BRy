package talis.bry.Async;

import java.util.List;

public class UpdateMultiStatusResponse {
    private List<UserUpdateResult> results;

    public UpdateMultiStatusResponse(List<UserUpdateResult> results) {
        this.results = results;
    }

    public List<UserUpdateResult> getResults() {
        return results;
    }

    public void setResults(List<UserUpdateResult> results) {
        this.results = results;
    }

    public static class UserUpdateResult {
        private Long cpf;
        private Integer statusCode;
        private String message;

        public UserUpdateResult(Long cpf, Integer statusCode, String message) {
            this.cpf = cpf;
            this.statusCode = statusCode;
            this.message = message;
        }

        public Long getCpf() {
            return cpf;
        }

        public void setCpf(Long cpf) {
            this.cpf = cpf;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

