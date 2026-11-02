
package com.coworking.hotdesk.service;

import com.coworking.hotdesk.dao.WorkspaceDao;
import com.coworking.hotdesk.entity.Workspace;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkspaceService {

    private final WorkspaceDao workspaceDao;

    public WorkspaceService(WorkspaceDao workspaceDao) {
        this.workspaceDao = workspaceDao;
    }

    // Get all active workspaces
    public List<Workspace> getAllActiveWorkspaces() {
        return workspaceDao.getAllActiveWorkspaces();
    }

    // Get workspace by ID
    public Workspace getWorkspaceById(Long spaceId) {
        return workspaceDao.getWorkspaceById(spaceId);
    }

    // Register a new workspace
    public void addWorkspace(Workspace workspace) {
        workspaceDao.addWorkspace(workspace);
    }

    // Get available workspaces for a date and hour
    public List<Workspace> getAvailableWorkspaces(
            LocalDate bookingDate, int slotHour) {

        if (bookingDate == null) {
            throw new IllegalArgumentException("Booking date is required.");
        }

        if (bookingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past.");
        }

        if (slotHour < 0 || slotHour > 23) {
            throw new IllegalArgumentException(
                    "Slot hour must be between 0 and 23.");
        }

        return workspaceDao.getAvailableWorkspaces(
                bookingDate, slotHour);
    }
}
