package org.example.persistance;

import org.example.model.Charge;
import org.example.persistance.connection.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChargeRepo {

    public List<Charge> findAll(Integer customerId, String dbConnector) {

        try (
                Connection connection = DatabaseConnector.connect(dbConnector)) {
            if (connection == null) {
                System.err.println("[ERROR] Could not connect to database.");
                return List.of();
            }

            System.out.println("Connected to the PostgreSQL station database.");

            String query = "SELECT id, kwh, customer_id FROM charge WHERE customer_id = ?";

            PreparedStatement s = connection.prepareStatement(query);
            s.setInt(1, customerId);
            ResultSet resultSet = s.executeQuery();

            List<Charge> stations = new ArrayList<>();
            while (resultSet.next()) {
                stations.add(mapToCharge(resultSet));
            }

            connection.close();
            return stations;

        } catch (
                SQLException se) {
            se.printStackTrace();
            return List.of();
        }
    }

    private Charge mapToCharge(ResultSet resultSet) throws SQLException {
        final Integer chargeId = resultSet.getInt("id");
        final Integer userId = resultSet.getInt("customer_id");
        final String amount = resultSet.getString("kwh");
        return new Charge(
                chargeId,
                userId,
                amount
        );
    }
}
