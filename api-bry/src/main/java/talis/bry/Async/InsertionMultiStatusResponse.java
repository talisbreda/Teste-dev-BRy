package talis.bry.Async;

import java.util.List;

public class InsertionMultiStatusResponse {
    private List<UserInsertionResult> results;

    public InsertionMultiStatusResponse(List<UserInsertionResult> results) {
        this.results = results;
    }

    public List<UserInsertionResult> getResults() {
        return results;
    }

    public void setResults(List<UserInsertionResult> results) {
        this.results = results;
    }

    public static class UserInsertionResult {
        private String cpf;
        private Integer status;
        private String message;

        public UserInsertionResult(String cpf, Integer status, String message) {
            this.cpf = cpf;
            this.status = status;
            this.message = message;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

