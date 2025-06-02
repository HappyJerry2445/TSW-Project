package com.cardhaven.cardhaven.model.dao;
import com.cardhaven.cardhaven.model.dto.AttributeDefinitionDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class AttributeDefinitionDAO implements GenericDAO<AttributeDefinitionDTO, Integer> {

	private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
		"AttributeID", "AttributeName", "DataType", "ApplicableTo"
	);

	private static final String DEFAULT_ORDER_COLUMN = "AttributeID";

	private final DataSource dataSource;

	public AttributeDefinitionDAO(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null");
	}

	@Override
	public void save(AttributeDefinitionDTO attributeDefinitionDTO) throws SQLException {
		if (attributeDefinitionDTO == null || attributeDefinitionDTO.getAttributeName() ==null ||
				attributeDefinitionDTO.getAttributeName().trim().isEmpty() ||
				attributeDefinitionDTO.getDataType() == null || attributeDefinitionDTO.getApplicableTo() == null){
			throw new IllegalArgumentException("Attribute, AttributeName, DataType or ApplicableTo cannot be null or empty");
		}

		String sql;
		if(attributeDefinitionDTO.getAttributeId() == 0){
			sql = "INSERT INTO AttributeDefinition ( AttributeName, DataType, ApplicableTo) VALUES ( ?, ?, ?)";
			try (Connection connection = dataSource.getConnection();
				 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				ps.setString(1, attributeDefinitionDTO.getAttributeName());
				ps.setString(2, attributeDefinitionDTO.getDataType().name());
				ps.setString(3, attributeDefinitionDTO.getApplicableTo().name());

				int affectedRows = ps.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Creating AttributeDefinition failed, no rows affected");
				}


				try (ResultSet generatedKeys = ps.getGeneratedKeys()){
					if (generatedKeys.next()) {
						attributeDefinitionDTO.setAttributeId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating AttributeDefinition failed, no ID obtained");
					}
				}


			}
		} else{
			sql= "UPDATE AttributeDefinition SET AttributeName = ?, DataType = ?, ApplicableTo = ? WHERE AttributeID = ?";
			try (Connection connection = dataSource.getConnection();
				 PreparedStatement ps = connection.prepareStatement(sql)){
					 ps.setString(1, attributeDefinitionDTO.getAttributeName());
					 ps.setString(2, attributeDefinitionDTO.getDataType().name());
					 ps.setString(3, attributeDefinitionDTO.getApplicableTo().name());
					 ps.setInt(4, attributeDefinitionDTO.getAttributeId());

					 ps.executeUpdate();
			}
		}
	}

	@Override
	public boolean delete(Integer id) throws SQLException {
		if(id == null || id == 0){
			throw new IllegalArgumentException("ID cannot be null or 0 for deletion");
		}

		String sql = "DELETE FROM AttributeDefinition WHERE AttributeID = ?";
		try (Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, id);

			int affectedRows = ps.executeUpdate();
			return affectedRows > 0;
		}
	}

	@Override
	public AttributeDefinitionDTO getById(Integer attributeID) throws SQLException {
		if (attributeID == null || attributeID <= 0){
			throw new IllegalArgumentException("AttributeID cannot be null or less than 1");
		}

		String sql = "SELECT AttributeID, AttributeName, DataType, ApplicableTo FROM AttributeDefinition WHERE AttributeID = ?";
		AttributeDefinitionDTO attributeDefinitionDTO = null;
		try (Connection connection = dataSource.getConnection();
		PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, attributeID);
			try (ResultSet rs = ps.executeQuery()){
				if (rs.next()){
					attributeDefinitionDTO = extractAttributeFromResultSet(rs);
				}
			}
		}
		return attributeDefinitionDTO;
	}

	public Collection<AttributeDefinitionDTO> getAll(String order) throws SQLException {
		Collection<AttributeDefinitionDTO> attributeDefinitionDTOS = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT AttributeID, AttributeName, DataType, ApplicableTo FROM AttributeDefinition");

		String actualOrderColumn = DEFAULT_ORDER_COLUMN;
		if(order != null && !order.trim().isEmpty()){
			String trimmedOrder = order.trim();
			if(ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)){
				actualOrderColumn = trimmedOrder;
			} else {
				System.err.println("Warning: Attempted to order by invalid column: '\" + order + \"'. Falling back to default order.");
			}
		}
		sql.append("ORDER BY ").append(actualOrderColumn);

		try(Connection connection = dataSource.getConnection();
		PreparedStatement ps = connection.prepareStatement(sql.toString());
		ResultSet rs = ps.executeQuery()){
			while (rs.next()){
				attributeDefinitionDTOS.add(extractAttributeFromResultSet(rs));
			}
		}
		return attributeDefinitionDTOS;
	}

	public List<String> getAllowedOrderColumns(){
		return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
	}

	private AttributeDefinitionDTO extractAttributeFromResultSet(ResultSet rs) throws SQLException {
		AttributeDefinitionDTO attributeDefinitionDTO = new AttributeDefinitionDTO();
		attributeDefinitionDTO.setAttributeId(rs.getInt("AttributeID"));
		attributeDefinitionDTO.setAttributeName(rs.getString("AttributeName"));
		attributeDefinitionDTO.setApplicableTo(AttributeDefinitionDTO.ApplicableTo.valueOf(rs.getString("ApplicableTo")));
		attributeDefinitionDTO.setDataType(AttributeDefinitionDTO.DataType.valueOf(rs.getString("DataType")));
		return attributeDefinitionDTO;
	}
}
