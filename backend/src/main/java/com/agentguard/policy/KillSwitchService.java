package com.agentguard.policy;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class KillSwitchService {
    private final AtomicBoolean active = new AtomicBoolean(false);

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean state) {
        this.active.set(state);
    }

    public boolean toggle() {
        boolean newState = !active.get();
        active.set(newState);
        return newState;
    }
}
