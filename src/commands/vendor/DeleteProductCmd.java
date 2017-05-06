package commands.vendor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

public class DeleteProductCmd extends Command implements Runnable{

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		// TODO Auto-generated method stub
		CallableStatement sqlProc;
		int productID;
		
		try {
			
			productID = ((Integer) mapUserData.get("product_id"));
			
			sqlProc = connection.prepareCall("{call delete_product(?)}");
			sqlProc.setInt(1, productID);
			sqlProc.execute();
			
			
		
			sqlProc.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new StringBuffer("[").append("{\"res\":\"ok\"}").append("]");
		
	}

}
