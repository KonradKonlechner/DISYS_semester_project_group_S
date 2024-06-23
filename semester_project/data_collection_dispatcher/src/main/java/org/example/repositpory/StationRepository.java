package org.example.repositpory;

import org.example.model.Station;
import org.example.repositpory.connection.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StationRepository {

    public List<Station> findAllStations() {
        try (Connection connection = DatabaseConnector.connect()) {
            if (connection == null) {
                System.err.println("[ERROR] Could not connect to database.");
                return List.of();
            }

            System.out.println("Connected to the PostgreSQL station database.");

            String query = "SELECT id, db_url, lat, lng FROM station";

            PreparedStatement s = connection.prepareStatement(query);

            ResultSet resultSet = s.executeQuery();

            List<Station> stations = new ArrayList<>();
            while (resultSet.next()) {
                stations.add(mapToStation(resultSet));
            }

            connection.close();
            return stations;

        } catch (SQLException se) {
            se.printStackTrace();
            return List.of();
        }
    }

    private Station mapToStation(ResultSet resultSet) throws SQLException {
        final Integer stationId = resultSet.getInt("id");
        final String stationDbUrl = resultSet.getString("db_url");
        final String longitude = resultSet.getString("lng");
        final String latitude = resultSet.getString("lat");
        return new Station(
                stationId,
                stationDbUrl,
                longitude,
                latitude
        );
    }
}
