
package com.coworking.hotdesk.entity;

import java.math.BigDecimal;

public class Workspace {

    private Long spaceId;
    private String spaceName;
    private String spaceType;
    private BigDecimal hourlyRate;
    private boolean active;

    public Workspace() {
    }

    public Workspace(Long spaceId, String spaceName,
            String spaceType, BigDecimal hourlyRate,
            boolean active) {

        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.spaceType = spaceType;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
