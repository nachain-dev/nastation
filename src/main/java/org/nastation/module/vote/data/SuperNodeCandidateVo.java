package org.nastation.module.vote.data;

public class SuperNodeCandidateVo extends SuperNodeCandidate {

    private boolean isSubmitPrivateKey;

    public SuperNodeCandidateVo() {
    }

    public boolean isSubmitPrivateKey() {
        return isSubmitPrivateKey;
    }

    public void setSubmitPrivateKey(boolean submitPrivateKey) {
        isSubmitPrivateKey = submitPrivateKey;
    }
}
