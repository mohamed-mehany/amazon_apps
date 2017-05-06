package commands.vendor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

public class AddProductCmd extends Command implements Runnable{

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		// TODO Auto-generated method stub
		CallableStatement sqlProc;
		
		String name;
		int vendor_id;
		String description;
		int department_id;
		int size;
		int stock;
		String colour;
		int price;
		String image_path;
		
		
try {
			name = ((String) mapUserData.get("name"));
			vendor_id = ((int) mapUserData.get("vendor_id"));
			description = ((String) mapUserData.get("description"));
			department_id = ((int) mapUserData.get("department_id"));
			size = ((int) mapUserData.get("size"));
			stock = ((int) mapUserData.get("stock"));
			colour = ((String) mapUserData.get("colour"));
			price = ((int) mapUserData.get("price"));
			image_path = ((String) mapUserData.get("image_path"));
			
			sqlProc = connection.prepareCall("{call add_product(?,?,?,?,?,?,?,?,?)}");
			sqlProc.setString(1, name);
			sqlProc.setInt(2, vendor_id);
			sqlProc.setString(3, description);
			sqlProc.setInt(4, department_id);
			sqlProc.setInt(5, size);
			sqlProc.setInt(6, stock);
			sqlProc.setString(7, colour);
			sqlProc.setDouble(8,price);
			sqlProc.setString(9, image_path);
			sqlProc.execute();
			
			
		
			sqlProc.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new StringBuffer("[").append("{\"res\":\"ok\"}").append("]");
		
	}

}
