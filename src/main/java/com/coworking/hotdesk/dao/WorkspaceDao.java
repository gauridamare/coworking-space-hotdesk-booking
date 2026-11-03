
package com.coworking.hotdesk.dao;

import java.time.LocalDate;
import java.util.List;

import com.coworking.hotdesk.entity.Workspace;
import com.coworking.hotdesk.rowmapper.WorkspaceRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WorkspaceDao {

    private final JdbcTemplate jdbcTemplate;

    public WorkspaceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all active workspaces
    public List<Workspace> getAllActiveWorkspaces() {

        String sql = "SELECT * FROM workspaces WHERE is_active = TRUE";

        return jdbcTemplate.query(sql, new WorkspaceRowMapper());
    }

    // Get workspace by ID
    public Workspace getWorkspaceById(Long spaceId) {

        String sql = "SELECT * FROM workspaces WHERE space_id = ?";

        List<Workspace> workspaces = jdbcTemplate.query(
                sql,
                new WorkspaceRowMapper(),
                spaceId
        );

        return workspaces.isEmpty() ? null : workspaces.get(0);
    }

    // Register a new workspace
    public int addWorkspace(Workspace workspace) {

        String sql = "INSERT INTO workspaces "
                + "(space_name, space_type, hourly_rate, is_active) "
                + "VALUES (?, ?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                workspace.getSpaceName(),
                workspace.getSpaceType(),
                workspace.getHourlyRate(),
                workspace.isActive()
        );
    }

    // Find workspaces available for a particular date and hour
    public List<Workspace> getAvailableWorkspaces(
            LocalDate bookingDate, int slotHour) {

        String sql = "SELECT w.* FROM workspaces w "
                + "WHERE w.is_active = TRUE "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM bookings b "
                + "WHERE b.space_id = w.space_id "
                + "AND b.booking_date = ? "
                + "AND b.slot_hour = ? "
                + "AND b.booking_status = 'CONFIRMED'"
                + ")";

        return jdbcTemplate.query(
                sql,
                new WorkspaceRowMapper(),
                bookingDate,
                slotHour
        );
    }
}
