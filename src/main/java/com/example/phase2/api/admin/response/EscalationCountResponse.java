package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.EscalationType;
import java.io.Serializable;

public class EscalationCountResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private EscalationType escalationType;
    private int count;

    public EscalationCountResponse() {
    }

    public EscalationCountResponse(EscalationType escalationType, int count) {
        this.escalationType = escalationType;
        this.count = count;
    }

    public EscalationType getEscalationType() {
        return escalationType;
    }

    public void setEscalationType(EscalationType escalationType) {
        this.escalationType = escalationType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
