package com.coworking.hotdesk.rowmapper;

import com.coworking.hotdesk.entity.Workspace;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkspaceRowMapper implements RowMapper<Workspace> {

    @Override
    public Workspace mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Workspace workspace = new Workspace();

        workspace.setSpaceId(rs.getLong("space_id"));
        workspace.setSpaceName(rs.getString("space_name"));
        workspace.setSpaceType(rs.getString("space_type"));
        workspace.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        workspace.setActive(rs.getBoolean("is_active"));

        return workspace;
    }
}